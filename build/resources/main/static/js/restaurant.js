// 맛집 UI 관리 클래스 (Spring Boot API 연동 버전)
class RestaurantUI {
    constructor() {
        this.selectedRating = 5;
        this.currentRestaurants = [];
        this.init();
    }

    async init() {
        this.initEventListeners();
        await this.loadAndRenderRestaurants();
    }

    // 이벤트 리스너 초기화
    initEventListeners() {
        // 검색 기능
        const searchBtn = document.getElementById('searchBtn');
        const searchInput = document.getElementById('searchInput');

        searchBtn.addEventListener('click', () => this.handleSearch());
        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.handleSearch();
        });

        // 필터링 기능
        const categoryFilter = document.getElementById('categoryFilter');
        const sortOrder = document.getElementById('sortOrder');

        categoryFilter.addEventListener('change', () => this.handleFilter());
        sortOrder.addEventListener('change', () => this.handleFilter());

        // 맛집 추가 폼
        const addForm = document.getElementById('addRestaurantForm');
        addForm.addEventListener('submit', (e) => this.handleAddRestaurant(e));

        // 취소 버튼
        const cancelBtn = document.getElementById('cancelBtn');
        cancelBtn.addEventListener('click', () => this.hideAddForm());

        // 별점 이벤트
        this.initStarRating();
    }

    // 별점 초기화
    initStarRating() {
        const stars = document.querySelectorAll('.star');
        stars.forEach((star, index) => {
            star.addEventListener('click', () => {
                this.selectedRating = index + 1;
                this.updateStarDisplay();
            });

            star.addEventListener('mouseenter', () => {
                this.highlightStars(index + 1);
            });
        });

        document.querySelector('.star-rating').addEventListener('mouseleave', () => {
            this.updateStarDisplay();
        });
    }

    // 별점 표시 업데이트
    updateStarDisplay() {
        const stars = document.querySelectorAll('.star');
        stars.forEach((star, index) => {
            star.classList.toggle('active', index < this.selectedRating);
        });
    }

    // 별점 하이라이트
    highlightStars(rating) {
        const stars = document.querySelectorAll('.star');
        stars.forEach((star, index) => {
            star.classList.toggle('active', index < rating);
        });
    }

    // 검색 처리
    async handleSearch() {
        const query = document.getElementById('searchInput').value.trim();
        if (!query) {
            await this.loadAndRenderRestaurants();
            return;
        }

        try {
            this.showLoading();

            // 지도에서 주소 검색
            if (mapManager) {
                mapManager.searchByAddress(query, (result) => {
                    // 검색 결과가 있으면 지도 이동
                });
            }

            // 서버에서 맛집 검색
            const results = await apiService.searchRestaurants(query);
            this.currentRestaurants = results;
            this.renderRestaurantList(results);
            mapManager.updateMarkers(results);
        } catch (error) {
            this.showError('검색 중 오류가 발생했습니다.');
            console.error('Search error:', error);
        }
    }

    // 필터링 처리
    async handleFilter() {
        const category = document.getElementById('categoryFilter').value;
        const sortOrder = document.getElementById('sortOrder').value;

        try {
            this.showLoading();
            let restaurants;

            if (category) {
                restaurants = await apiService.getRestaurantsByCategory(category);
            } else if (sortOrder === 'rating') {
                restaurants = await apiService.getRestaurantsByRating();
            } else {
                restaurants = await apiService.getRestaurantsByDate();
            }

            this.currentRestaurants = restaurants;
            this.renderRestaurantList(restaurants);
            mapManager.updateMarkers(restaurants);
        } catch (error) {
            this.showError('필터링 중 오류가 발생했습니다.');
            console.error('Filter error:', error);
        }
    }

    // 맛집 추가 처리
    async handleAddRestaurant(e) {
        e.preventDefault();

        const name = document.getElementById('restaurantName').value.trim();
        const address = document.getElementById('restaurantAddress').value.trim();
        const category = document.getElementById('restaurantCategory').value;
        const review = document.getElementById('restaurantReview').value.trim();

        // 프론트엔드 유효성 검사
        if (!name) {
            this.showError('맛집 이름을 입력해주세요.');
            return;
        }

        if (!address) {
            this.showError('주소를 입력해주세요.');
            return;
        }

        if (!category) {
            this.showError('카테고리를 선택해주세요.');
            return;
        }

        if (!mapManager.selectedPosition) {
            this.showError('지도에서 위치를 선택해주세요.');
            return;
        }

        // 위도/경도 범위 검증
        const lat = mapManager.selectedPosition.lat;
        const lng = mapManager.selectedPosition.lng;

        if (lat < -90 || lat > 90) {
            this.showError('올바르지 않은 위도입니다. (-90 ~ 90도)');
            return;
        }

        if (lng < -180 || lng > 180) {
            this.showError('올바르지 않은 경도입니다. (-180 ~ 180도)');
            return;
        }

        const restaurantData = {
            name,
            address,
            category,
            rating: this.selectedRating,
            review: review || null,
            latitude: lat,
            longitude: lng
        };

        try {
            const restaurant = await apiService.addRestaurant(restaurantData);

            // 지도에 마커 추가
            mapManager.addMarker(restaurant);

            await this.loadAndRenderRestaurants();
            this.hideAddForm();
            this.resetForm();

            this.showSuccess('맛집이 성공적으로 추가되었습니다!');
        } catch (error) {
            this.showError(error.message || '맛집 추가 중 오류가 발생했습니다.');
            console.error('Add restaurant error:', error);
        }
    }

    // 맛집 목록 로드 및 렌더링
    async loadAndRenderRestaurants() {
        try {
            this.showLoading();
            const restaurants = await apiService.getAllRestaurants();
            this.currentRestaurants = restaurants;
            this.renderRestaurantList(restaurants);
            mapManager.updateMarkers(restaurants);
        } catch (error) {
            this.showError('맛집 목록을 불러오는 중 오류가 발생했습니다.');
            console.error('Load restaurants error:', error);
        }
    }

    // 맛집 목록 렌더링
    renderRestaurantList(restaurants) {
        const listContainer = document.getElementById('restaurantList');

        if (restaurants.length === 0) {
            listContainer.innerHTML = '<p style="text-align: center; color: #666; padding: 20px;">저장된 맛집이 없습니다.</p>';
            return;
        }

        const html = restaurants.map(restaurant => `
            <div class="restaurant-item" data-id="${restaurant.id}">
                <div class="restaurant-name">${restaurant.name}</div>
                <span class="restaurant-category">${restaurant.categoryDisplayName}</span>
                <div class="restaurant-address">${restaurant.address}</div>
                <div class="restaurant-rating">${'★'.repeat(restaurant.rating)}${'☆'.repeat(5 - restaurant.rating)}</div>
                <div class="restaurant-review">${restaurant.review || '리뷰 없음'}</div>
                <div class="restaurant-actions">
                    <button class="btn-view" onclick="restaurantUI.moveToRestaurant(${restaurant.id})">지도에서 보기</button>
                    <button class="btn-delete" onclick="restaurantUI.deleteRestaurant(${restaurant.id})">삭제</button>
                </div>
            </div>
        `).join('');

        listContainer.innerHTML = html;
    }

    // 맛집으로 이동
    moveToRestaurant(id) {
        const restaurant = this.currentRestaurants.find(r => r.id === id);
        if (restaurant && mapManager) {
            mapManager.moveToRestaurant(restaurant);
        }
    }

    // 맛집 삭제
    async deleteRestaurant(id) {
        if (!confirm('이 맛집을 삭제하시겠습니까?')) {
            return;
        }

        try {
            await apiService.deleteRestaurant(id);

            // 지도에서 마커 제거
            mapManager.removeMarker(id);

            await this.loadAndRenderRestaurants();
            this.showSuccess('맛집이 성공적으로 삭제되었습니다.');
        } catch (error) {
            this.showError('맛집 삭제 중 오류가 발생했습니다.');
            console.error('Delete restaurant error:', error);
        }
    }

    // 추가 폼 숨기기
    hideAddForm() {
        document.querySelector('.add-restaurant-form').style.display = 'none';
        if (mapManager) {
            mapManager.selectedPosition = null;
        }
    }

    // 폼 리셋
    resetForm() {
        document.getElementById('addRestaurantForm').reset();
        this.selectedRating = 5;
        this.updateStarDisplay();
    }

    // 로딩 표시
    showLoading() {
        const listContainer = document.getElementById('restaurantList');
        listContainer.innerHTML = '<div class="loading">로딩 중...</div>';
    }

    // 에러 메시지 표시
    showError(message) {
        const listContainer = document.getElementById('restaurantList');
        listContainer.innerHTML = `<div class="error-message">${message}</div>`;

        // 3초 후 자동으로 목록 다시 로드
        setTimeout(() => {
            this.loadAndRenderRestaurants();
        }, 3000);
    }

    // 성공 메시지 표시
    showSuccess(message) {
        // 임시 성공 메시지 엘리먼트 생성
        const successDiv = document.createElement('div');
        successDiv.className = 'success-message';
        successDiv.textContent = message;

        const sidebar = document.querySelector('.sidebar');
        sidebar.insertBefore(successDiv, sidebar.firstChild);

        // 3초 후 메시지 제거
        setTimeout(() => {
            successDiv.remove();
        }, 3000);
    }
}

// 전역 변수
let restaurantUI;
