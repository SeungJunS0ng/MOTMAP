// ═══════════════════════════════════════════
// MOTMAP — API Service Module
// JWT 인증 + REST API 통신
// ═══════════════════════════════════════════

class ApiService {
    constructor() {
        this.baseUrl = '/api/restaurants';
        this.authUrl = '/api/auth';
    }

    // ── Auth Headers ──
    getHeaders() {
        const headers = { 'Content-Type': 'application/json' };
        const token = localStorage.getItem('motmap_token');
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        return headers;
    }

    // ── Generic Request Wrapper ──
    async request(url, options = {}) {
        try {
            const response = await fetch(url, {
                headers: this.getHeaders(),
                ...options
            });

            // Token expired or invalid → auto logout
            if (response.status === 401 || response.status === 403) {
                const currentPath = window.location.pathname;
                if (currentPath !== '/login') {
                    this.clearAuth();
                    if (typeof showToast === 'function') {
                        showToast('세션이 만료되었습니다. 다시 로그인해주세요.', 'warning');
                    }
                    setTimeout(() => location.reload(), 1200);
                }
                throw new Error('인증이 필요합니다');
            }

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `오류가 발생했습니다 (${response.status})`);
            }

            if (response.status === 204) return null;
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // ════════════════════════════════
    // AUTH API
    // ════════════════════════════════

    async login(username, password) {
        const response = await fetch(`${this.authUrl}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({}));
            throw new Error(err.message || '아이디 또는 비밀번호가 올바르지 않습니다');
        }

        const data = await response.json();
        this.saveAuth(data);
        return data;
    }

    async signup(username, email, password, nickname) {
        const response = await fetch(`${this.authUrl}/signup`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password, nickname })
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({}));
            const errorObj = new Error(err.message || '회원가입에 실패했습니다');
            errorObj.fieldErrors = err.fieldErrors || {};
            throw errorObj;
        }

        return await response.json();
    }

    saveAuth(data) {
        localStorage.setItem('motmap_token', data.token);
        localStorage.setItem('motmap_username', data.username || '');
        localStorage.setItem('motmap_nickname', data.nickname || data.username || '');
    }

    clearAuth() {
        localStorage.removeItem('motmap_token');
        localStorage.removeItem('motmap_username');
        localStorage.removeItem('motmap_nickname');
    }

    isLoggedIn() {
        return !!localStorage.getItem('motmap_token');
    }

    getUsername() {
        return localStorage.getItem('motmap_username') || '';
    }

    getNickname() {
        return localStorage.getItem('motmap_nickname') || '';
    }

    // ════════════════════════════════
    // RESTAURANT API
    // ════════════════════════════════

    async getAllRestaurants() {
        return this.request(this.baseUrl);
    }

    async getRestaurantById(id) {
        return this.request(`${this.baseUrl}/${id}`);
    }

    async addRestaurant(data) {
        return this.request(this.baseUrl, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async updateRestaurant(id, data) {
        return this.request(`${this.baseUrl}/${id}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    async deleteRestaurant(id) {
        await this.request(`${this.baseUrl}/${id}`, { method: 'DELETE' });
        return true;
    }

    async getRestaurantsByCategory(category) {
        return this.request(`${this.baseUrl}/category/${category}`);
    }

    async searchRestaurants(keyword) {
        return this.request(`${this.baseUrl}/search?keyword=${encodeURIComponent(keyword)}`);
    }

    async getRestaurantsByRating() {
        return this.request(`${this.baseUrl}/sorted/rating`);
    }

    async getRestaurantsByDate() {
        return this.request(`${this.baseUrl}/sorted/date`);
    }

    async getHighRatedRestaurants() {
        return this.request(`${this.baseUrl}/high-rated`);
    }

    async getNearbyRestaurants(lat, lng, radius = 3) {
        return this.request(`${this.baseUrl}/nearby?latitude=${lat}&longitude=${lng}&radius=${radius}`);
    }

    async getRestaurantsByUsername(username) {
        return this.request(`${this.baseUrl}/user/${encodeURIComponent(username)}`);
    }

    async kakaoLogin(kakaoData) {
        const res = await this.request(`${this.authUrl}/kakao`, {
            method: 'POST',
            body: JSON.stringify(kakaoData)
        });
        if (res.token) {
            localStorage.setItem('motmap_token', res.token);
            localStorage.setItem('motmap_username', res.username);
            localStorage.setItem('motmap_nickname', res.nickname);
        }
        return res;
    }

    async getAiCourse(restaurantId = null, lat = null, lng = null) {
        let query = [];
        if (restaurantId) query.push(`restaurantId=${restaurantId}`);
        if (lat) query.push(`latitude=${lat}`);
        if (lng) query.push(`longitude=${lng}`);
        const qStr = query.length > 0 ? `?${query.join('&')}` : '';
        return this.request(`${this.baseUrl}/ai-course${qStr}`);
    }
}

// Global instance
const apiService = new ApiService();
