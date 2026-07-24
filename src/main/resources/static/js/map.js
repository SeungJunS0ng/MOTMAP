// ═══════════════════════════════════════════
// MOTMAP — Kakao Map Manager
// 카카오맵, 커스텀 오버레이, 이모지 마커, 내 위치, 드래프트 핀, 마커 클러스터러, 반경 원, 길찾기
// ═══════════════════════════════════════════

class KakaoMapManager {
    constructor() {
        this.map = null;
        this.markers = [];
        this.overlays = [];
        this.geocoder = null;
        this.places = null;
        this.clusterer = null;
        this.radiusCircle = null;
        this.selectedPosition = null;
        this.currentOverlay = null;
        this.myLocationOverlay = null;
        this.draftOverlay = null;
        this.currentCoords = null; // { lat, lng }
        this.init();
    }

    init() {
        const container = document.getElementById('map');
        const options = {
            center: new kakao.maps.LatLng(37.566826, 126.9786567),
            level: 4
        };

        this.map = new kakao.maps.Map(container, options);
        this.geocoder = new kakao.maps.services.Geocoder();

        // Kakao Map Controls (MapType & Zoom)
        const mapTypeControl = new kakao.maps.MapTypeControl();
        this.map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);

        const zoomControl = new kakao.maps.ZoomControl();
        this.map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);

        // Marker Clusterer
        if (kakao.maps.MarkerClusterer) {
            this.clusterer = new kakao.maps.MarkerClusterer({
                map: this.map,
                averageCenter: true,
                minLevel: 6
            });
        }

        // Map click listener
        kakao.maps.event.addListener(this.map, 'click', (mouseEvent) => {
            this.closeOverlay();
            this.handleMapClick(mouseEvent);
        });

        // Current location button
        document.getElementById('currentLocationBtn').addEventListener('click', () => {
            this.getCurrentLocation();
        });
    }

    // ── Map Click → Show Draft Pin & Form ──
    handleMapClick(mouseEvent) {
        const latlng = mouseEvent.latLng;
        const lat = latlng.getLat();
        const lng = latlng.getLng();

        this.selectedPosition = { lat, lng };
        this.setDraftMarker(lat, lng);

        this.geocoder.coord2Address(lng, lat, (result, status) => {
            if (status === kakao.maps.services.Status.OK) {
                const address = result[0].address.address_name;
                document.getElementById('restaurantAddress').value = address;
            }
        });

        // Show form & toast guidance
        const formEl = document.getElementById('restaurantForm');
        formEl.classList.remove('hidden');
        formEl.scrollIntoView({ behavior: 'smooth', block: 'start' });
        showToast('선택한 위치의 주소가 채워졌습니다 📍 맛집 정보를 입력 후 저장해 주세요!', 'info');
    }

    // ── Set Draft Marker (📌) ──
    setDraftMarker(lat, lng) {
        this.removeDraftMarker();

        const position = new kakao.maps.LatLng(lat, lng);
        const draftEl = document.createElement('div');
        draftEl.className = 'draft-pin-bounce';
        draftEl.innerHTML = '<span>📌</span>';
        draftEl.title = '등록할 위치';

        this.draftOverlay = new kakao.maps.CustomOverlay({
            position: position,
            content: draftEl,
            yAnchor: 1,
            zIndex: 15
        });

        this.draftOverlay.setMap(this.map);
    }

    removeDraftMarker() {
        if (this.draftOverlay) {
            this.draftOverlay.setMap(null);
            this.draftOverlay = null;
        }
    }

    // ── Draw Radius Circle (kakao.maps.Circle) ──
    drawRadiusCircle(lat, lng, radiusMeters = 1000) {
        this.removeRadiusCircle();

        this.radiusCircle = new kakao.maps.Circle({
            center: new kakao.maps.LatLng(lat, lng),
            radius: radiusMeters,
            strokeWeight: 2,
            strokeColor: '#3B82F6',
            strokeOpacity: 0.8,
            strokeStyle: 'dashed',
            fillColor: '#3B82F6',
            fillOpacity: 0.12
        });

        this.radiusCircle.setMap(this.map);
    }

    removeRadiusCircle() {
        if (this.radiusCircle) {
            this.radiusCircle.setMap(null);
            this.radiusCircle = null;
        }
    }

    // ── Current Location ──
    getCurrentLocation() {
        if (!navigator.geolocation) {
            showToast('이 브라우저는 위치 서비스를 지원하지 않습니다.', 'warning');
            return;
        }

        showToast('현재 위치를 확인하는 중입니다...', 'info');

        const onSuccess = (position) => {
            const lat = position.coords.latitude;
            const lng = position.coords.longitude;
            this.currentCoords = { lat, lng };

            const locPosition = new kakao.maps.LatLng(lat, lng);
            this.map.setCenter(locPosition);
            this.map.setLevel(4);

            // Update My Location Pulse Overlay & 1km Radius Circle
            this.setMyLocationMarker(lat, lng);
            this.drawRadiusCircle(lat, lng, 1000);
            showToast('현재 위치로 이동했습니다 (반경 1km 표시) 📍', 'success');

            // Recalculate card distances
            if (restaurantUI) restaurantUI.updateCardDistances();
        };

        // Try high accuracy first, fallback to standard accuracy for Desktop PC
        navigator.geolocation.getCurrentPosition(
            onSuccess,
            (err) => {
                console.warn('High accuracy geolocation failed, trying standard accuracy...', err);
                navigator.geolocation.getCurrentPosition(
                    onSuccess,
                    (err2) => {
                        console.warn('Standard geolocation failed:', err2);
                        const center = this.map.getCenter();
                        const lat = center.getLat();
                        const lng = center.getLng();
                        this.currentCoords = { lat, lng };
                        this.setMyLocationMarker(lat, lng);
                        showToast('위치를 추정할 수 없어 지도 중심점으로 설정되었습니다 📍', 'warning');
                        if (restaurantUI) restaurantUI.updateCardDistances();
                    },
                    { enableHighAccuracy: false, timeout: 5000, maximumAge: 60000 }
                );
            },
            { enableHighAccuracy: true, timeout: 4000, maximumAge: 0 }
        );
    }

    // ── Set My Location Marker ──
    setMyLocationMarker(lat, lng) {
        if (this.myLocationOverlay) {
            this.myLocationOverlay.setMap(null);
        }

        const position = new kakao.maps.LatLng(lat, lng);
        const el = document.createElement('div');
        el.className = 'my-location-pulse-marker';
        el.title = '내 위치';

        this.myLocationOverlay = new kakao.maps.CustomOverlay({
            position: position,
            content: el,
            yAnchor: 0.5,
            xAnchor: 0.5,
            zIndex: 20
        });

        this.myLocationOverlay.setMap(this.map);
    }

    // ── Category Emoji ──
    getCategoryEmoji(category) {
        const map = {
            'KOREAN': '🍚', 'CHINESE': '🥟', 'JAPANESE': '🍣',
            'WESTERN': '🍝', 'CAFE': '☕', 'ETC': '🍴', 'OTHER': '🍴'
        };
        return map[category] || '🍴';
    }

    getCategoryLabel(category) {
        const map = {
            'KOREAN': '한식', 'CHINESE': '중식', 'JAPANESE': '일식',
            'WESTERN': '양식', 'CAFE': '카페', 'ETC': '기타', 'OTHER': '기타'
        };
        return map[category] || category;
    }

    // ── Kakao Map Directions (길찾기) ──
    openDirections(name, lat, lng) {
        const safeName = encodeURIComponent(name);
        const url = `https://map.kakao.com/link/to/${safeName},${lat},${lng}`;
        window.open(url, '_blank');
    }

    // ── Add Marker ──
    addMarker(restaurant) {
        const position = new kakao.maps.LatLng(restaurant.latitude, restaurant.longitude);
        const emoji = this.getCategoryEmoji(restaurant.category);

        const markerContent = document.createElement('div');
        markerContent.innerHTML = `
            <div style="
                width:36px; height:36px;
                background:white;
                border-radius:50% 50% 50% 0;
                transform:rotate(-45deg);
                display:flex; align-items:center; justify-content:center;
                box-shadow:0 3px 10px rgba(0,0,0,0.2);
                border:2px solid white;
                cursor:pointer;
                transition: transform 0.15s ease;
            ">
                <span style="transform:rotate(45deg); font-size:16px; line-height:1;">${emoji}</span>
            </div>
        `;

        const markerOverlay = new kakao.maps.CustomOverlay({
            position: position,
            content: markerContent,
            yAnchor: 1,
            zIndex: 1
        });
        markerOverlay.setMap(this.map);

        const starsHtml = Array.from({length: 5}, (_, i) =>
            `<span class="star-icon ${i < restaurant.rating ? 'filled' : ''}" style="font-size:0.78rem; color:${i < restaurant.rating ? '#F59E0B' : '#CBD5E1'}">★</span>`
        ).join('');

        const escapedName = escapeHtml(restaurant.name).replace(/'/g, "\\'");

        const overlayContent = document.createElement('div');
        overlayContent.className = 'custom-overlay';
        overlayContent.innerHTML = `
            <button class="overlay-close" onclick="mapManager.closeOverlay()">✕</button>
            <div class="overlay-name">${emoji} ${escapeHtml(restaurant.name)}</div>
            <div class="overlay-stars">${starsHtml}</div>
            <div class="overlay-address">📍 ${escapeHtml(restaurant.address) || '주소 없음'}</div>
            ${restaurant.review ? `<div class="overlay-review">"${escapeHtml(restaurant.review)}"</div>` : ''}
            <div style="display:flex; gap:6px; margin-top:8px;">
                <button class="overlay-btn" onclick="restaurantUI.showDetail(${restaurant.id})">상세보기</button>
                <button class="overlay-btn" onclick="mapManager.openDirections('${escapedName}', ${restaurant.latitude}, ${restaurant.longitude})" style="background:var(--accent-blue); flex:1;">🧭 길찾기</button>
            </div>
        `;

        const infoOverlay = new kakao.maps.CustomOverlay({
            position: position,
            content: overlayContent,
            yAnchor: 1.15,
            zIndex: 10
        });

        markerContent.addEventListener('click', (e) => {
            e.stopPropagation();
            this.closeOverlay();
            this.currentOverlay = infoOverlay;
            infoOverlay.setMap(this.map);
            markerContent.querySelector('div').style.transform = 'rotate(-45deg) scale(1.15)';
        });

        this.markers.push({
            markerOverlay,
            infoOverlay,
            restaurant,
            markerContent,
            position
        });
    }

    // ── Close Overlay ──
    closeOverlay() {
        if (this.currentOverlay) {
            this.currentOverlay.setMap(null);
            this.currentOverlay = null;
        }
        this.markers.forEach(m => {
            const div = m.markerContent.querySelector('div');
            if (div) div.style.transform = 'rotate(-45deg) scale(1)';
        });
    }

    // ── Move to Restaurant ──
    moveToRestaurant(restaurant) {
        const position = new kakao.maps.LatLng(restaurant.latitude, restaurant.longitude);
        this.map.setCenter(position);
        this.map.setLevel(3);

        const markerData = this.markers.find(m => m.restaurant.id === restaurant.id);
        if (markerData) {
            this.closeOverlay();
            this.currentOverlay = markerData.infoOverlay;
            markerData.infoOverlay.setMap(this.map);
        }
    }

    // ── Fit Map Bounds to Markers ──
    fitBoundsToMarkers() {
        if (this.markers.length === 0) return;
        if (this.markers.length === 1) {
            this.map.setCenter(this.markers[0].position);
            this.map.setLevel(3);
            return;
        }

        const bounds = new kakao.maps.LatLngBounds();
        this.markers.forEach(m => bounds.extend(m.position));
        this.map.setBounds(bounds);
    }

    // ── Remove Marker ──
    removeMarker(restaurantId) {
        const index = this.markers.findIndex(m => m.restaurant.id === restaurantId);
        if (index !== -1) {
            this.markers[index].markerOverlay.setMap(null);
            this.markers[index].infoOverlay.setMap(null);
            this.markers.splice(index, 1);
        }
    }

    // ── Clear Markers ──
    clearMarkers() {
        this.closeOverlay();
        this.markers.forEach(m => {
            m.markerOverlay.setMap(null);
            m.infoOverlay.setMap(null);
        });
        this.markers = [];
    }

    // ── Update Markers & Fit Bounds ──
    updateMarkers(restaurants, autoFit = true) {
        this.clearMarkers();
        restaurants.forEach(r => this.addMarker(r));
        if (autoFit && restaurants.length > 0) {
            this.fitBoundsToMarkers();
        }
    }

    // ── Search Place or Address (Kakao Geocoder + Places Keyword Search) ──
    searchPlaceOrAddress(keyword, callback) {
        if (!keyword) return;

        // 1. Try Geocoder Address Search
        this.geocoder.addressSearch(keyword, (result, status) => {
            if (status === kakao.maps.services.Status.OK && result.length > 0) {
                const lat = parseFloat(result[0].y);
                const lng = parseFloat(result[0].x);
                const coords = new kakao.maps.LatLng(lat, lng);
                this.map.setCenter(coords);
                this.map.setLevel(3);
                if (callback) callback({ lat, lng, address: result[0].address.address_name, placeName: keyword });
                return;
            }

            // 2. Fallback to Kakao Places Keyword Search for general buildings/landmarks/areas
            if (!this.places) {
                this.places = new kakao.maps.services.Places();
            }

            this.places.keywordSearch(keyword, (data, placeStatus) => {
                if (placeStatus === kakao.maps.services.Status.OK && data.length > 0) {
                    const firstPlace = data[0];
                    const lat = parseFloat(firstPlace.y);
                    const lng = parseFloat(firstPlace.x);
                    const coords = new kakao.maps.LatLng(lat, lng);
                    this.map.setCenter(coords);
                    this.map.setLevel(3);
                    const placeAddr = firstPlace.road_address_name || firstPlace.address_name || firstPlace.place_name;
                    if (callback) callback({ lat, lng, address: placeAddr, placeName: firstPlace.place_name });
                } else {
                    if (callback) callback(null);
                }
            });
        });
    }

    // Keep backwards compatibility alias
    searchByAddress(address, callback) {
        this.searchPlaceOrAddress(address, callback);
    }
}

let mapManager;
