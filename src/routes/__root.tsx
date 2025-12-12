import { createRootRoute, Outlet } from '@tanstack/react-router';
// 개발 도구 임포트 (개발 환경에서 유용합니다)
import { TanStackRouterDevtools } from '@tanstack/router-devtools';

// 모든 라우트의 최상위 부모(Root) 라우트를 정의합니다.
export const Route = createRootRoute({
    component: () => (
        <div className="flex justify-center items-center min-h-[48rem] h-full">
            <div className="max-w-[39rem] w-full bg-foreground px-6">
                <Outlet />
            </div>
            <TanStackRouterDevtools initialIsOpen={false} />
        </div>
    ),
});
