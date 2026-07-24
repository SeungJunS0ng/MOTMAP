// ═══════════════════════════════════════════
// MOTMAP — Main Application Controller
// 초기화, 인증, 다크모드, 토스트, 확인 모달
// ═══════════════════════════════════════════

// ════════════════════════════════
// TOAST NOTIFICATION SYSTEM
// ════════════════════════════════

let isGuestMode = false;

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function showToast(message, type = 'info', duration = 3500) {
    const container = document.getElementById('toastContainer');

    // Remove any existing duplicate toast with same message to prevent stacking
    const existingToasts = container.querySelectorAll('.toast');
    existingToasts.forEach(t => {
        if (t.dataset.message === message) {
            t.remove();
        }
    });

    const icons = {
        success: '✅', error: '❌', warning: '⚠️', info: 'ℹ️'
    };

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.dataset.message = message;
    toast.style.cursor = 'pointer';
    toast.title = '클릭하여 닫기';
    toast.innerHTML = `
        <span class="toast-icon">${icons[type] || icons.info}</span>
        <span style="flex:1; line-height:1.35;">${escapeHtml(message)}</span>
        <span style="font-size:0.75rem; opacity:0.6; padding-left:8px;">✕</span>
    `;

    const removeSelf = () => {
        if (!toast.classList.contains('toast-out')) {
            toast.classList.add('toast-out');
            setTimeout(() => toast.remove(), 250);
        }
    };

    toast.addEventListener('click', removeSelf);

    container.appendChild(toast);

    if (duration > 0) {
        setTimeout(removeSelf, duration);
    }

    return toast;
}

function removeToast(toastEl) {
    if (toastEl && toastEl.parentNode) {
        toastEl.classList.add('toast-out');
        setTimeout(() => toastEl.remove(), 250);
    }
}

// ════════════════════════════════
// CONFIRM MODAL
// ════════════════════════════════

let _confirmCallback = null;

function showConfirm(title, message, onConfirm) {
    _confirmCallback = onConfirm;
    document.getElementById('confirmTitle').textContent = title;
    document.getElementById('confirmMessage').textContent = message;
    document.getElementById('confirmModal').classList.remove('hidden');
}

function hideConfirm() {
    document.getElementById('confirmModal').classList.add('hidden');
    _confirmCallback = null;
}

// ════════════════════════════════
// DARK MODE
// ════════════════════════════════

function initTheme() {
    const saved = localStorage.getItem('motmap_theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    const theme = saved || (prefersDark ? 'dark' : 'light');
    applyTheme(theme);
}

function applyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('motmap_theme', theme);
    const btn = document.getElementById('themeToggle');
    if (btn) btn.textContent = theme === 'dark' ? '☀️' : '🌙';
}

function toggleTheme() {
    const current = document.documentElement.getAttribute('data-theme');
    applyTheme(current === 'dark' ? 'light' : 'dark');
}

// ════════════════════════════════
// AUTH
// ════════════════════════════════

function showAuthModal() {
    document.getElementById('authModal').classList.remove('hidden');
}

function hideAuthModal() {
    document.getElementById('authModal').classList.add('hidden');
}

function updateUserUI() {
    const username = apiService.getUsername();
    const nickname = apiService.getNickname();
    const displayName = nickname || username;

    document.getElementById('usernameDisplay').textContent = displayName;
    document.getElementById('userAvatar').textContent = displayName.charAt(0).toUpperCase();
    document.getElementById('userBadge').classList.remove('hidden');
    document.getElementById('logoutBtn').classList.remove('hidden');
}

async function handleLogin() {
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;
    const errorEl = document.getElementById('authError');

    if (!username || !password) {
        errorEl.textContent = '아이디와 비밀번호를 입력해주세요';
        errorEl.style.display = 'block';
        return;
    }

    try {
        await apiService.login(username, password);
        hideAuthModal();
        updateUserUI();
        showToast(`${apiService.getNickname() || username}님 환영합니다! 👋`, 'success');
        initApp();
    } catch (error) {
        errorEl.textContent = error.message;
        errorEl.style.display = 'block';
    }
}

function clearSignupErrors() {
    const errorEl = document.getElementById('authError');
    errorEl.style.display = 'none';
    errorEl.textContent = '';

    ['Username', 'Email', 'Nickname', 'Password'].forEach(field => {
        const input = document.getElementById(`signup${field}`);
        const errSpan = document.getElementById(`error${field}`);
        if (input) input.classList.remove('invalid');
        if (errSpan) {
            errSpan.textContent = '';
            errSpan.classList.remove('show');
        }
    });
}

function showFieldError(fieldKey, message) {
    // Map backend field key (or frontend name) to DOM elements
    const keyCap = fieldKey.charAt(0).toUpperCase() + fieldKey.slice(1);
    const input = document.getElementById(`signup${keyCap}`);
    const errSpan = document.getElementById(`error${keyCap}`);

    if (input) input.classList.add('invalid');
    if (errSpan) {
        errSpan.textContent = `⚠️ ${message}`;
        errSpan.classList.add('show');
    }
}

async function handleSignup() {
    clearSignupErrors();

    const username = document.getElementById('signupUsername').value.trim();
    const email = document.getElementById('signupEmail').value.trim();
    const nickname = document.getElementById('signupNickname').value.trim();
    const password = document.getElementById('signupPassword').value;
    const errorEl = document.getElementById('authError');

    let hasClientError = false;

    if (!username) {
        showFieldError('username', '아이디를 입력해주세요 (3자 이상)');
        hasClientError = true;
    } else if (username.length < 3) {
        showFieldError('username', '아이디는 최소 3자 이상이어야 합니다');
        hasClientError = true;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email) {
        showFieldError('email', '이메일을 입력해주세요');
        hasClientError = true;
    } else if (!emailRegex.test(email)) {
        showFieldError('email', '올바른 이메일 형식이 아닙니다 (예: name@domain.com)');
        hasClientError = true;
    }

    if (!nickname) {
        showFieldError('nickname', '닉네임을 입력해주세요 (2자 이상)');
        hasClientError = true;
    } else if (nickname.length < 2) {
        showFieldError('nickname', '닉네임은 2자 이상이어야 합니다');
        hasClientError = true;
    }

    if (!password) {
        showFieldError('password', '비밀번호를 입력해주세요 (4자 이상)');
        hasClientError = true;
    } else if (password.length < 4) {
        showFieldError('password', '비밀번호는 최소 4자 이상이어야 합니다');
        hasClientError = true;
    }

    if (hasClientError) return;

    try {
        await apiService.signup(username, email, password, nickname);
        showToast('회원가입이 완료되었습니다! 로그인해주세요.', 'success');

        // Switch to login tab
        document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
        document.querySelector('[data-tab="login"]').classList.add('active');
        document.getElementById('loginForm').classList.add('active');
        document.getElementById('signupForm').classList.remove('active');
        document.getElementById('loginUsername').value = username;
        clearSignupErrors();
    } catch (error) {
        if (error.fieldErrors && Object.keys(error.fieldErrors).length > 0) {
            Object.entries(error.fieldErrors).forEach(([field, msg]) => {
                showFieldError(field, msg);
            });
        } else {
            errorEl.textContent = error.message || '회원가입 실패';
            errorEl.style.display = 'block';
        }
    }
}

function handleLogout() {
    showConfirm('로그아웃', '정말 로그아웃하시겠습니까?', () => {
        apiService.clearAuth();
        location.reload();
    });
}

function debounce(func, wait = 300) {
    let timeout;
    return function (...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), wait);
    };
}

// ════════════════════════════════
// APP INITIALIZATION
// ════════════════════════════════

function initApp() {
    if (typeof kakao === 'undefined' || !kakao.maps) {
        showToast('카카오맵 API를 불러올 수 없습니다. 페이지를 새로고침해주세요.', 'error');
        return;
    }

    kakao.maps.load(() => {
        try {
            mapManager = new KakaoMapManager();
            restaurantUI = new RestaurantUI();
            console.log('✅ MOTMAP initialized successfully (Kakao Maps Async)');
        } catch (error) {
            console.error('Init error:', error);
            showToast('애플리케이션 초기화 중 오류가 발생했습니다', 'error');
        }
    });
}

// ════════════════════════════════
// PWA & SERVICE WORKER
// ════════════════════════════════
if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker.register('/service-worker.js').then(reg => {
            console.log('✅ PWA ServiceWorker registered:', reg.scope);
        }).catch(err => {
            console.log('PWA ServiceWorker registration failed:', err);
        });
    });
}

let deferredPrompt;
window.addEventListener('beforeinstallprompt', (e) => {
    e.preventDefault();
    deferredPrompt = e;
    const pwaBtn = document.getElementById('pwaInstallBtn');
    if (pwaBtn) pwaBtn.classList.remove('hidden');
});

document.addEventListener('DOMContentLoaded', () => {
    // PWA install button listener
    const pwaBtn = document.getElementById('pwaInstallBtn');
    if (pwaBtn) {
        pwaBtn.addEventListener('click', async () => {
            if (deferredPrompt) {
                deferredPrompt.prompt();
                const { outcome } = await deferredPrompt.userChoice;
                if (outcome === 'accepted') {
                    showToast('MOTMAP 앱이 홈 화면에 설치되었습니다! 📲', 'success');
                }
                deferredPrompt = null;
                pwaBtn.classList.add('hidden');
            } else {
                showToast('모바일 브라우저 메뉴 [홈 화면에 추가]를 눌러 앱으로 설치할 수 있습니다 📲', 'info');
            }
        });
    // Bottom Sheet Drag Handle Interaction for Mobile
    const bottomSheetHandle = document.getElementById('bottomSheetHandle');
    const sidebar = document.getElementById('sidebar');
    if (bottomSheetHandle && sidebar) {
        let startY = 0;
        let isDragging = false;

        bottomSheetHandle.addEventListener('click', () => {
            sidebar.classList.toggle('expanded');
        });

        bottomSheetHandle.addEventListener('touchstart', (e) => {
            startY = e.touches[0].clientY;
            isDragging = true;
        }, { passive: true });

        bottomSheetHandle.addEventListener('touchmove', (e) => {
            if (!isDragging) return;
            const currentY = e.touches[0].clientY;
            const diffY = currentY - startY;
            if (diffY < -25) {
                sidebar.classList.add('expanded');
            } else if (diffY > 25) {
                sidebar.classList.remove('expanded');
            }
        }, { passive: true });

        bottomSheetHandle.addEventListener('touchend', () => {
            isDragging = false;
        });
    }

    // Theme
    initTheme();

    // Theme toggle
    document.getElementById('themeToggle').addEventListener('click', toggleTheme);

    // Auth tabs
    document.querySelectorAll('.auth-tab').forEach(tab => {
        tab.addEventListener('click', () => {
            const target = tab.dataset.tab;
            document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            document.getElementById('loginForm').classList.toggle('active', target === 'login');
            document.getElementById('signupForm').classList.toggle('active', target === 'signup');
            clearSignupErrors();
        });
    });

    // Login Header button
    const loginHeaderBtn = document.getElementById('loginHeaderBtn');
    if (loginHeaderBtn) {
        loginHeaderBtn.addEventListener('click', showAuthModal);
    }

    // Guest Browse button
    const guestBrowseBtn = document.getElementById('guestBrowseBtn');
    if (guestBrowseBtn) {
        guestBrowseBtn.addEventListener('click', () => {
            hideAuthModal();
            isGuestMode = true;
            document.getElementById('loginHeaderBtn')?.classList.remove('hidden');
            document.getElementById('logoutBtn')?.classList.add('hidden');
            initApp();
            showToast('둘러보기 모드로 접속했습니다 👁️ (등록 시 로그인 필요)', 'info');
        });
    }

    // Real-time error clearing on input
    ['Username', 'Email', 'Nickname', 'Password'].forEach(field => {
        const input = document.getElementById(`signup${field}`);
        if (input) {
            input.addEventListener('input', () => {
                input.classList.remove('invalid');
                const errSpan = document.getElementById(`error${field}`);
                if (errSpan) {
                    errSpan.textContent = '';
                    errSpan.classList.remove('show');
                }
            });
        }
    });

    // Login button
    document.getElementById('loginBtn').addEventListener('click', handleLogin);
    document.getElementById('loginPassword').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') handleLogin();
    });
    document.getElementById('loginUsername').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') handleLogin();
    });

    // Signup button
    document.getElementById('signupBtn').addEventListener('click', handleSignup);
    document.getElementById('signupPassword').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') handleSignup();
    });

    // Logout button
    document.getElementById('logoutBtn').addEventListener('click', handleLogout);

    // Password toggles
    document.querySelectorAll('.password-toggle').forEach(btn => {
        btn.addEventListener('click', () => {
            const target = document.getElementById(btn.dataset.target);
            const isPassword = target.type === 'password';
            target.type = isPassword ? 'text' : 'password';
            btn.textContent = isPassword ? '🙈' : '👁️';
        });
    });

    // Confirm modal buttons
    document.getElementById('confirmOkBtn').addEventListener('click', () => {
        if (_confirmCallback) _confirmCallback();
        hideConfirm();
    });
    document.getElementById('confirmCancelBtn').addEventListener('click', hideConfirm);

    // Detail modal close on overlay click
    document.getElementById('detailModal').addEventListener('click', (e) => {
        if (e.target === document.getElementById('detailModal')) {
            restaurantUI?.hideDetail();
        }
    });

    // Confirm modal close on overlay click
    document.getElementById('confirmModal').addEventListener('click', (e) => {
        if (e.target === document.getElementById('confirmModal')) {
            hideConfirm();
        }
    });

    // Global ESC key listener to close modals
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            hideConfirm();
            restaurantUI?.hideDetail();
            if (mapManager) mapManager.closeOverlay();
        }
    });

    // Sidebar toggle (mobile)
    const sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', () => {
            document.getElementById('sidebar').classList.toggle('sidebar-open');
        });
    }

    // ── Check Auth & Start ──
    if (apiService.isLoggedIn()) {
        updateUserUI();
        hideAuthModal();
        initApp();
    } else {
        showAuthModal();
    }
});
