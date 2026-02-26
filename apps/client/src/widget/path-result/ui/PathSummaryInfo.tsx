interface PathSummaryInfoProps {
    walkDuration: number;
    fare: number;
    transferCount: number;
}

export const PathSummaryInfo = ({ walkDuration, fare, transferCount }: PathSummaryInfoProps) => (
    <div className="flex items-center gap-4 text-xs font-semibold text-muted-foreground">
        {walkDuration > 0 && (
            <>
                <span>도보 {walkDuration}분</span>
                <span className="text-border">|</span>
            </>
        )}
        <span>{fare.toLocaleString()}원</span>
        <span className="text-border">|</span>
        <span>환승 {transferCount}회</span>
    </div>
);
