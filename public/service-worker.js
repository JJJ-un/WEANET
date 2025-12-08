/* eslint-disable no-restricted-globals */
/* eslint-disable no-undef */

const CACHE_NAME = 'my-pwa-cache-v1';
const urlsToCache = [
  '/',
  '/index.html',
  '/manifest.json'
  // 여기에 오프라인에서도 보여주고 싶은 이미지나 CSS 경로를 추가하면 됩니다.
];

// 1. 설치 (캐싱할 파일 저장)
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => {
        return cache.addAll(urlsToCache);
      })
  );
});

// 2. 요청 가로채기 (네트워크 대신 캐시에서 파일 꺼내주기)
self.addEventListener('fetch', (event) => {
  event.respondWith(
    caches.match(event.request)
      .then((response) => {
        // 캐시에 있으면 그거 주고, 없으면 네트워크로 요청
        return response || fetch(event.request);
      })
  );
});

// 3. 업데이트 (이전 캐시 삭제)
self.addEventListener('activate', (event) => {
  const cacheWhitelist = [CACHE_NAME];
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cacheName) => {
          if (cacheWhitelist.indexOf(cacheName) === -1) {
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
});