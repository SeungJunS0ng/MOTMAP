// API 통신을 담당하는 모듈 (ES6+ 개선 버전)
class ApiService {
    constructor() {
        this.baseUrl = '/api/restaurants';
        this.defaultHeaders = {
            'Content-Type': 'application/json',
        };
    }

    // 공통 fetch 래퍼 메소드
    async request(url, options = {}) {
        try {
            const response = await fetch(url, {
                headers: this.defaultHeaders,
                ...options
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
            }

            // 204 No Content인 경우 빈 응답 반환
            if (response.status === 204) {
                return null;
            }

            return await response.json();
        } catch (error) {
            console.error('API Request Error:', error);
            throw error;
        }
    }

    // 모든 맛집 조회
    async getAllRestaurants() {
        return this.request(this.baseUrl);
    }

    // 특정 맛집 조회
    async getRestaurantById(id) {
        return this.request(`${this.baseUrl}/${id}`);
    }

    // 맛집 추가
    async addRestaurant(restaurantData) {
        return this.request(this.baseUrl, {
            method: 'POST',
            body: JSON.stringify(restaurantData)
        });
    }

    // 맛집 수정
    async updateRestaurant(id, restaurantData) {
        return this.request(`${this.baseUrl}/${id}`, {
            method: 'PUT',
            body: JSON.stringify(restaurantData)
        });
    }

    // 맛집 삭제
    async deleteRestaurant(id) {
        await this.request(`${this.baseUrl}/${id}`, {
            method: 'DELETE'
        });
        return true;
    }

    // 카테고리별 조회
    async getRestaurantsByCategory(category) {
        return this.request(`${this.baseUrl}/category/${category}`);
    }

    // 키워드 검색
    async searchRestaurants(keyword) {
        return this.request(`${this.baseUrl}/search?keyword=${encodeURIComponent(keyword)}`);
    }

    // 평점순 정렬
    async getRestaurantsByRating() {
        return this.request(`${this.baseUrl}/sorted/rating`);
    }

    // 최신순 정렬
    async getRestaurantsByDate() {
        return this.request(`${this.baseUrl}/sorted/date`);
    }

    // 고평점 맛집 조회 (새로운 기능)
    async getHighRatedRestaurants() {
        return this.request(`${this.baseUrl}/high-rated`);
    }

    // 근처 맛집 검색
    async getNearbyRestaurants(latitude, longitude, radius = 5) {
        const params = new URLSearchParams({
            latitude: latitude.toString(),
            longitude: longitude.toString(),
            radius: radius.toString()
        });
        return this.request(`${this.baseUrl}/nearby?${params}`);
    }
}

// 전역 API 서비스 인스턴스
const apiService = new ApiService();
