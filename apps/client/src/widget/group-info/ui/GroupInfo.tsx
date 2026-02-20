import * as S from '@/shared/ui/Card';
import KebabMenuIcon from '@/shared/assets/icons/kebab-menu.svg?react';
import SubwayIcon from '@/shared/assets/icons/subway.svg?react';
import { InfoItem } from '@/entities/group-info/ui/InfoItem';
import { type GroupInfoItem } from '@/entities/group-info/model/types';

/**
 * 출근길 리포트 위젯
 * 교통(지하철/버스), 경로상 날씨, 사용자 제보 데이터를 섹션별로 보여줍니다.
 */
const GroupInfo = () => {
    // 실제 운영 시에는 각각의 useQuery 훅에서 데이터를 가져오게 됩니다.
    // 예: const { data: transport } = useTransport();

    const mockData: GroupInfoItem[] = [
        {
            id: 'subway-1',
            category: 'subway',
            content: '2호선 강남역 혼잡도 높음',
        },
        {
            id: 'bus-1',
            category: 'bus',
            content: '강남대로 정체 (서초 -> 강남)',
            isUrgent: true,
        },
        {
            id: 'weather-1',
            category: 'path-weather',
            content: '이동 경로 중 소나기 예상 (양재역 인근)',
        },
        {
            id: 'report-1',
            category: 'user-report',
            content: '사용자 집단 정보, 우산 챙겨가세요',
            participants: 34,
        },
    ];

    return (
        <S.Card className="p-6 bg-white">
            {/* 상단 헤더: 아이콘, 타이틀, 메뉴 */}
            <S.CardHeader className="flex flex-row items-center justify-between gap-4 p-0">
                <div className="flex items-center gap-6">
                    <SubwayIcon />
                    <div className="flex flex-col">
                        <S.CardTitle className="text-xl">출근길 리포트</S.CardTitle>
                        <S.CardTitle className="text-xs text-muted-foreground font-normal">경로상의 실시간 교통 및 날씨 정보</S.CardTitle>
                    </div>
                </div>
                <KebabMenuIcon className="cursor-pointer" />
            </S.CardHeader>

            {/* 리포트 아이템 리스트 */}
            <div className="flex flex-col gap-3 mt-6">
                {mockData.map((item) => (
                    <InfoItem key={item.id} {...item} />
                ))}
            </div>
        </S.Card>
    );
};

export default GroupInfo;
