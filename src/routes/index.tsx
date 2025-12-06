import { createFileRoute } from "@tanstack/react-router";
import { Button } from "@/shared/ui/button";

export const Route = createFileRoute("/")({
  component: RouteComponent,
});

function RouteComponent() {
  return <Button>시작하기</Button>;
}
