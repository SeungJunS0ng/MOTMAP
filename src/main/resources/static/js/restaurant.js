// ═══════════════════════════════════════════
// MOTMAP — Restaurant UI Manager
// 카드 렌더링, 상세보기, CRUD, 필터링, 거리 계산, 주소 검색
// ═══════════════════════════════════════════

class RestaurantUI {
    constructor() {
        this.selectedRating = 5;
        this.currentRestaurants = [];
        this.editingId = null;
        this.activeChip = null;
        this.init();
    }

    async init() {
        this.initEventListeners();
        this.initStarRating();
        await this.loadAndRenderRestaurants();
    }

    // ════════════════════════════════
    // EVENT LISTENERS
    // ════════════════════════════════

    initEventListeners() {
        // Search
        const debouncedSearch = typeof debounce === 'function' ? debounce(() => this.handleSearch(), 350) : null;
        document.getElementById('searchBtn').addEventListener('click', () => this.handleSearch());
        document.getElementById('searchInput').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.handleSearch();
        });
        if (debouncedSearch) {
            document.getElementById('searchInput').addEventListener('input', debouncedSearch);
        }

        // Address search button in form
        const addressSearchBtn = document.getElementById('addressSearchBtn');
        if (addressSearchBtn) {
            addressSearchBtn.addEventListener('click', () => this.handleAddressSearch());
        }

        // Address input Enter key
        document.getElementById('restaurantAddress').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                this.handleAddressSearch();
            }
        });

        // Filters
        document.getElementById('categoryFilter').addEventListener('change', () => this.handleFilter());
        document.getElementById('sortOrder').addEventListener('change', () => this.handleFilter());

        // Chips
        document.getElementById('chipHighRated').addEventListener('click', () => this.toggleChip('highRated'));
        document.getElementById('chipNearby').addEventListener('click', () => this.toggleChip('nearby'));
        const chipSetLocation = document.getElementById('chipSetLocation');
        if (chipSetLocation) {
            chipSetLocation.addEventListener('click', () => {
                if (mapManager) mapManager.promptSetMyLocation();
            });
        }
        document.getElementById('chipReset').addEventListener('click', () => this.resetFilters());

        // Review character counter
        const reviewTextarea = document.getElementById('restaurantReview');
        const charCounter = document.getElementById('reviewCharCounter');
        if (reviewTextarea && charCounter) {
            reviewTextarea.addEventListener('input', () => {
                const len = reviewTextarea.value.length;
                charCounter.textContent = `${len} / 500자`;
                charCounter.style.color = len >= 480 ? '#EF4444' : 'var(--text-tertiary)';
            });
        }

        // Form
        document.getElementById('addRestaurantForm').addEventListener('submit', (e) => this.handleSaveRestaurant(e));
        document.getElementById('cancelFormBtn').addEventListener('click', () => this.hideForm());

        // Detail modal
        document.getElementById('detailCloseBtn').addEventListener('click', () => this.hideDetail());
        document.getElementById('detailMapBtn').addEventListener('click', () => this.detailGoToMap());
        const detailNaviBtn = document.getElementById('detailNaviBtn');
        if (detailNaviBtn) {
            detailNaviBtn.addEventListener('click', () => {
                if (this._detailRestaurant && mapManager) {
                    mapManager.openDirections(
                        this._detailRestaurant.name,
                        this._detailRestaurant.latitude,
                        this._detailRestaurant.longitude
                    );
                }
            });
        }

        // Share Map Link button
        const shareMapBtn = document.getElementById('shareMapBtn');
        if (shareMapBtn) {
            shareMapBtn.addEventListener('click', () => {
                const currentUser = apiService.getUsername();
                if (!currentUser && isGuestMode) {
                    showToast('지도를 공유하려면 로그인이 필요합니다 🔒', 'warning');
                    showAuthModal();
                    return;
                }
                const shareUrl = `${window.location.origin}/?user=${encodeURIComponent(currentUser || 'admin')}`;
                if (navigator.clipboard) {
                    navigator.clipboard.writeText(shareUrl);
                    showToast(`🔗 나만의 맛집 지도 공유 링크가 클립보드에 복사되었습니다!\n(${shareUrl})`, 'success');
                } else {
                    prompt('아래 공유 링크를 복사하세요:', shareUrl);
                }
            });
        }

        // Reset Share View button
        const resetShareViewBtn = document.getElementById('resetShareViewBtn');
        if (resetShareViewBtn) {
            resetShareViewBtn.addEventListener('click', () => {
                window.history.pushState({}, '', '/');
                document.getElementById('shareBannerContainer')?.classList.add('hidden');
                this.loadAndRenderRestaurants();
            });
        }

        // Photo upload / URL listeners
        const uploadFileBtn = document.getElementById('uploadFileBtn');
        const restaurantFile = document.getElementById('restaurantFile');
        const restaurantImage = document.getElementById('restaurantImage');
        const imagePreviewContainer = document.getElementById('imagePreviewContainer');
        const imagePreview = document.getElementById('imagePreview');
        const removeImageBtn = document.getElementById('removeImageBtn');

        if (uploadFileBtn && restaurantFile) {
            uploadFileBtn.addEventListener('click', () => restaurantFile.click());
            restaurantFile.addEventListener('change', (e) => {
                const file = e.target.files[0];
                if (file) {
                    if (file.size > 5 * 1024 * 1024) {
                        showToast('이미지 크기는 5MB 이하만 가능합니다', 'warning');
                        return;
                    }
                    const reader = new FileReader();
                    reader.onload = (evt) => {
                        restaurantImage.value = evt.target.result;
                        imagePreview.src = evt.target.result;
                        imagePreviewContainer.classList.remove('hidden');
                    };
                    reader.readAsDataURL(file);
                }
            });
        }

        if (restaurantImage) {
            restaurantImage.addEventListener('input', () => {
                const val = restaurantImage.value.trim();
                if (val) {
                    imagePreview.src = val;
                    imagePreviewContainer.classList.remove('hidden');
                } else {
                    imagePreviewContainer.classList.add('hidden');
                }
            });
        }

        if (removeImageBtn) {
            removeImageBtn.addEventListener('click', () => {
                restaurantImage.value = '';
                if (restaurantFile) restaurantFile.value = '';
                imagePreviewContainer.classList.add('hidden');
            });
        }
    }

    // ── Form Address Search ──
    handleAddressSearch() {
        const addressInput = document.getElementById('restaurantAddress');
        const query = addressInput.value.trim();
        if (!query) {
            showToast('검색할 주소 또는 장소명을 입력하세요', 'warning');
            return;
        }

        if (mapManager) {
            mapManager.searchPlaceOrAddress(query, (res) => {
                if (res) {
                    addressInput.value = res.address;
                    mapManager.setDraftMarker(res.lat, res.lng);
                    mapManager.selectedPosition = { lat: res.lat, lng: res.lng };
                    showToast(`"${res.placeName || res.address}" 위치가 지정되었습니다 📌`, 'info');
                } else {
                    showToast('해당 주소 또는 장소를 찾을 수 없습니다', 'warning');
                }
            });
        }
    }

    // ── Interactive Star Rating ──
    initStarRating() {
        const container = document.getElementById('starRatingInput');
        const stars = container.querySelectorAll('.star-btn');

        stars.forEach((star) => {
            star.addEventListener('click', () => {
                this.selectedRating = parseInt(star.dataset.rating);
                this.updateStarDisplay();
            });

            star.addEventListener('mouseenter', () => {
                const rating = parseInt(star.dataset.rating);
                stars.forEach((s, i) => {
                    s.classList.toggle('active', i < rating);
                });
            });
        });

        container.addEventListener('mouseleave', () => {
            this.updateStarDisplay();
        });

        this.updateStarDisplay();
    }

    updateStarDisplay() {
        const stars = document.querySelectorAll('#starRatingInput .star-btn');
        stars.forEach((star, index) => {
            star.classList.toggle('active', index < this.selectedRating);
        });
    }

    // ════════════════════════════════
    // DISTANCE CALCULATION (Haversine)
    // ════════════════════════════════

    calculateDistance(lat1, lon1, lat2, lon2) {
        if (!lat1 || !lon1 || !lat2 || !lon2) return null;
        const R = 6371e3; // meters
        const φ1 = lat1 * Math.PI / 180;
        const φ2 = lat2 * Math.PI / 180;
        const Δφ = (lat2 - lat1) * Math.PI / 180;
        const Δλ = (lon2 - lon1) * Math.PI / 180;

        const a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                  Math.cos(φ1) * Math.cos(φ2) *
                  Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        const d = R * c;

        if (d < 1000) {
            return `${Math.round(d)}m`;
        }
        return `${(d / 1000).toFixed(1)}km`;
    }

    updateCardDistances() {
        if (this.currentRestaurants.length > 0) {
            this.renderRestaurantList(this.currentRestaurants);
        }
    }

    // ════════════════════════════════
    // SEARCH & FILTER
    // ════════════════════════════════

    async handleSearch() {
        const query = document.getElementById('searchInput').value.trim();
        if (!query) {
            await this.loadAndRenderRestaurants();
            return;
        }

        try {
            this.showSkeleton();
            const results = await apiService.searchRestaurants(query);
            this.currentRestaurants = results;
            this.renderRestaurantList(results);

            if (mapManager) {
                mapManager.searchPlaceOrAddress(query, (location) => {
                    if (results.length > 0) {
                        mapManager.updateMarkers(results, true);
                        showToast(`"${query}" 맛집 검색 결과 ${results.length}건`, 'info');
                    } else if (location) {
                        mapManager.clearMarkers();
                        mapManager.setDraftMarker(location.lat, location.lng);
                        mapManager.selectedPosition = { lat: location.lat, lng: location.lng };
                        document.getElementById('restaurantAddress').value = location.address;
                        showToast(`지도 위치가 "${location.placeName || query}"(으)로 이동했습니다. 클릭해서 새 맛집을 등록해보세요! 📌`, 'info');
                    } else {
                        mapManager.clearMarkers();
                        showToast(`"${query}" 검색 결과가 없습니다`, 'warning');
                    }
                });
            }
        } catch (error) {
            showToast('검색 중 오류가 발생했습니다', 'error');
        }
    }

    async handleFilter() {
        const category = document.getElementById('categoryFilter').value;
        const sortOrder = document.getElementById('sortOrder').value;

        try {
            this.showSkeleton();
            let restaurants;

            if (category) {
                restaurants = await apiService.getRestaurantsByCategory(category);
            } else if (sortOrder === 'rating') {
                restaurants = await apiService.getRestaurantsByRating();
            } else {
                restaurants = await apiService.getRestaurantsByDate();
            }

            // Distance sorting if selected
            if (sortOrder === 'distance' && mapManager && mapManager.currentCoords) {
                const { lat, lng } = mapManager.currentCoords;
                restaurants.sort((a, b) => {
                    const distA = Math.hypot(a.latitude - lat, a.longitude - lng);
                    const distB = Math.hypot(b.latitude - lat, b.longitude - lng);
                    return distA - distB;
                });
            } else if (sortOrder === 'distance' && (!mapManager || !mapManager.currentCoords)) {
                showToast('현재 위치를 먼저 확인해 주세요 (📍 버튼 클릭)', 'warning');
            }

            this.currentRestaurants = restaurants;
            this.renderRestaurantList(restaurants);
            if (mapManager) mapManager.updateMarkers(restaurants, true);
        } catch (error) {
            showToast('필터링 중 오류가 발생했습니다', 'error');
        }
    }

    async toggleChip(type) {
        const chips = document.querySelectorAll('.chip');
        const chipEl = document.getElementById(type === 'highRated' ? 'chipHighRated' : 'chipNearby');

        if (this.activeChip === type) {
            chips.forEach(c => c.classList.remove('active'));
            this.activeChip = null;
            await this.loadAndRenderRestaurants();
            return;
        }

        chips.forEach(c => c.classList.remove('active'));
        chipEl.classList.add('active');
        this.activeChip = type;

        try {
            this.showSkeleton();

            if (type === 'highRated') {
                const restaurants = await apiService.getHighRatedRestaurants();
                this.currentRestaurants = restaurants;
                this.renderRestaurantList(restaurants);
                if (mapManager) mapManager.updateMarkers(restaurants, true);
                showToast(`고평점 맛집 ${restaurants.length}건`, 'info');
            } else if (type === 'nearby') {
                if (mapManager && mapManager.currentCoords) {
                    const { lat, lng } = mapManager.currentCoords;
                    const restaurants = await apiService.getNearbyRestaurants(lat, lng, 3);
                    this.currentRestaurants = restaurants;
                    this.renderRestaurantList(restaurants);
                    if (mapManager) mapManager.updateMarkers(restaurants, true);
                    showToast(`근처 맛집 ${restaurants.length}건`, 'info');
                } else {
                    if (!navigator.geolocation) {
                        showToast('위치 서비스를 지원하지 않는 브라우저입니다', 'warning');
                        return;
                    }
                    navigator.geolocation.getCurrentPosition(
                        async (pos) => {
                            const lat = pos.coords.latitude;
                            const lng = pos.coords.longitude;
                            if (mapManager) {
                                mapManager.currentCoords = { lat, lng };
                                mapManager.setMyLocationMarker(lat, lng);
                            }
                            const restaurants = await apiService.getNearbyRestaurants(lat, lng, 3);
                            this.currentRestaurants = restaurants;
                            this.renderRestaurantList(restaurants);
                            if (mapManager) mapManager.updateMarkers(restaurants, true);
                            showToast(`근처 맛집 ${restaurants.length}건`, 'info');
                        },
                        () => showToast('위치 정보를 가져올 수 없습니다', 'error'),
                        { enableHighAccuracy: true, timeout: 10000 }
                    );
                }
            }
        } catch (error) {
            showToast('데이터를 불러올 수 없습니다', 'error');
        }
    }

    async resetFilters() {
        document.getElementById('categoryFilter').value = '';
        document.getElementById('sortOrder').value = 'date';
        document.getElementById('searchInput').value = '';
        document.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
        this.activeChip = null;
        await this.loadAndRenderRestaurants();
        showToast('필터가 초기화되었습니다', 'info');
    }

    // ════════════════════════════════
    // LOAD & RENDER
    // ════════════════════════════════

    async loadAndRenderRestaurants() {
        const urlParams = new URLSearchParams(window.location.search);
        const sharedUser = urlParams.get('user');

        if (sharedUser) {
            try {
                this.showSkeleton();
                const sharedRestaurants = await apiService.getRestaurantsByUsername(sharedUser);
                this.currentRestaurants = sharedRestaurants;
                this.renderRestaurantList(sharedRestaurants);
                if (mapManager) mapManager.updateMarkers(sharedRestaurants, true);

                const banner = document.getElementById('shareBannerContainer');
                const title = document.getElementById('shareBannerTitle');
                const sub = document.getElementById('shareBannerSubtitle');
                if (banner && title && sub) {
                    title.textContent = `📌 [${escapeHtml(sharedUser)}]님의 큐레이션 맛집 지도`;
                    sub.textContent = `공유된 큐레이션 맛집 총 ${sharedRestaurants.length}곳`;
                    banner.classList.remove('hidden');
                }
                showToast(`[${sharedUser}]님의 큐레이션 맛집 지도(${sharedRestaurants.length}곳)로 접속했습니다! 🔗`, 'info');
                return;
            } catch (err) {
                console.warn('Failed to load shared user map:', err);
            }
        }

        try {
            this.showSkeleton();
            const restaurants = await apiService.getAllRestaurants();
            this.currentRestaurants = restaurants;
            this.renderRestaurantList(restaurants);
            if (mapManager) mapManager.updateMarkers(restaurants, true);
        } catch (error) {
            this.showEmpty('맛집 목록을 불러올 수 없습니다');
        }
    }

    renderRestaurantList(restaurants) {
        const container = document.getElementById('restaurantList');
        document.getElementById('restaurantCount').textContent = restaurants.length;

        if (restaurants.length === 0) {
            this.showEmpty();
            return;
        }

        const getCategoryEmoji = (cat) => {
            const m = { 'KOREAN':'🍚','CHINESE':'🥟','JAPANESE':'🍣','WESTERN':'🍝','CAFE':'☕','ETC':'🍴','OTHER':'🍴' };
            return m[cat] || '🍴';
        };

        const getCategoryLabel = (cat) => {
            const m = { 'KOREAN':'한식','CHINESE':'중식','JAPANESE':'일식','WESTERN':'양식','CAFE':'카페','ETC':'기타','OTHER':'기타' };
            return m[cat] || cat;
        };

        const renderStars = (rating) => {
            return Array.from({length: 5}, (_, i) =>
                `<span class="star-icon ${i < rating ? 'filled' : ''}">★</span>`
            ).join('');
        };

        const currentUser = apiService.getUsername();
        const myCoords = mapManager ? mapManager.currentCoords : null;

        container.innerHTML = restaurants.map(r => {
            const emoji = getCategoryEmoji(r.category);
            const catLabel = r.categoryDisplayName || getCategoryLabel(r.category);
            const catClass = r.category || 'OTHER';
            const isOwner = r.createdBy === currentUser;
            const ownerName = escapeHtml(r.createdByNickname || r.createdBy || '익명');
            const safeName = escapeHtml(r.name);
            const safeAddress = escapeHtml(r.address) || '주소 없음';
            const safeReview = escapeHtml(r.review);

            // Calculate distance badge
            let distanceStr = '';
            if (myCoords) {
                const dist = this.calculateDistance(myCoords.lat, myCoords.lng, r.latitude, r.longitude);
                if (dist) {
                    distanceStr = `<span class="distance-badge">📍 ${dist}</span>`;
                }
            }

            const hasImage = r.imageUrl && r.imageUrl.trim();
            const imageHtml = hasImage ? `
                <div class="card-image-banner" style="width:calc(100% + 32px); height:140px; margin:-16px -16px 12px -16px; overflow:hidden; border-radius:16px 16px 0 0; position:relative; background:var(--bg-tertiary);">
                    <img src="${escapeHtml(r.imageUrl)}" alt="${safeName}" style="width:100%; height:100%; object-fit:cover;" />
                </div>
            ` : '';

            return `
                <div class="restaurant-card" onclick="restaurantUI.showDetail(${r.id})">
                    ${imageHtml}
                    <div class="card-top">
                        <span class="card-name"><span class="emoji">${emoji}</span> ${safeName} ${distanceStr}</span>
                        <div class="card-stars">${renderStars(r.rating)}</div>
                    </div>
                    <div class="card-meta">
                        <span class="category-tag ${catClass}">${catLabel}</span>
                        <span class="card-owner">👤 ${ownerName}</span>
                    </div>
                    <div class="card-address">📍 ${safeAddress}</div>
                    ${r.review ? `<div class="card-review">${safeReview}</div>` : ''}
                    <div class="card-actions">
                        <button class="btn btn-secondary" onclick="event.stopPropagation(); restaurantUI.goToMap(${r.id})">🗺️ 지도</button>
                        ${isOwner ? `<button class="btn btn-secondary" onclick="event.stopPropagation(); restaurantUI.editRestaurant(${r.id})">✏️ 수정</button>` : ''}
                        ${isOwner ? `<button class="btn btn-danger" onclick="event.stopPropagation(); restaurantUI.confirmDelete(${r.id}, '${safeName.replace(/'/g, "\\'")}')">🗑️</button>` : ''}
                    </div>
                </div>
            `;
        }).join('');
    }

    // ════════════════════════════════
    // CRUD
    // ════════════════════════════════

    async handleSaveRestaurant(e) {
        e.preventDefault();

        if (typeof isGuestMode !== 'undefined' && (isGuestMode || !apiService.isLoggedIn())) {
            showConfirm('로그인 필요 🔒', '맛집을 등록하려면 로그인이 필요합니다. 지금 로그인하시겠습니까?', () => {
                showAuthModal();
            });
            return;
        }

        const name = document.getElementById('restaurantName').value.trim();
        const address = document.getElementById('restaurantAddress').value.trim();
        const category = document.getElementById('restaurantCategory').value;
        const review = document.getElementById('restaurantReview').value.trim();

        if (!name) { showToast('맛집 이름을 입력해주세요', 'warning'); return; }
        if (!category) { showToast('카테고리를 선택해주세요', 'warning'); return; }

        if (!this.editingId && !mapManager.selectedPosition) {
            showToast('지도에서 위치를 클릭하거나 주소 검색을 진행해 주세요', 'warning');
            return;
        }

        const lat = mapManager.selectedPosition ? mapManager.selectedPosition.lat : parseFloat(document.getElementById('restaurantAddress').dataset.lat);
        const lng = mapManager.selectedPosition ? mapManager.selectedPosition.lng : parseFloat(document.getElementById('restaurantAddress').dataset.lng);

        const imageUrl = document.getElementById('restaurantImage') ? document.getElementById('restaurantImage').value.trim() : null;

        const data = {
            name, address, category,
            rating: this.selectedRating,
            review: review || null,
            imageUrl: imageUrl || null,
            latitude: lat,
            longitude: lng
        };

        const submitBtn = e.target.querySelector('button[type="submit"]');
        const originalText = submitBtn ? submitBtn.innerHTML : '💾 저장하기';

        try {
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '⌛ 저장 중...';
            }

            if (this.editingId) {
                await apiService.updateRestaurant(this.editingId, data);
                showToast('맛집 정보가 수정되었습니다', 'success');
            } else {
                await apiService.addRestaurant(data);
                showToast('새 맛집이 등록되었습니다! 🎉', 'success');
            }
            this.hideForm();
            this.resetForm();
            await this.loadAndRenderRestaurants();
        } catch (error) {
            showToast(error.message || '저장에 실패했습니다', 'error');
        } finally {
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalText;
            }
        }
    }

    async editRestaurant(id) {
        try {
            const r = await apiService.getRestaurantById(id);
            this.editingId = r.id;

            document.getElementById('restaurantName').value = r.name;
            document.getElementById('restaurantAddress').value = r.address || '';
            document.getElementById('restaurantAddress').dataset.lat = r.latitude;
            document.getElementById('restaurantAddress').dataset.lng = r.longitude;
            document.getElementById('restaurantCategory').value = r.category;
            document.getElementById('restaurantReview').value = r.review || '';

            const imgInput = document.getElementById('restaurantImage');
            const imgPreviewContainer = document.getElementById('imagePreviewContainer');
            const imgPreview = document.getElementById('imagePreview');
            if (r.imageUrl) {
                if (imgInput) imgInput.value = r.imageUrl;
                if (imgPreview) imgPreview.src = r.imageUrl;
                if (imgPreviewContainer) imgPreviewContainer.classList.remove('hidden');
            } else {
                if (imgInput) imgInput.value = '';
                if (imgPreviewContainer) imgPreviewContainer.classList.add('hidden');
            }

            this.selectedRating = r.rating;
            this.updateStarDisplay();

            if (mapManager) {
                mapManager.selectedPosition = { lat: r.latitude, lng: r.longitude };
                mapManager.setDraftMarker(r.latitude, r.longitude);
                mapManager.map.setCenter(new kakao.maps.LatLng(r.latitude, r.longitude));
            }

            const formEl = document.getElementById('restaurantForm');
            formEl.querySelector('h3').textContent = '✏️ 맛집 수정';
            formEl.classList.remove('hidden');
            formEl.scrollIntoView({ behavior: 'smooth' });
        } catch (error) {
            showToast('맛집 정보를 불러올 수 없습니다', 'error');
        }
    }

    confirmDelete(id, name) {
        showConfirm(
            '맛집 삭제',
            `"${name}"을(를) 정말 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.`,
            async () => {
                try {
                    await apiService.deleteRestaurant(id);
                    if (mapManager) mapManager.removeMarker(id);
                    await this.loadAndRenderRestaurants();
                    showToast('맛집이 삭제되었습니다', 'success');
                } catch (error) {
                    showToast(error.message || '삭제에 실패했습니다', 'error');
                }
            }
        );
    }

    // ════════════════════════════════
    // DETAIL MODAL
    // ════════════════════════════════

    _detailRestaurant = null;

    async showDetail(id) {
        try {
            const r = await apiService.getRestaurantById(id);
            this._detailRestaurant = r;

            const getCategoryEmoji = (cat) => {
                const m = { 'KOREAN':'🍚','CHINESE':'중식','JAPANESE':'🍣','WESTERN':'🍝','CAFE':'☕','ETC':'🍴','OTHER':'🍴' };
                return m[cat] || '🍴';
            };
            const getCategoryLabel = (cat) => {
                const m = { 'KOREAN':'한식','CHINESE':'중식','JAPANESE':'일식','WESTERN':'양식','CAFE':'카페','ETC':'기타','OTHER':'기타' };
                return m[cat] || cat;
            };

            const emoji = getCategoryEmoji(r.category);
            const catLabel = r.categoryDisplayName || getCategoryLabel(r.category);

            document.getElementById('detailName').textContent = `${emoji} ${r.name}`;
            document.getElementById('detailStars').innerHTML = Array.from({length:5}, (_,i) =>
                `<span class="star-icon ${i < r.rating ? 'filled' : ''}">★</span>`
            ).join('');
            document.getElementById('detailCategory').textContent = catLabel;
            document.getElementById('detailAddress').textContent = r.address || '주소 없음';
            document.getElementById('detailReview').textContent = r.review || '리뷰가 없습니다';
            document.getElementById('detailOwner').textContent = r.createdByNickname || r.createdBy || '익명';

            const detailImageHeader = document.getElementById('detailImageHeader');
            const detailImage = document.getElementById('detailImage');
            if (r.imageUrl && detailImageHeader && detailImage) {
                detailImage.src = r.imageUrl;
                detailImageHeader.classList.remove('hidden');
            } else if (detailImageHeader) {
                detailImageHeader.classList.add('hidden');
            }

            const formatDate = (dateStr) => {
                if (!dateStr) return '정보 없음';
                const d = new Date(dateStr);
                return `${d.getFullYear()}년 ${d.getMonth()+1}월 ${d.getDate()}일`;
            };
            document.getElementById('detailDate').textContent = formatDate(r.createdAt);

            document.getElementById('detailModal').classList.remove('hidden');
        } catch (error) {
            showToast('상세 정보를 불러올 수 없습니다', 'error');
        }
    }

    hideDetail() {
        document.getElementById('detailModal').classList.add('hidden');
    }

    detailGoToMap() {
        if (this._detailRestaurant && mapManager) {
            mapManager.moveToRestaurant(this._detailRestaurant);
            this.hideDetail();

            // Mobile sidebar collapse
            const sidebar = document.getElementById('sidebar');
            if (sidebar) sidebar.classList.remove('sidebar-open');
        }
    }

    // ════════════════════════════════
    // FORM HELPERS & NAVIGATION
    // ════════════════════════════════

    goToMap(id) {
        const r = this.currentRestaurants.find(r => r.id === id);
        if (r && mapManager) mapManager.moveToRestaurant(r);

        // Mobile sidebar collapse
        const sidebar = document.getElementById('sidebar');
        if (sidebar) sidebar.classList.remove('sidebar-open');
    }

    hideForm() {
        document.getElementById('restaurantForm').classList.add('hidden');
        this.editingId = null;
        document.getElementById('restaurantForm').querySelector('h3').textContent = '📌 새 맛집 등록';
        if (mapManager) {
            mapManager.selectedPosition = null;
            mapManager.removeDraftMarker();
        }
    }

    resetForm() {
        document.getElementById('addRestaurantForm').reset();
        const imgInput = document.getElementById('restaurantImage');
        const imgPreviewContainer = document.getElementById('imagePreviewContainer');
        const fileInput = document.getElementById('restaurantFile');
        if (imgInput) imgInput.value = '';
        if (fileInput) fileInput.value = '';
        if (imgPreviewContainer) imgPreviewContainer.classList.add('hidden');
        this.selectedRating = 5;
        this.updateStarDisplay();
        this.editingId = null;
        if (mapManager) mapManager.removeDraftMarker();
    }

    // ════════════════════════════════
    // UI STATES
    // ════════════════════════════════

    showSkeleton() {
        const container = document.getElementById('restaurantList');
        container.innerHTML = Array.from({length: 4}, () => `
            <div class="skeleton-card">
                <div class="skeleton-line medium"></div>
                <div class="skeleton-line short"></div>
                <div class="skeleton-line long"></div>
                <div class="skeleton-line xs"></div>
            </div>
        `).join('');
    }

    showEmpty(message) {
        const container = document.getElementById('restaurantList');
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">🍽️</div>
                <h4>${message || '등록된 맛집이 없습니다'}</h4>
                <p>지도를 클릭하거나 주소를 검색해 새로운 맛집을 추가해 보세요!</p>
            </div>
        `;
    }
}

let restaurantUI;
