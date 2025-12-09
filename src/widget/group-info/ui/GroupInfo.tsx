// 사용자 집단 정보를 렌더링 하는 컴포넌트
import InfoIcon from "@/shared/assets/icons/info.svg?react";

const GroupInfo = () => {
    {/*사용자 집단 정보 데이터 불러오는 훅 */}
  return (
    <div>
      <InfoIcon />
      {/** 사용자 집단 정보 업데이트해서 보여주는 컴포넌트 */}
      {/** 참여자 수를 보여주는 컴포넌트 */}
    </div>
  );
};

export default GroupInfo;
