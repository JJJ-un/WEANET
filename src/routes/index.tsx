import { createFileRoute } from "@tanstack/react-router";
import CurrentWeather from "@/widget/current-weather/ui/CurrentWeather";

export const Route = createFileRoute("/")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <div>
      <CurrentWeather />
    </div>
  );
}
