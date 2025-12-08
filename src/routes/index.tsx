import { createFileRoute } from "@tanstack/react-router";
import { Button } from "@/shared/ui/button";
import CurrentWeather from "@/widget/current-weather/ui/CurrentWeather";

export const Route = createFileRoute("/")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <div>
      <Button>시작하기</Button>
      <div>구분선</div>
      <CurrentWeather />
    </div>
  );
}
