import { createRootRoute, Outlet } from "@tanstack/react-router";
// 개발 도구 임포트 (개발 환경에서 유용합니다)
import { TanStackRouterDevtools } from "@tanstack/router-devtools";

// 모든 라우트의 최상위 부모(Root) 라우트를 정의합니다.
export const Route = createRootRoute({
  // 이 컴포넌트가 모든 하위 라우트를 감싸는 레이아웃이 됩니다.
  component: () => (
    <>
      <div>
        {/* 네비게이션 영역 (Header) */}
        <p>최상위 레이아웃 (Header / Navigation)</p>
      </div>
      {/* 🌟 Outlet: 하위 라우트의 컴포넌트가 렌더링될 위치입니다. 🌟 */}
      <div style={{ padding: "20px" }}>
        <Outlet />
      </div>
      {/* TanStack Router 개발 도구 */}
      <TanStackRouterDevtools initialIsOpen={false} />
    </>
  ),
});
