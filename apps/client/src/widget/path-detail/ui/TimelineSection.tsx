import { cn } from '@/shared/lib/utils';
import { DetailPathStep } from '@/entities/path/model/types';

interface TimelineSectionProps {
    steps: DetailPathStep[];
    totalTime: number;
}

export const TimelineSection = ({ steps, totalTime }: TimelineSectionProps) => {
    return (
        <section className="flex flex-col gap-4">
            <div className="flex items-center gap-2 px-1">
                <span className="size-5 flex items-center justify-center bg-slate-800 text-white rounded-full text-[10px] font-black">↓</span>
                <h3 className="text-lg font-extrabold text-foreground">상세 경로 안내</h3>
            </div>
            <div className="bg-white rounded-3xl p-8 shadow-sm border border-border/40">
                <div className="flex flex-col gap-10 ml-2 border-l-2 border-dashed border-border/60 pl-10 relative">
                    <div className="absolute top-0 right-0 -mt-10">
                         <span className="text-xs font-bold text-muted-foreground bg-muted px-2 py-1 rounded-lg">
                            약 {totalTime}분 소요
                        </span>
                    </div>
                    {steps.map((step, index) => (
                        <div key={index} className="relative">
                            <div 
                                className={cn(
                                    "absolute -left-[51px] top-1 size-6 rounded-full border-[5px] border-white shadow-md z-10",
                                    step.transportType === 'SUBWAY' ? 'bg-primary' : 
                                    step.transportType === 'BUS' ? 'bg-secondary' : 'bg-slate-300'
                                )} 
                            />
                            <div className="flex flex-col gap-1">
                                <div className="flex justify-between items-start">
                                    <span className="font-extrabold text-lg leading-tight text-foreground">
                                        {step.startStationName} {step.transportType !== 'WALK' ? '승차' : ''}
                                    </span>
                                    <span className="text-[10px] font-black text-muted-foreground">
                                        {step.sectionTime}분
                                    </span>
                                </div>
                                {step.transportType !== 'WALK' && (
                                    <div className="flex items-center gap-2 text-sm">
                                        <span className="font-bold text-primary">{step.lineName}</span>
                                        <span className="text-muted-foreground/40 font-bold">|</span>
                                        <span className="text-muted-foreground font-bold">{step.endStationName} 방면</span>
                                    </div>
                                )}
                                {index === steps.length - 1 && (
                                    <div className="mt-6 pt-4 border-t border-dashed border-border/40 relative">
                                        <div className="absolute -left-[51px] top-6 size-6 rounded-full bg-slate-800 border-[5px] border-white shadow-md z-10" />
                                        <span className="font-extrabold text-lg text-foreground">{step.endStationName} 도착</span>
                                    </div>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </section>
    );
};
