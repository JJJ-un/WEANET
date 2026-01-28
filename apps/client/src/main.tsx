import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { RouterProvider, createRouter } from '@tanstack/react-router';
import { QueryClientProvider, QueryClient } from '@tanstack/react-query';

import { routeTree } from './routeTree.gen';
import './index.css';

export const queryClient = new QueryClient();

const router = createRouter({
    routeTree,
    context: {
        queryClient,
    },
});

declare module '@tanstack/react-router' {
    interface Register {
        router: typeof router;
    }
}

if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker
            .register('/service-worker.js')
            .then((registration) => {
                console.log('SW 등록 성공:', registration);
            })
            .catch((error) => {
                console.log('SW 등록 실패:', error);
            });
    });
}

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <QueryClientProvider client={queryClient}>
            <RouterProvider router={router} />
        </QueryClientProvider>
    </StrictMode>,
);
