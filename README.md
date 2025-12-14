# Economy Survey - Global Capital Phase Space Analysis

## 📖 프로젝트 소개
**Economy Survey**는 국가별 금융자본의 위상 공간을 분석하고, 자본과 생산의 관계를 시각화하여 경제 흐름을 통찰할 수 있도록 돕는 웹 플랫폼입니다.

금융부문 총신용, 설비투자, 시가총액, 가계부채 등 다양한 거시경제 지표를 4차원 위상 공간 그래프로 표현하여, 각 국가의 경제 발전 경로와 현재 위치를 직관적으로 파악할 수 있습니다.

## 🚀 주요 기능

### 1. 위상 공간 분석 (Phase Space Analysis)
국가별 경제 데이터를 기반으로 다음과 같은 4차원 시각화를 제공합니다.
- **X축**: 금융의 사회적 지배력 (민간신용/GDP %)
- **Y축**: 자본의 생산적 실현 (설비투자/GDP %)
- **버블 크기**: 시가총액 (MarketCap/GDP)
- **궤적 색상**: 가계부채 비율 (Blue: 낮음 → Red: 높음)

### 2. 커뮤니티 (Community)
- 사용자 간의 의견 공유를 위한 게시판 기능
- 게시글 작성, 수정, 조회 (CRUD)
- 댓글 및 좋아요 기능

### 3. 사용자 인증 (Authentication)
- Google, Naver 등 소셜 로그인(OAuth 2.0) 지원
- 안전한 세션 기반 인증 관리

## 🛠 기술 스택 (Tech Stack)

### Backend (Web)
- **Language**: Java 21
- **Framework**: Spring Boot 3.x
- **Data Access**: Spring Data JPA
- **Security**: Spring Security, OAuth 2.0
- **Template Engine**: Mustache
- **Database**: H2 Database (In-memory/Local file)

### Backend (Data Analysis)
- **Language**: Python 3.x
- **Framework**: FastAPI
- **Libraries**: Pandas, Pandas-DataReader, NumPy
- **Source**: World Bank Open Data

### Frontend
- **Language**: JavaScript (ES6+)
- **Visualization**: Chart.js
- **Styling**: Bootstrap (via WebJars/CDN)

## 📂 프로젝트 구조

\`\`\`
.
├── backserver.py          # Python 데이터 분석 서버 (FastAPI)
├── survey/                # Spring Boot 웹 애플리케이션
│   ├── src/main/java      # Java 소스 코드 (Controller, Service, Domain)
│   ├── src/main/resources # 설정 파일 및 템플릿 (Mustache)
│   └── build.gradle       # 빌드 설정
└── README.md              # 프로젝트 문서
\`\`\`

## 🏁 시작하기 (Getting Started)

이 프로젝트는 **Spring Boot 웹 서버**와 **Python 데이터 서버** 두 개의 프로세스로 구성되어 있습니다.

### 1. 사전 요구사항 (Prerequisites)
- Java 21 이상
- Python 3.8 이상
- Git

### 2. Python 데이터 서버 실행
데이터 수집 및 전처리를 담당하는 Python 서버를 먼저 실행합니다.

\`\`\`bash
# 필수 라이브러리 설치
pip install fastapi uvicorn pandas pandas-datareader numpy

# 서버 실행 (Port: 8000)
python backserver.py
\`\`\`

### 3. Spring Boot 웹 서버 실행
웹 인터페이스를 제공하는 Spring Boot 서버를 실행합니다.

\`\`\`bash
cd survey

# Windows
./gradlew bootRun

# Mac/Linux
./gradlew bootRun
\`\`\`

### 4. 접속
브라우저를 열고 다음 주소로 접속합니다.
- **Web URL**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console

## 📊 데이터 출처
- **World Bank Open Data**: https://data.worldbank.org/
- 일부 데이터(가계부채 등)는 보조 자료를 통해 보정되었습니다.

## 📝 라이선스
This project is licensed under the MIT License.
