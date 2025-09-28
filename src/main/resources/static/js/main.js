// 메인 애플리케이션 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 카카오맵 API 로드 확인
    if (typeof kakao === 'undefined') {
        console.error('카카오맵 API가 로드되지 않았습니다. API 키를 확인해주세요.');
        alert('카카오맵 API가 로드되지 않았습니다. 페이지를 새로고침해주세요.');
        return;
    }

    try {
        // 전역 객체 초기화
        mapManager = new KakaoMapManager();
        restaurantUI = new RestaurantUI();

        console.log('MOTMAP 애플리케이션이 성공적으로 초기화되었습니다.');
    } catch (error) {
        console.error('애플리케이션 초기화 중 오류 발생:', error);
        alert('애플리케이션 초기화 중 오류가 발생했습니다. 페이지를 새로고침해주세요.');
    }
});

// 페이지 언로드 시 정리 작업
window.addEventListener('beforeunload', function() {
    // 필요한 정리 작업이 있다면 여기에 추가
    console.log('MOTMAP 애플리케이션이 종료됩니다.');
});
