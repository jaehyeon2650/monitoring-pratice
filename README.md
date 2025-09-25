# Monitoring Practice

이 저장소는 **Grafana**, **Prometheus**, **Loki**를 이용해 애플리케이션의 **메트릭**과 **로그**를 수집하고 시각화하는 예제 프로젝트입니다.  
실습 환경은 Docker Compose를 기반으로 하며, `mysql`, `monitoring`, `local` 세 가지 컴포즈 파일로 구성되어 있습니다.

---

## 📊 사용된 주요 도구

### 1. Prometheus
- **역할**: 메트릭 수집 및 저장소  
- 애플리케이션(Spring Boot 등)에서 노출하는 메트릭(`Actuator + Micrometer`)을 주기적으로 스크랩(scrape)하여 시계열 DB에 저장합니다.  
- 수집된 메트릭은 쿼리 언어 **PromQL**로 조회할 수 있습니다.

### 2. Grafana
- **역할**: 대시보드 & 시각화  
- Prometheus에 저장된 메트릭을 기반으로 대시보드를 만들고 모니터링할 수 있습니다.  
- 게이지, 라인 차트, 바 차트 등 다양한 시각화 옵션을 제공합니다.

### 3. Loki
- **역할**: 로그 수집 및 검색  
- Prometheus가 메트릭을 수집하듯이, Loki는 애플리케이션과 Docker 컨테이너의 로그를 수집합니다.  
- Grafana와 연동하여 **로그 + 메트릭**을 한 화면에서 볼 수 있습니다.  
- Promtail 에이전트를 이용해 로그를 Loki로 전송합니다.

---

## 🏗️ 아키텍처 개요

프로젝트는 3가지 Docker Compose 환경으로 나누어져 있습니다:

1. **mysql**
   - MySQL 컨테이너 실행
   - 애플리케이션이 사용할 DB 제공

2. **local**
   - 우리 서비스(Spring Boot 프로젝트)와 Promtail 실행
   - 로컬에서 애플리케이션 실행 후 메트릭/로그를 수집하도록 구성

3. **monitoring**
   - Prometheus, Grafana, Loki 실행
   - 메트릭/로그 수집 및 시각화를 위한 모니터링 서버

> 실제 배포 환경에서는 **3개의 EC2 인스턴스**에 배포하는 것을 가정합니다:
> - EC2-1: 애플리케이션 + Promtail (local 컴포즈)
> - EC2-2: Monitoring 스택 (Prometheus, Grafana, Loki)
> - EC2-3: MySQL

---

## ⚙️ 실습 방법

1. **MySQL 실행**
   ```bash
   docker-compose -f mysql-compose.yml up -d
   ```

2. **애플리케이션 실행**  
   로컬에서 Spring Boot 프로젝트 실행:
   ```bash
   # Gradle로 실행
   ./gradlew bootRun

   # 또는 빌드된 JAR 실행 (파일명은 환경에 맞게 수정)
   java -jar build/libs/app.jar
   ```
   실행 후 Actuator + Prometheus 엔드포인트가 열려 있어야 합니다.  
   예) [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)

3. **Local Compose 실행 (서비스 로그 수집)**
   ```bash
   docker-compose -f local-compose.yml up -d
   ```
   → Promtail이 애플리케이션 로그를 수집하여 Loki로 전송합니다.

4. **Monitoring Compose 실행 (모니터링 스택)**
   ```bash
   docker-compose -f monitoring-compose.yml up -d
   ```

   접속 URL:
   - Prometheus: [http://localhost:9090](http://localhost:9090)  
   - Grafana: [http://localhost:3000](http://localhost:3000) (설정 계정: `admin / 1234`)  
   - Loki: [http://localhost:3100](http://localhost:3100)

5. **Grafana 대시보드 구성**
   - **데이터 소스 등록**
     - Prometheus  
       - 컨테이너 네트워크: `http://prometheus:9090`  
       - 로컬 접근: `http://localhost:9090`
     - Loki  
       - 컨테이너 네트워크: `http://loki:3100`  
       - 로컬 접근: `http://localhost:3100`

   - **대시보드 생성**
     - 메트릭 예시: 요청 평균 응답 시간, 에러율  
     - 로그 예시: 특정 URI 요청 시 남는 애플리케이션 로그

---

## ✅ 정리

- **Prometheus** → 메트릭 수집 및 저장  
- **Grafana** → 메트릭/로그 시각화  
- **Loki + Promtail** → 로그 수집 및 전달  

이 예제를 통해 EC2 분리 배포(애플리케이션+Promtail / Monitoring / MySQL) 구조로 손쉽게 확장 가능하며,  
실시간 상태 추적과 장애 원인 분석을 효율적으로 수행할 수 있습니다.
