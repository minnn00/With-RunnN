# WithRun

## 🐶 팀명: 같이, 달려갈개!
- 프로젝트명: WithRun
- 개발자 명단 (닉네임/이름):
    - 케이/최우혁
    - 바주/방지석
    - 성게/박성재
    - 효비/최효비

---

## 📚 사용 라이브러리 및 기술스택

| 라이브러리/기술    | 사용 목적 (Purpose)                            |
|--------------------|-----------------------------------------------|
| DotIndicator       | 온보딩 및 친구 카드뷰 단계 표시                |
| RecyclerView       | 리스트 및 그리드 등 모든 목록 화면             |
| Navigation         | 화면 전환 및 Fragment Backstack 관리           |
| Leonardi Speed Dial| Floating Action Button 및 ActionItem 표시      |
| Glide              | 유연한 이미지 로딩                             |
| ViewPager          | 온보딩/친구 카드뷰 등 슬라이드 화면 구현       |
| ViewModel          | MVVM 아키텍처 구현                             |
| LiveData           | MVVM 아키텍처 내 데이터 관찰 및 연동           |

---

## 🌳 브랜치 전략 및 컨벤션

- **브랜치 전략**
    - 퍼블리시를 위한 `main` 브랜치와, 개발용 `develop` 브랜치를 중심으로 운영
    - 공용 리소스/라이브러리 관리용 `package/resources` 브랜치 별도 운영
    - 각 개발자는 `develop` 브랜치에서 담당 기능별 브랜치를 생성해 작업
    - 공용 리소스/라이브러리가 필요하면 `package/resources`에 추가 후 본인 브랜치에 머지

- **브랜치 네이밍 컨벤션**
- `Type/#(number)-Work`
    - 예시: `feat/#1-map-UI`

---

## 🏷️ Issue/PR/Commit 컨벤션 및 템플릿

### Issue 컨벤션
- `[Type] (ViewName / Work to do)`
    - 예시: `[Feat] Map View / Map UI`

#### Issue 템플릿

[Type] (ViewName / Work to do)

설명:
세부 작업 내역:

---

### PR(Pull Request) 컨벤션
- **Title:** `[Type] Work`
    - 예시: `[Add] RecyclerView 라이브러리 의존성 추가`
- **Body:**  
    - 주요 설명  
    - 주요 변경점  
    - 세부 변경점  

#### PR 템플릿
[Type] Work

- 주요 설명:
- 주요 변경점:
- 세부 변경점:


---

### Commit 컨벤션
- **Title:** `[Type] Work`
    - 예시: `[Feat] Map 화면 Floating Action Button 구현`
- **Body:**  
    - 주요 변경점  
    - 세부 변경점  

#### Commit 템플릿
[Type] Work
- 주요 변경점:
- 세부 변경점:


---

## 🧑‍💻 코드 컨벤션

- Kotlin 공식 스타일 가이드 준수  
  [Android Kotlin Style Guide (공식)](https://developer.android.com/kotlin/style-guide?hl=ko)

---

## ⚙️ Android Studio 환경설정

- **Android Studio 버전:** Koala (최신 기준)
- **targetSDK:** 35
- **minSDK:** 24
- **테스트 기기:** 에뮬레이터

---

## 📢 마침글

- 본 저장소는 **공개(Public)**로 운영합니다.
- 모든 팀원은 위 컨벤션과 전략을 준수해 협업합니다.

