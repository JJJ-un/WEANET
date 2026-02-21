import WarningIcon from '@/shared/assets/icons/warning.svg?react';

/**
 * 날씨 뉴스 피드 위젯
 */
const WeatherNews = () => {
    // 실제 연동 시 서버에서 받아올 수 있는 뉴스 데이터 리스트
    const newsList = [
        {
            title: '서울시, 미세먼지 주의보 발령. 외출 시 마스크 착용 권장',
            source: '연합뉴스',
        },
        {
            title: '내일 아침 기온 영하권으로 급락, 강한 바람 주의',
            source: 'KBS 뉴스',
        },
        {
            title: '강원도 영서 지역 대설 특보, 출근길 교통 혼잡 예상',
            source: 'YTN',
        },
    ];

    return (
        <div className="flex flex-col gap-4">
            <h2 className="text-lg text-foreground font-bold">날씨 뉴스 피드</h2>

            <div className="flex flex-col gap-8">
                {newsList.map((news, index) => (
                    <div key={index} className="flex items-center gap-4">
                        <WarningIcon className="shrink-0" />
                        <div className="flex flex-col gap-1">
                            <span className="text-sm text-foreground leading-tight">{news.title}</span>
                            <span className="text-xs text-muted-foreground">{news.source}</span>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default WeatherNews;
