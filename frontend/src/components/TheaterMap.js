import { useEffect } from "react";

const TheaterMap = ({ theaterList }) => {
  useEffect(() => {
    if (!window.kakao || !theaterList || theaterList.length === 0) return;

    window.kakao.maps.load(() => {
      const container = document.getElementById("map");
      if (!container) return;
      container.innerHTML = "";

      const map = new window.kakao.maps.Map(container, {
        center: new window.kakao.maps.LatLng(37.5, 127),
        level: 5,
      });

      const bounds = new window.kakao.maps.LatLngBounds();

      theaterList.forEach((theater) => {
        const lat = Number(theater.latitude);
        const lng = Number(theater.longitude);
        const position = new window.kakao.maps.LatLng(lat, lng);

        bounds.extend(position);

        new window.kakao.maps.Marker({
          map,
          position,
        });
      });

      map.setBounds(bounds); // ✅ 선택된 항목 중심 자동 조정
      
    });
  }, [theaterList]);

  return (
  <div id="map" className="theater-map"></div>
  );
};

export default TheaterMap;