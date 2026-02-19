import { render, screen } from '@testing-library/react';
import CurrentWeather from './widget/current-weather/ui/CurrentWeather';

describe('환경 설정 테스트', () => {
  test('CurrentWeather 컴포넌트가 정상적으로 렌더링되는가?', () => {
    render(<CurrentWeather />);
    // CurrentWeather 컴포넌트에 특정 텍스트가 있는지 확인
    const element = screen.getByText(/강남구 현재 날씨/);
    expect(element).toBeInTheDocument();
  });
});
