# ⚡ TimeBox — Elon Musk 스타일 타임박싱 앱

> "Work like hell. Put in 80-hour weeks." — 일론 머스크 식 시간 관리를 안드로이드에서.

---

## 📱 기능 요약

| 탭 | 기능 | 설명 |
|---|---|---|
| 🧠 브레인 덤프 | 생각 비우기 | 머릿속 모든 생각을 즉시 기록 |
| ⭐ MIT | 핵심 과제 3가지 | 하루 최우선 과제 (최대 3개 제한) |
| ⏱ 타임블록 | 5분 단위 스케줄 | 충돌 감지 + 카테고리 색상 구분 |
| 🌙 저녁 회고 | 하루 마무리 | 성과·배움·개선 + 에너지/집중도 평가 |

---

## 🏗 아키텍처

```
UI (Compose)
    ↕
ViewModel (StateFlow)
    ↕
Repository (비즈니스 로직)
    ↕
DAO (Room 쿼리)
    ↕
Room DB (SQLite 로컬 저장)
```

**패턴**: MVVM + Repository  
**DI**: Hilt (Dagger 기반 의존성 주입)  
**반응형**: Kotlin Flow → StateFlow → Compose collectAsState()

---

## 🛠 기술 스택

| 분류 | 라이브러리 | 버전 |
|---|---|---|
| UI | Jetpack Compose + Material 3 | BOM 2025.02.00 |
| 로컬DB | Room + KSP | 2.7.0 |
| DI | Hilt | 2.55 |
| 비동기 | Kotlin Coroutines + Flow | 1.10.1 |
| 내비게이션 | Navigation Compose | 2.8.9 |
| 언어 | Kotlin | 2.1.0 |
| 빌드 | AGP + Gradle KTS | 8.7.3 |

---

## 🚀 Android Studio에서 실행하기

### 1. 프로젝트 열기
```
Android Studio → File → Open → TimeBox 폴더 선택
```

### 2. SDK 요구사항 확인
- **minSdk**: 26 (Android 8.0+)
- **targetSdk**: 35 (Android 15)
- **Java**: 17

### 3. Gradle Sync
```
File → Sync Project with Gradle Files
```

### 4. 실행
- 에뮬레이터: AVD Manager → Pixel 8 + API 35 권장
- 실기기: USB 디버깅 활성화 후 연결

---

## 📂 전체 파일 구조

```
TimeBox/
├── build.gradle.kts                    # 루트 Gradle
├── settings.gradle.kts
├── gradle/
│   └── libs.versions.toml             # 버전 카탈로그 (중앙 버전 관리)
└── app/
    ├── build.gradle.kts               # 앱 의존성
    └── src/main/
        ├── AndroidManifest.xml
        └── java/com/elon/timebox/
            ├── MainActivity.kt         # 앱 진입점 + BottomNav
            ├── TimeBoxApplication.kt  # Hilt Application
            ├── data/
            │   ├── AppDatabase.kt     # Room DB 싱글톤
            │   ├── dao/
            │   │   └── TimeBoxDaos.kt # DAO 4개 (CRUD 쿼리)
            │   └── entity/
            │       ├── BrainDumpEntity.kt
            │       ├── MitTaskEntity.kt
            │       ├── TimeBlockEntity.kt
            │       └── EveningReviewEntity.kt
            ├── di/
            │   └── DatabaseModule.kt  # Hilt 의존성 주입 모듈
            ├── repository/
            │   └── Repositories.kt    # 비즈니스 로직 계층
            ├── viewmodel/
            │   └── ViewModels.kt      # 4개 ViewModel (UI 상태)
            └── ui/
                ├── theme/
                │   ├── Theme.kt       # 다크/라이트 색상 팔레트
                │   └── Typography.kt
                ├── navigation/
                │   └── Navigation.kt  # 화면 라우트 정의
                └── screens/
                    ├── BrainDumpScreen.kt
                    ├── MitScreen.kt
                    ├── TimeBlockScreen.kt
                    └── EveningReviewScreen.kt
```

---

## 🎨 디자인 철학

- **다크모드**: 딥 스페이스 블랙 + 일렉트릭 블루 (Tesla 대시보드 영감)
- **라이트모드**: 클린 화이트 + 딥 네이비
- **카테고리 색상**: 업무(파랑), 개인(보라), 건강(초록), 학습(노랑), 기타(회색)

---

## 🔮 향후 개선 아이디어

- [ ] 날짜 선택기 (DatePicker)로 과거 데이터 조회
- [ ] 타임블록 드래그 앤 드롭 재배치
- [ ] 회고 히스토리 캘린더 뷰
- [ ] 위젯 (Glance API) — 오늘의 MIT 홈 화면 표시
- [ ] 알림 (WorkManager) — 타임블록 시작 5분 전 알림
- [ ] CSV/JSON 백업 내보내기
