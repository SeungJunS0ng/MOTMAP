// 맛집 데이터 관리 모듈
class RestaurantManager {
    constructor() {
        this.restaurants = [];
        this.currentId = 1;
        this.loadFromStorage();
    }

    // 로컬 스토리지에서 데이터 불러오기
    loadFromStorage() {
        const stored = localStorage.getItem('motmap_restaurants');
        if (stored) {
            this.restaurants = JSON.parse(stored);
            this.currentId = Math.max(...this.restaurants.map(r => r.id), 0) + 1;
        }
    }

    // 로컬 스토리지에 데이터 저장
    saveToStorage() {
        localStorage.setItem('motmap_restaurants', JSON.stringify(this.restaurants));
    }

    // 맛집 추가
    addRestaurant(restaurantData) {
        const restaurant = {
            id: this.currentId++,
            name: restaurantData.name,
            address: restaurantData.address,
            category: restaurantData.category,
            rating: restaurantData.rating,
            review: restaurantData.review,
            lat: restaurantData.lat,
            lng: restaurantData.lng,
            createdAt: new Date().toISOString()
        };

        this.restaurants.push(restaurant);
        this.saveToStorage();
        return restaurant;
    }

    // 맛집 수정
    updateRestaurant(id, updatedData) {
        const index = this.restaurants.findIndex(r => r.id === id);
        if (index !== -1) {
            this.restaurants[index] = { ...this.restaurants[index], ...updatedData };
            this.saveToStorage();
            return this.restaurants[index];
        }
        return null;
    }

    // 맛집 삭제
    deleteRestaurant(id) {
        const index = this.restaurants.findIndex(r => r.id === id);
        if (index !== -1) {
            const deleted = this.restaurants.splice(index, 1)[0];
            this.saveToStorage();
            return deleted;
        }
        return null;
    }

    // 모든 맛집 가져오기
    getAllRestaurants() {
        return [...this.restaurants];
    }

    // ID로 맛집 찾기
    getRestaurantById(id) {
        return this.restaurants.find(r => r.id === id);
    }

    // 카테고리별 맛집 검색
    getRestaurantsByCategory(category) {
        return this.restaurants.filter(r => r.category === category);
    }

    // 이름으로 맛집 검색
    searchRestaurants(query) {
        const lowercaseQuery = query.toLowerCase();
        return this.restaurants.filter(r =>
            r.name.toLowerCase().includes(lowercaseQuery) ||
            r.address.toLowerCase().includes(lowercaseQuery) ||
            r.review.toLowerCase().includes(lowercaseQuery)
        );
    }

    // 평점별 정렬
    sortByRating(ascending = false) {
        return [...this.restaurants].sort((a, b) =>
            ascending ? a.rating - b.rating : b.rating - a.rating
        );
    }

    // 최근 추가순 정렬
    sortByDate(newest = true) {
        return [...this.restaurants].sort((a, b) => {
            const dateA = new Date(a.createdAt);
            const dateB = new Date(b.createdAt);
            return newest ? dateB - dateA : dateA - dateB;
        });
    }
}

// UI 관리 클래스
class RestaurantUI {
    constructor(restaurantManager) {
        this.restaurantManager = restaurantManager;
        this.selectedRating = 5;
        this.init();
    }

    init() {
        this.initEventListeners();
        this.renderRestaurantList();
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
    handleSearch() {
        const query = document.getElementById('searchInput').value.trim();
        if (!query) {
            this.renderRestaurantList();
            return;
        }

        // 지도에서 주소 검색
        if (mapManager) {
            mapManager.searchByAddress(query, (result) => {
                // 검색 결과가 있으면 지도 이동
            });
        }

        // 저장된 맛집에서 검색
        const results = this.restaurantManager.searchRestaurants(query);
        this.renderRestaurantList(results);
    }

    // 맛집 추가 처리
    handleAddRestaurant(e) {
        e.preventDefault();

        const formData = new FormData(e.target);
        const name = document.getElementById('restaurantName').value;
        const address = document.getElementById('restaurantAddress').value;
        const category = document.getElementById('restaurantCategory').value;
        const review = document.getElementById('restaurantReview').value;

        if (!mapManager.selectedPosition) {
            alert('지도에서 위치를 선택해주세요.');
            return;
        }

        const restaurantData = {
            name,
            address,
            category,
            rating: this.selectedRating,
            review,
            lat: mapManager.selectedPosition.lat,
            lng: mapManager.selectedPosition.lng
        };

        const restaurant = this.restaurantManager.addRestaurant(restaurantData);

        // 지도에 마커 추가
        if (mapManager) {
            mapManager.addMarker(restaurant);
        }

        this.renderRestaurantList();
        this.hideAddForm();
        this.resetForm();

        alert('맛집이 성공적으로 추가되었습니다!');
    }

    // 맛집 목록 렌더링
    renderRestaurantList(restaurants = null) {
        const listContainer = document.getElementById('restaurantList');
        const restaurantList = restaurants || this.restaurantManager.getAllRestaurants();

        if (restaurantList.length === 0) {
            listContainer.innerHTML = '<p style="text-align: center; color: #666; padding: 20px;">저장된 맛집이 없습니다.</p>';
            return;
        }

        const html = restaurantList.map(restaurant => `
            <div class="restaurant-item" data-id="${restaurant.id}">
                <div class="restaurant-name">${restaurant.name}</div>
                <span class="restaurant-category">${this.getCategoryName(restaurant.category)}</span>
                <div class="restaurant-address">${restaurant.address}</div>
                <div class="restaurant-rating">${'★'.repeat(restaurant.rating)}${'☆'.repeat(5 - restaurant.rating)}</div>
                <div class="restaurant-review">${restaurant.review}</div>
                <div class="restaurant-actions" style="margin-top: 10px;">
                    <button onclick="restaurantUI.moveToRestaurant(${restaurant.id})" style="background: #28a745; color: white; border: none; padding: 5px 10px; border-radius: 15px; cursor: pointer; font-size: 11px; margin-right: 5px;">지도에서 보기</button>
                    <button onclick="restaurantUI.deleteRestaurant(${restaurant.id})" style="background: #dc3545; color: white; border: none; padding: 5px 10px; border-radius: 15px; cursor: pointer; font-size: 11px;">삭제</button>
                </div>
            </div>
        `).join('');

        listContainer.innerHTML = html;
    }

    // 맛집으로 이동
    moveToRestaurant(id) {
        const restaurant = this.restaurantManager.getRestaurantById(id);
        if (restaurant && mapManager) {
            mapManager.moveToRestaurant(restaurant);
        }
    }

    // 맛집 삭제
    deleteRestaurant(id) {
        if (confirm('이 맛집을 삭제하시겠습니까?')) {
            this.restaurantManager.deleteRestaurant(id);

            // 지도에서 마커 제거
            if (mapManager) {
                mapManager.removeMarker(id);
            }

            this.renderRestaurantList();
        }
    }

    // 카테고리 이름 변환
    getCategoryName(category) {
        const categories = {
            'korean': '한식',
            'chinese': '중식',
            'japanese': '일식',
            'western': '양식',
            'cafe': '카페',
            'etc': '기타'
        };
        return categories[category] || '기타';
    }

    // 추가 폼 숨기기
    hideAddForm() {
        document.querySelector('.add-restaurant-form').style.display = 'none';
        mapManager.selectedPosition = null;
    }

    // 폼 리셋
    resetForm() {
        document.getElementById('addRestaurantForm').reset();
        this.selectedRating = 5;
        this.updateStarDisplay();
    }
}

// 전역 변수
let restaurantManager;
let restaurantUI;
