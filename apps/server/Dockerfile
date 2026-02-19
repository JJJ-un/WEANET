# 1. Base Image (Amazon Corretto 또는 Eclipse Temurin 추천)
FROM eclipse-temurin:17-jdk-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너로 복사
COPY build/libs/*.jar app.jar

# 4. 실행 명령어 (프로파일 설정 포함)
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
