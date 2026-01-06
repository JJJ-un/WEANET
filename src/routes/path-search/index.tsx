import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/path-search/")({
  component: RouteComponent,
});

function RouteComponent() {
  return <div>Hello</div>;
}
