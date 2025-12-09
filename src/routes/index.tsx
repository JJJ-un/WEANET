import { createFileRoute } from "@tanstack/react-router";
import { Button } from "@/shared/ui/button";
import { Input } from "@/shared/ui/input";
import * as S from "@/shared/ui/card";
import GroupInfo from "@/widget/group-info/ui/GroupInfo";

export const Route = createFileRoute("/")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <div>
      <Button>시작하기</Button>
      <div>구분선</div>
      <S.Card className="w-[400px]">
        <S.CardHeader>
          <S.CardTitle>카드 제목</S.CardTitle>
          <S.CardDescription>카드 설명</S.CardDescription>
        </S.CardHeader>
        <S.CardContent>
          <Input placeholder="입력하세요" />
        </S.CardContent>
        <S.CardFooter>카드 푸터</S.CardFooter>
      </S.Card>
      <GroupInfo />
    </div>
  );
}
