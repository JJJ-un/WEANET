# 1. Base Image (Java 17 환경)
FROM openjdk:17-jdk-slim

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너로 복사
# (빌드는 GitHub Actions 등 외부에서 수행하고 결과물만 가져옵니다)
COPY build/libs/*.jar app.jar

# 4. 실행 명령어 (프로파일 설정 포함)
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
