import { createFileRoute } from "@tanstack/react-router";
import CurrentWeather from "@/widget/current-weather/ui/CurrentWeather";
import DailyForecast from "@/widget/current-weather/ui/DailyForecast";
import GroupInfo from "@/widget/group-info/ui/GroupInfo";

export const Route = createFileRoute("/")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <div className="flex flex-col gap-4">
      <CurrentWeather />
      <DailyForecast />
      <GroupInfo />
    </div>
  );
}
