// 카카오맵 관리 모듈 (Spring Boot 연동 버전)
class KakaoMapManager {
    constructor() {
        this.map = null;
        this.markers = [];
        this.infoWindows = [];
        this.geocoder = null;
        this.selectedPosition = null;
        this.init();
    }

    init() {
        // 카카오맵 초기화
        const container = document.getElementById('map');
        const options = {
            center: new kakao.maps.LatLng(37.566826, 126.9786567), // 서울시청 좌표
            level: 3
        };

        this.map = new kakao.maps.Map(container, options);
        this.geocoder = new kakao.maps.services.Geocoder();

        // 지도 클릭 이벤트 등록
        kakao.maps.event.addListener(this.map, 'click', (mouseEvent) => {
            this.handleMapClick(mouseEvent);
        });

        // 현재 위치로 이동 버튼 추가
        this.addCurrentLocationButton();
    }

    // 지도 클릭 처리
    handleMapClick(mouseEvent) {
        const latlng = mouseEvent.latLng;
        this.selectedPosition = {
            lat: latlng.getLat(),
            lng: latlng.getLng()
        };

        // 주소 검색
        this.geocoder.coord2Address(latlng.getLng(), latlng.getLat(), (result, status) => {
            if (status === kakao.maps.services.Status.OK) {
                const address = result[0].address.address_name;

                // 맛집 추가 폼 표시
                document.getElementById('restaurantAddress').value = address;
                document.querySelector('.add-restaurant-form').style.display = 'block';
                document.querySelector('.add-restaurant-form').scrollIntoView({ behavior: 'smooth' });
            }
        });
    }

    // 현재 위치 버튼 추가
    addCurrentLocationButton() {
        const locationBtn = document.createElement('button');
        locationBtn.innerHTML = '📍 현재 위치';
        locationBtn.style.cssText = `
            position: absolute;
            top: 10px;
            right: 10px;
            z-index: 1000;
            background: white;
            border: 2px solid #667eea;
            color: #667eea;
            padding: 8px 12px;
            border-radius: 20px;
            cursor: pointer;
            font-size: 12px;
            font-weight: bold;
        `;

        locationBtn.addEventListener('click', () => {
            this.getCurrentLocation();
        });

        document.querySelector('.map-section').appendChild(locationBtn);
    }

    // 현재 위치 가져오기
    getCurrentLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const lat = position.coords.latitude;
                    const lng = position.coords.longitude;
                    const moveLatLng = new kakao.maps.LatLng(lat, lng);

                    this.map.setCenter(moveLatLng);
                    this.map.setLevel(3);
                },
                (error) => {
                    alert('현재 위치를 가져올 수 없습니다.');
                }
            );
        } else {
            alert('이 브라우저는 위치 서비스를 지원하지 않습니다.');
        }
    }

    // 마커 추가 (Spring Boot 데이터 구조에 맞춤)
    addMarker(restaurant) {
        const position = new kakao.maps.LatLng(restaurant.latitude, restaurant.longitude);

        const marker = new kakao.maps.Marker({
            position: position,
            title: restaurant.name
        });

        marker.setMap(this.map);

        // 인포윈도우 생성
        const infowindow = new kakao.maps.InfoWindow({
            content: this.createInfoWindowContent(restaurant)
        });

        // 마커 클릭 시 인포윈도우 표시
        kakao.maps.event.addListener(marker, 'click', () => {
            this.closeAllInfoWindows();
            infowindow.open(this.map, marker);
        });

        this.markers.push({
            marker: marker,
            restaurant: restaurant,
            infoWindow: infowindow
        });
    }

    // 인포윈도우 내용 생성 (Spring Boot 데이터 구조에 맞춤)
    createInfoWindowContent(restaurant) {
        const stars = '★'.repeat(restaurant.rating) + '☆'.repeat(5 - restaurant.rating);
        return `
            <div style="padding: 10px; min-width: 200px;">
                <strong style="font-size: 14px;">${restaurant.name}</strong><br>
                <span style="background: #667eea; color: white; padding: 2px 6px; border-radius: 10px; font-size: 10px;">
                    ${restaurant.categoryDisplayName}
                </span><br>
                <div style="color: #ffa500; margin: 5px 0;">${stars}</div>
                <div style="font-size: 12px; color: #666; margin-bottom: 5px;">${restaurant.address}</div>
                <div style="font-size: 11px; color: #555; line-height: 1.3;">${restaurant.review || '리뷰 없음'}</div>
            </div>
        `;
    }

    // 모든 인포윈도우 닫기
    closeAllInfoWindows() {
        this.markers.forEach(item => {
            item.infoWindow.close();
        });
    }

    // 주소로 검색
    searchByAddress(address, callback) {
        this.geocoder.addressSearch(address, (result, status) => {
            if (status === kakao.maps.services.Status.OK) {
                const coords = new kakao.maps.LatLng(result[0].y, result[0].x);
                this.map.setCenter(coords);
                this.map.setLevel(3);

                if (callback) {
                    callback({
                        lat: result[0].y,
                        lng: result[0].x,
                        address: result[0].address_name
                    });
                }
            } else {
                alert('검색 결과가 없습니다.');
            }
        });
    }

    // 특정 맛집으로 이동
    moveToRestaurant(restaurant) {
        const position = new kakao.maps.LatLng(restaurant.latitude, restaurant.longitude);
        this.map.setCenter(position);
        this.map.setLevel(3);

        // 해당 마커의 인포윈도우 열기
        const markerData = this.markers.find(item => item.restaurant.id === restaurant.id);
        if (markerData) {
            this.closeAllInfoWindows();
            markerData.infoWindow.open(this.map, markerData.marker);
        }
    }

    // 마커 삭제
    removeMarker(restaurantId) {
        const index = this.markers.findIndex(item => item.restaurant.id === restaurantId);
        if (index !== -1) {
            this.markers[index].marker.setMap(null);
            this.markers.splice(index, 1);
        }
    }

    // 모든 마커 삭제
    clearMarkers() {
        this.markers.forEach(item => {
            item.marker.setMap(null);
        });
        this.markers = [];
    }

    // 마커들 업데이트
    updateMarkers(restaurants) {
        this.clearMarkers();
        restaurants.forEach(restaurant => {
            this.addMarker(restaurant);
        });
    }
}

// 전역 변수로 맵 매니저 인스턴스 생성
let mapManager;
