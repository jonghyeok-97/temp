# 프로젝트 목표

- 에어비앤비 같은 숙소 예약 서비스를 구현하기 입니다.
- 운영 중 발생하는 이슈를 고려하고, 운영 환경을 고려하며 기술을 학습하고 적용하는 프로젝트를 만들고자 합니다.

# Issue 해결 과정
#### [- [#6] RestTemplate 로깅 중 404 응답에 대한 FileNotFoundException 해결](https://dkswhdgur246.tistory.com/70)
#### [- [#5] @Async 이메일 전송 고도화 : 재시도, 예외 처리, 테스트](https://dkswhdgur246.tistory.com/69)
#### [- [#4] 이메일 전송을 Async-NonBlocking 처리하기: 쓰레드 풀 설정과 API 응답 속도 99% 개선](https://dkswhdgur246.tistory.com/68)
#### [- [#3] [결제 프로세스 설계] 트랜잭션 설정 및 전파, 동기/비동기, 동시성, Facade 패턴](https://dkswhdgur246.tistory.com/66)
#### [- [#2] 테스트로 알아보는 컨트롤러의 HttpSession 주입 시점](https://dkswhdgur246.tistory.com/57)
#### [- [#1] 20만 더미 데이터 삽입을 csv파일을 이용해서 DB connection timeout → 7초로 줄이기](https://dkswhdgur246.tistory.com/47)

# 프로젝트 중점사항
### #1. 숙소 예약 및 결제 기능
#### 데이터 정합성 확보
  - 스프링 AOP의 동작 원리를 학습해 트랜잭션 설계
  - 트랜잭션 전파를 사용하여 논리 트랜잭션 롤백 시 물리 트랜잭션도 롤백되도록 구현
  - rollbackFor 속성을 이용해 DB IOException 발생 시 트랜잭션 롤백 처리

#### 동시 예약 및 결제 방지
  - DB 트랜잭션 격리 수준, JPA 낙관적/비관적 락 학습
  - 유니크 제약 조건을 통해 동시 예약을 방지하고, 예외 발생 시 try-catch를 통해 결제 취소를 위한 외부 결제 API 호출
  - concurrent 패키지를 사용해 동시성 제어 로직을 테스트

#### 외부 API 사용 시 고려 사항
  - 동기 방식의 외부 API 호출의 영향을 덜 받는 방법을 학습하고 타임아웃 설정
  - 새로운 PG사 API 추가 시 기존 PG사 API 호출와 구분되도록 별도의 RestTemplate을 생성하고, 로깅 및 4xx/5xx 응답 처리 핸들러 구현.
  - Mockito의 verify를 사용해 외부 API 호출 로직이 실행되었는지 검증
  - RestClientTest로 MockServer를 구축하여 결제 승인, 취소, 4xx 응답 상황을 테스트
  - Facade 패턴을 적용해 외부 API 호출과 트랜잭션을 분리, 테스트 코드의 용이성 확보

### #2. 이메일 인증 기반 회원가입/로그인 기능
#### @Async 를 사용해 이메일 전송을 비동기로 처리
  - ThreadPoolExecutor 의 기본 크기, 최대 크기, 큐 용량 등을 설정하며 동작 원리를 학습
  - JMeter 를 통해 응답 속도가 99% 개선됨을 확인

#### 메일 전송 실패 시 재시도 로직
- 네트워크 오류로 인한 실패에 대해 재시도 로직 추가, @Recover를 활용해 복구 시 로그 기록
- 큐 용량 초과 예외와 재시도 복구 예외를 구분하여 운영 혼란 방지
- 비동기 처리, 재시도, 복구 로직의 테스트 중복 문제를 DynamicTest와 ParameterizedTest로 해결해 테스트 가독성을 향상

### #3. 테스트
#### 테스트 코드 작성
- JUnit5와 Mockito를 활용해 210여 개의 테스트 코드 작성, 99% 테스트 커버리지 달성
- 비동기, 재시도, 동시성, 외부 API 서버 호출을 포함한 전반적인 테스트 작성

#### 테스트 최적화
- @SpringBootTest를 사용해 테스트 환경에서 하나의 서버만 실행하도록 추상 클래스를 설계, 테스트 시간 5초 단축
- Classist 원칙을 따르며, 제어할 수 없는 외부 API 호출에는 Mock을 사용

#### 테스트로 얻은 효과
- Spring Boot의 HttpSession 주입 시점을 파악하며 로직상의 버그를 수정
- WebMvcTest와 MockMvc는 내장 톰캣(Web Server)을 테스트하지 않는다.
 
### #4. 기타 개선 사항
#### 쿼리 최적화
- 인덱스 활용 시 WHERE 조건 비교를 동등 비교(IN절)로 변경하여 쿼리 수행 시간을 0.9초 단축

#### API 문서화
- Spring Rest Docs 를 활용한 API 문서 자동화 및 S3 를 통한 배포 [**(Link)**](http://restdocs.s3-website.ap-northeast-2.amazonaws.com/)

#### 코드 중복 제거 및 유지보수성 향상
- ArgumentResolver 를 활용해 로그인 확인 로직 중복 제거
- 일관된 응답 제공을 위해 ApiResponse<T>와 ErrorCode를 사용해 성공 및 에러 응답 형식을 통일
- Bean Validation을 재정의하여 DTO의 이메일 유효성 검사 중복 로직 제거
- Web ↔ Controller, Controller ↔ Service 간 DTO를 분리해 계층 간 참조가 역행하지 않도록 설계

#### 로컬 모니터링 환경 구축
- Docker-Compose, Prometheus, Grafana를 활용해 로컬 모니터링 서버 구축
- metric 학습

## 결제 프로세스 과정
![image](https://github.com/user-attachments/assets/41871857-d94a-4175-9ce5-4c09f405ba53)


## 개발환경
* JDK17 / Spring Boot 3.X / Spring Rest Docs / Spring Mail
* JPA / MySQL / Redis
* JUnit5 & Mockito
* Docker / S3 / Prometheus / Grafana






