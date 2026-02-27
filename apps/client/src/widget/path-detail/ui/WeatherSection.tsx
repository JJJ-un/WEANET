import { WeatherIcon } from '@/entities/weather/ui/WeatherIcon';
import { type DetailPathStep } from '@/entities/path/model/types';

interface WeatherSectionProps {
    steps: DetailPathStep[];
}

export const WeatherSection = ({ steps }: WeatherSectionProps) => {
    return (
        <section className="flex flex-col gap-4">
            <div className="flex items-center gap-2 px-1">
                <WeatherIcon status="Clear" className="w-8 h-8 text-primary" />
                <h3 className="text-sm text-foreground">경로 구간별 날씨</h3>
            </div>
            <div className="flex gap-4 overflow-x-auto pb-2 [ms-overflow-style:none] [scrollbar-width:none] [&::-webkit-scrollbar]:hidden -mx-6 px-6">
                {steps.map((step, idx) => (
                    <div
                        key={idx}
                        className="bg-background-subtle min-w-[300px] p-5 rounded-3xl border border-border/40 shadow-sm flex flex-col gap-3"
                    >
                        <span className="text-[10px] font-bold text-muted-foreground bg-white px-2 py-0.5 rounded-lg border border-border/60 w-fit">
                            {step.startStationName} 부근
                        </span>
                        <div className="flex items-center justify-between mb-5 gap-4">
                            <div className="flex items-center gap-2">
                                <WeatherIcon status={step.weather.weather} className="w-10 h-10 text-primary" />
                                <span className="text-xl font-semibold">{step.weather.currentTemp}°C</span>
                            </div>
                            <div className="flex flex-col items-center gap-1">
                                <span className="text-[10px] text-muted-foreground font-bold">
                                    {step.weather.minTemp}° / {step.weather.maxTemp}°
                                </span>
                                <span className="text-[10px] font-bold text-primary bg-primary/10 px-2 py-0.5 rounded-full">
                                    강수 {Math.round(step.weather.precipitationProbability * 100)}%
                                </span>
                            </div>
                        </div>
                        <p className="text-xs font-bold text-muted-foreground mt-1 leading-relaxed">{step.weather.advice}</p>
                    </div>
                ))}
            </div>
        </section>
    );
};
