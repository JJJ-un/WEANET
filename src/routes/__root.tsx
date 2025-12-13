import { createRootRoute, Outlet } from "@tanstack/react-router";
// 개발 도구 임포트 (개발 환경에서 유용합니다)
import { TanStackRouterDevtools } from "@tanstack/router-devtools";
import TabMenu from "@/shared/ui/TabMenu";

// 모든 라우트의 최상위 부모(Root) 라우트를 정의합니다.
// 여기에 탭메뉴를 구현하면 될 것 같다.
export const Route = createRootRoute({
  component: () => (
    <div className="flex justify-center items-center">
      <div className="max-w-[39rem] w-full bg-foreground px-6 min-h-screen">
        <Outlet />
        <TabMenu />
      </div>
      <TanStackRouterDevtools initialIsOpen={false} />
    </div>
  ),
});
