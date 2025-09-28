// API 통신을 담당하는 모듈
class ApiService {
    constructor() {
        this.baseUrl = '/api/restaurants';
    }

    // 모든 맛집 조회
    async getAllRestaurants() {
        try {
            const response = await fetch(this.baseUrl);
            if (!response.ok) throw new Error('맛집 목록을 불러오는데 실패했습니다.');
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // 특정 맛집 조회
    async getRestaurantById(id) {
        try {
            const response = await fetch(`${this.baseUrl}/${id}`);
            if (!response.ok) throw new Error('맛집 정보를 불러오는데 실패했습니다.');
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // 맛집 추가
    async addRestaurant(restaurantData) {
        try {
            const response = await fetch(this.baseUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(restaurantData)
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                const message = errorData.message || '맛집 추가에 실패했습니다.';
                throw new Error(message);
            }

            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // 맛집 수정
    async updateRestaurant(id, restaurantData) {
        try {
            const response = await fetch(`${this.baseUrl}/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(restaurantData)
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                const message = errorData.message || '맛집 수정에 실패했습니다.';
                throw new Error(message);
            }

            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // 맛집 삭제
    async deleteRestaurant(id) {
        try {
            const response = await fetch(`${this.baseUrl}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                const message = errorData.message || '맛집 삭제에 실패했습니다.';
                throw new Error(message);
            }

            return true;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // 카테고리별 조회
    async getRestaurantsByCategory(category) {
        try {
            const response = await fetch(`${this.baseUrl}/category/${category}`);
            if (!response.ok) throw new Error('카테고리별 맛집을 불러오는데 실패했습니다.');
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // 키워드 검색
    async searchRestaurants(keyword) {
        try {
            const response = await fetch(`${this.baseUrl}/search?keyword=${encodeURIComponent(keyword)}`);
            if (!response.ok) throw new Error('검색에 실패했습니다.');
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // 평점순 정렬
    async getRestaurantsByRating() {
        try {
            const response = await fetch(`${this.baseUrl}/sorted/rating`);
            if (!response.ok) throw new Error('평점순 정렬에 실패했습니다.');
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // 최신순 정렬
    async getRestaurantsByDate() {
        try {
            const response = await fetch(`${this.baseUrl}/sorted/date`);
            if (!response.ok) throw new Error('최신순 정렬에 실패했습니다.');
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // 근처 맛집 검색
    async getNearbyRestaurants(latitude, longitude, radius = 5) {
        try {
            const response = await fetch(`${this.baseUrl}/nearby?latitude=${latitude}&longitude=${longitude}&radius=${radius}`);
            if (!response.ok) throw new Error('근처 맛집 검색에 실패했습니다.');
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }
}

// 전역 API 서비스 인스턴스
const apiService = new ApiService();
