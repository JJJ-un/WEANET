import { WeatherIcon } from '@/entities/weather/ui/WeatherIcon';
import { DetailPathStep } from '@/entities/path/model/types';

interface WeatherSectionProps {
    steps: DetailPathStep[];
}

export const WeatherSection = ({ steps }: WeatherSectionProps) => {
    return (
        <section className="flex flex-col gap-4">
            <div className="flex items-center gap-2 px-1 text-sky-500">
                <WeatherIcon status="Clear" className="size-5" />
                <h3 className="text-lg font-extrabold text-foreground">구간별 날씨</h3>
            </div>
            <div className="flex gap-4 overflow-x-auto pb-2 [ms-overflow-style:none] [scrollbar-width:none] [&::-webkit-scrollbar]:hidden -mx-6 px-6">
                {steps.map((step, idx) => (
                    <div key={idx} className="bg-white min-w-[240px] p-5 rounded-3xl border border-border/40 shadow-sm flex flex-col gap-3">
                        <div className="flex justify-between items-start">
                            <span className="text-[10px] font-black text-muted-foreground bg-muted px-2 py-0.5 rounded-lg">
                                {step.startStationName} 부근
                            </span>
                            <WeatherIcon status={step.weather.weather} className="size-6" />
                        </div>
                        <div className="flex flex-col">
                            <span className="text-xl font-black text-foreground">{step.weather.currentTemp}°C</span>
                            <p className="text-xs font-bold text-muted-foreground mt-1 leading-relaxed">
                                {step.weather.advice}
                            </p>
                        </div>
                    </div>
                ))}
            </div>
        </section>
    );
};
