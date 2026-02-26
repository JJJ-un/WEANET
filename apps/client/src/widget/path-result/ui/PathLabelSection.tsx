import { cn } from '@/shared/lib/utils';

interface PathLabelSectionProps {
    labels: string[];
}

export const PathLabelSection = ({ labels }: PathLabelSectionProps) => (
    <div className="flex gap-2">
        {labels.map((label) => (
            <span
                key={label}
                className={cn(
                    'px-2.5 py-1 rounded-lg text-[10px] font-extrabold',
                    label === '최적' ? 'bg-primary text-white' : 'bg-muted text-muted-foreground border border-border/50',
                )}
            >
                {label}
            </span>
        ))}
    </div>
);
