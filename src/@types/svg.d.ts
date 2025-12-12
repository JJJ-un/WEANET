// svg.d.ts

// 1. 일반적인 SVG 파일 import에 대한 선언
declare module "*.svg" {
  const content: string;
  export default content;
}

// 2. ⭐️ *.svg?react 쿼리가 붙은 SVG 파일에 대한 선언 (핵심 해결책)
//    : 이 파일을 React 컴포넌트(Functional Component)로 간주하도록 선언합니다.
declare module "*.svg?react" {
  import * as React from "react";

  const SVG: React.FC<React.SVGProps<SVGSVGElement>>;
  export default SVG;
}
