🔐 SecurityConfig.java — 보안을 위한 설정 클래스

📌 왜 필요한가?

Spring Boot는 기본적으로 모든 요청을 보호하려고 함. 그래서 로그인, 상품 조회 같은 페이지도 막힘
이 문제를 해결하려면 "이 URL은 누구나 접근해도 된다", "이 URL은 로그인한 사용자만 접근 가능하다"처럼 규칙을 설정해야됨.
그걸 담당하는 게 바로 SecurityConfig.

🔧 주요 설정

.csrf(csrf -> csrf.disable()) // CSRF 보안은 비활성화 (API에선 보통 비활성화)
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/", "/login", "/register", "/items").permitAll() // 로그인, 상품 등은 누구나 접근 가능
    .anyRequest().authenticated() // 나머지는 로그인한 사용자만 가능
)

🔒 필터 추가

http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

JWT(토큰)를 검사하는 필터(jwtFilter)를 추가.

이 필터는 사용자가 보낸 요청에 토큰이 있으면, "이 사람은 로그인한 사람이다!"라고 판단하도록 도와줌.

🔑 JWT 토큰 생성과 검증 담당

📌 JWT란?

사용자가 로그인하면 "로그인 증명서"를 만들어서 브라우저에게 줌.

이 증명서가 바로 JWT(Json Web Token).

이후의 요청은 ID/PW 없이 JWT만 보내면 "아, 너 로그인했지?"라고 인식.

🔧 JwtToken.java 클래스 역할

createToken(String userId, String role) : 로그인 후 토큰을 만듬

getUserId(String token) : 토큰에서 userId를 추출

validateToken(String token) : 유효한 토큰인지 검사 (만료 여부, 조작 여부 등)

🔐 JwtAuthenticationFilter.java — 요청마다 토큰 검사

📌 왜 필요한가?

모든 요청에서 "이 사용자가 로그인했는지" 확인하려면 필터가 필요.

🔧 동작 흐름

protected void doFilterInternal(HttpServletRequest request, ...)

요청 헤더에서 토큰을 꺼냄

유효하면 userId를 꺼내서 사용자 정보를 DB에서 가져옴

Spring Security에 로그인된 사용자로 등록

💡 예: 사용자가 마이페이지에 접근하면 이 필터가 작동해서 JWT 토큰이 유효한지 확인하고, 맞으면 해당 사용자의 정보를 인증.

📘 ShoseShop 설계 문서

1. 프로젝트 구조

ShoseShop-main/
├── build.gradle
├── settings.gradle
├── Dump20250509.sql            # DB 덤프
├── gradlew, gradlew.bat
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/example/shoseshop/
    │   │   ├── api/               # API용 컨트롤러
    │   │   ├── config/            # 보안 설정
    │   │   ├── controller/        # 웹 페이지 컨트롤러
    │   │   ├── domain/            # 도메인 클래스
    │   │   ├── dto/               # DTO 정의
    │   │   ├── entity/            # JPA Entity
    │   │   ├── jwtoken/           # JWT 관련 처리
    │   │   ├── repository/        # Repository 인터페이스
    │   │   └── service/           # 서비스 클래스
    │   └── resources/
    └── test/

2. 주요 컴포넌트 설명

2.1 Controller (controller/, api/)

사용자의 요청을 받고 화면 또는 JSON 응답을 제공

대표 클래스:

ProductController, BoardController : 상품, 게시판 처리

UserCartApiController, OrderConfirmApiController: API 응답 처리

2.2 Entity (entity/)

DB와 매핑되는 도메인 객체들

주요 클래스: Item, Cart, Order, Brend, BBS, Board

2.3 DTO (dto/)

Controller ↔ Service 사이에서 데이터를 전달할 때 쓰는 객체

왜 사용하냐면, Entity를 그대로 넘기면 보안이나 구조적으로 위험할 수 있어서임

주요 클래스: CartResponseDTO, OrderResponseDTO, StripePaymentItemDTO 등

2.4 Service (service/)

실제 로직을 처리하는 계층. DB 저장, 비즈니스 규칙 적용 같은 걸 담당

주요 클래스: ItemService, OrderService, BoardService

2.5 Repository (repository/)

JPA 기반 DB 접근을 담당. 쿼리를 자동 생성하거나 직접 정의할 수도 있음

주요 클래스: ItemRepository, OrderItemRepository, MemberRepository

3. 주요 기능 흐름

3.1 상품 구매 흐름

사용자가 상품 클릭 → ProductController

장바구니에 추가 → CartController

결제 진행 → StripePaymentService

주문 정보 저장 → OrderService, OrderItemRepository

주문 확인 페이지 이동 → OrderConfirmApiController

3.2 회원 로그인/등록

로그인/가입 처리: LoginController, RegisterController

JWT 기반 인증: jwtoken/ 디렉토리 활용

4. 데이터 흐름 (구매 기준)

    A[상품 선택] --> B[ProductController]
    B --> C[CartController]
    C --> D[StripePaymentService]
    D --> E[OrderService]
    E --> F[OrderItemRepository]
    F --> G[주문 확인 페이지]

5. 확장 및 유지보수 가이드

5.1 기능 추가 시

Controller → Service → Repository 순으로 구조 설계

필요한 경우 DTO를 만들어서 전달 구조를 깔끔하게 함

5.2 API 확장

api/ 디렉토리에 JSON 전용 컨트롤러 추가

JWT 인증은 jwtoken/에 있는 기능 활용

5.3 관리자 기능 추가 시

기존 BoardController, MemberController 코드 참고해서 확장

6. 성능 및 보안 고려사항

6.1 성능

Repository에서 @Query를 활용하면 커스텀 쿼리 튜닝도 가능

@Transactional 어노테이션은 데이터 정합성을 위해 꼭 필요한 부분 (주문, 결제 등)

6.2 보안

SecurityConfig.java를 통한 Spring Security 기반 구조 구성

JWT 기반 인증으로 로그인 상태 유지 가능 (jwtoken/ 사용)

7. 테스트 가이드

7.1 단위 테스트

DTO, Service에 대한 단위 테스트 작성 가능
Repository 쿼리 정확성 테스트도 필요

7.2 통합 테스트

장바구니 → 결제 → 주문 전체 흐름을 테스트
예외 상황 (재고 없음, 로그인 안함 등) 테스트도 추가해보는 게 좋음

