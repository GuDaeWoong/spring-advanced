# SPRING ADVANCED


# Lv1 코드 개선

### 1-1. 문제 인식 및 정의
현재 회원가입 서비스 로직은 불필요한 연산을 수행하고 있습니다. 회원 정보 저장 전에 이메일 중복 검증이 제대로 이루어지지 않아, 이미 존재하는 이메일로 회원가입을 시도할 경우에 비밀번호 인코딩, 사용자 권한 삽입 작업이 실행되는 문제가 있습니다.

기존 코드 순서:

1. 비밀번호 인코딩
2. 유저 룰 삽입
3. 이메일 중복 검증 (이메일이 기존에 존재하는 값인지 확인)
4. 사용자 정보를 객체화
5. Repository를 통해 사용자 정보 저장
6. 사용자 정보를 토큰화

### 2. 해결 방안
개선점으로는 이메일 중복 검증을 최우선으로 수행하는 것입니다. 서비스 진입 시점에 이메일 중복 여부를 먼저 확인하여, 만약 중복된 이메일이라면 불필요한 다음 단계의 연산을 즉시 중단해야 합니다.

해결된 코드의 순서:

1. 이메일 중복 검증 (가장 먼저 수행)
  -  만약 이메일이 이미 존재한다면, 즉시 회원가입 실패 응답을 반환하고 로직 종료
2. 비밀번호 인코딩
3. 유저 룰 삽입
4. 사용자 정보를 객체화
5. Repository를 통해 사용자 정보 저장
6. 사용자 정보를 토큰화
  
이러한 순서 변경을 통해 이메일 중복 시 발생하는 불필요한 비밀번호 인코딩 및 사용자 권한 삽입 작업을 방지할 수 있습니다.

### 3. 해결 완료
이메일 중복 검증 로직의 위치를 회원가입 서비스 메서드의 가장 앞쪽으로 이동하여 코드의 효율성을 높였습니다.

<br>
<br>

### 1-2. 문제 인식 및 정의
WeatherClient 클래스 내 getTodayWeather() 메서드는 복잡한 if-else 구조를 가지고 있었습니다. 이 구조는 코드의 가독성을 떨어뜨리고, 향후 유지보수를 어렵게 만드는 문제를 야기했습니다. 특히, 예외 처리 로직이 중첩된 if-else 블록 안에 있어 코드의 흐름을 파악하기 힘들었습니다.

### 2. 해결 방안( SRP(단일 책임 원칙) 적용을 통한 예외 처리 분리 )
객체지향 설계 원칙 중 하나인 SRP(단일 책임 원칙)는 클래스나 모듈이 오직 하나의 책임만 가져야 한다는 원칙입니다. 이번 개선에서는 완벽한 SRP 적용보다는, 예외 처리 로직을 분리하여 getTodayWeather() 메서드의 가독성을 높이는 데 중점을 두었습니다.

        // 원본 코드
        WeatherDto[] weatherArray = responseEntity.getBody();
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new ServerException("날씨 데이터를 가져오는데 실패했습니다. 상태 코드: " + responseEntity.getStatusCode());
        } else {
            if (weatherArray == null || weatherArray.length == 0) {
                throw new ServerException("날씨 데이터가 없습니다.");
            }
        }

        // 수정된 코드
        WeatherDto[] weatherArray = responseEntity.getBody();
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new ServerException("날씨 데이터를 가져오는데 실패했습니다. 상태 코드: " + responseEntity.getStatusCode());
        }
        if (weatherArray == null || weatherArray.length == 0) {
            throw new ServerException("날씨 데이터가 없습니다.");
        }

수정된 코드에서는 각 예외 처리 조건을 별도의 if 블록으로 분리했습니다. 
- 가독성 향상: 각 예외 조건이 명확히 분리되어 코드의 흐름을 이해하기 쉬워집니다.
- 유지보수 용이성: 특정 예외 처리 로직을 수정하거나 추가할 때, 다른 조건에 영향을 주지 않고 해당 부분만 변경할 수 있습니다.
- 조기 종료: 잘못된 상태 코드나 데이터가 없을 경우, 더 이상 코드 실행을 진행하지 않고 즉시 예외를 발생시켜 불필요한 연산을 방지합니다.

### 3. 해결 완료
getTodayWeather() 메서드의 중첩된 if-else 구조를 독립적인 if 블록으로 분리하여 예외 처리 로직의 가독성을 크게 향상시켰습니다. 


<br>
<br>


### 1-3. 문제 인식 및 정의
현재 org.example.expert.domain.user.service 패키지의 UserService 클래스 내 changePassword() 메서드에는 새 비밀번호 생성 시 적용되어야 할 유효성 검증 조건(길이 8자 이상, 숫자 포함, 대문자 포함)이 포함되어 있습니다. 이러한 유효성 검증 로직을 서비스 레이어에서 직접 처리하는 것은 코드의 결합도를 높이고 서비스 메서드를 불필요하게 복잡하게 만듭니다.

### 2. 해결 방안( DTO를 활용한 유효성 검증 분리 )
이 문제에 대한 해결책은 데이터 전송 객체(DTO)에서 유효성 검증을 담당하도록 했습니다.Spring 환경에서는 @Size와 @Pattern 같은 Bean Validation 어노테이션을 활용하여 DTO 필드에 직접 유효성 검증 조건을 걸어 줄 수 있습니다

    @Size(min = 8, message = "새 비밀번호는 8자 이상이어야 합니다.")
    @Pattern(regexp = ".*\\d.*", message = "새 비밀번호는 숫자가 포함되야 합니다")
    @Pattern(regexp = ".*[A-Z].*", message = "새 비밀번호는 대문자를 포함해야 합니다.")
    private String newPassword;

이러한 변경을 통해 다음과 같은 이점을 얻을 수 있습니다:

- 코드 간결화: UserService의 changePassword() 메서드에서 복잡한 유효성 검증 로직을 제거하여 서비스 코드를 더욱 간결하고 명확하게 만듭니다.그리고 서비스는 비즈니스 로직에만 집중할 수 있습니다.
- 선제적 검증: 컨트롤러 레이어에서 DTO를 받을 때 자동으로 유효성 검증이 수행되므로 서비스 레이어에 도달하기 전에 잘못된 데이터를 걸러낼 수 있습니다.


### 3. 해결 완료
새 비밀번호 유효성 검증 로직을 UserChangePasswordRequest DTO로 이동하고 @Size, @Pattern 어노테이션을 사용하여 코드의 간결성과 가독성을 크게 향상시켰습니다. 이제 서비스 레이어는 핵심 비즈니스 로직에만 집중하고, 유효성 검증은 DTO와 프레임워크의 도움으로 효율적으로 처리할 수 있습니다.


<br>
<br>


# Lv2. N+1 문제

### 1. 문제 인식 및 정의
현재 JPQL 쿼리 @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")는 모든 Todo 항목을 가져오며, 연결된 User 항목을 함께 가져오려 합니다. 하지만 User 엔티티의 FetchType이 LAZY로 설정되어 있어, Todo 항목을 조회할 때마다 해당 Todo에 연결된 User 정보를 가져오기 위해 추가적인 쿼리가 발생합니다.  
 예를 들어 todo 항목이 10개가 있어서 불러올때 10개의 User정보를 불러오기위해 한번씩 해서 10개의 추가 쿼리가 발생한다.위 쿼리를 발생시키기 위해 11번의 쿼리가 발생하는 문제가 있습니다.

### 2. 해결 방안

		1. FetchType.EAGER 사용
		   - 유저 엔티티의 fetch전략을 Eager로 변경하여 todo를 가져올때 항상 User도 함께 로드
		   장점 : 연관된 데이터를 항상 로드한다
		   단점 : 불필요한 데이터도 함께 로드하면서 메모리가 낭비된다
		2. JPQL의 JOIN FETCH 사용
		   - JPQL 쿼리에서 JOIN FETCH를 사용하여 연관된 엔티티를 한 번의 쿼리로 가져온다
		   장점 : 필요한 데이터만 가져올 수 있어서 유연성을 높일 수 있다
		   단점 : 쿼리를 직접 작성해야해서 코드가 복잡해질 수 있다
		3. @EntityGraph 사용
		   - @EntityGraph를 사용하여 특정 엔티티를 로드할 때 어떤 연관된 엔티티를 함께 로드할지를 명시적으로 지정
		   장점 : 코드가 간결해지고 가독성이 높아진다
		   단점 : 복잡한 쿼리에는 한계가 있다고 합니다
		4. DTO Projection 사용
		   - 필요한 데이터만을 담은 DTO를 사용하여 쿼리 결과를 반환
		   장점 : 필요한 데이터만을 가져와 성능을 최적화할 수 있다
		   단점 : 없다고 생각이 들지만 DTO클래스를 추가로 작성해야한다.


### 3. 해결 완료

@Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")   
기존 쿼리가 Todo와 User 정보만 필요로 하는 것을 고려하여, @EntityGraph를 사용해 Todo를 가져올 때 연관된 User 엔티티를 즉시 로딩하도록 설정했습니다. @EntityGraph(attributePaths = "user")


<br>
<br>


# Lv3. 테스트코드

### 3-1. 문제 인식 및 정의
org.example.expert.config 패키지의 PassEncoderTest 클래스에 포함된 테스트 케이스가 정상적으로 동작하지 않는 문제가 발생했습니다. 비밀번호 매칭 여부를 확인하는 assertTrue(matches); 구문이 false를 반환하며 테스트가 실패하고 있었습니다.

        //실패 코드
        // when
        boolean matches = passwordEncoder.matches(encodedPassword,rawPassword);
        // then
        assertTrue(matches);

### 2. 해결 방안
passwordEncoder.matches() 메서드의 인자 전달 순서가 잘못된 것으로 파악되었습니다. 
matches 메서드의 내부 로직 (BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword))을 고려할 때, rawPassword (평문 비밀번호)가 첫 번째 인자로, encodedPassword (인코딩된 비밀번호)가 두 번째 인자로 전달되어야 합니다. 그러나 테스트 코드에서는 이 순서가 반대로 되어 있었습니다. PassEncoderTest 클래스의 when 섹션에서 passwordEncoder.matches() 호출 시 인자의 순서를 올바르게 변경해야 합니다.

        // when
        boolean matches = passwordEncoder.matches(rawPassword,encodedPassword);

        // then
        assertTrue(matches);

### 3. 해결 완료
PassEncoderTest 클래스 내 passwordEncoder.matches() 호출 시 인자 순서를 rawPassword 다음에 encodedPassword가 오도록 수정하여 문제를 해결했습니다.


<br>
<br>


### 3-2. 문제 인식 및 정의
org.example.expert.domain.manager.service 패키지의 ManagerServiceTest 클래스에서 manager_목록_조회_시_Todo가_없다면_NPE_에러를_던진다() 테스트 케이스가 NullPointerException을 발생시킬 수 있는 문제를 안고 있었습니다.  
테스트 코드는 todoRepository.findById(todoId)가 Optional.empty()를 반환할 때 InvalidRequestException이 발생하는지 검증하려 했으나, 실제 서비스 로직에서는 todo.getUser().getId() 호출 시 todo.getUser()가 null일 경우 NullPointerException이 발생할 수 있었습니다. 테스트가 기대하는 예외 메시지("Manager not found")와 실제 발생하는 예외(NullPointerException)가 달라서 테스트가 실패합니다.

    //실패 코드
    @Test
    public void manager_목록_조회_시_Todo가_없다면_NPE_에러를_던진다() {
        // given
        long todoId = 1L;
        given(todoRepository.findById(todoId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.getManagers(todoId));
        assertEquals("Manager not found", exception.getMessage());
    }

    //검증 대상
    Todo todo = todoRepository.findById(todoId)
        .orElseThrow(() -> new InvalidRequestException("Todo not found"));

    if (!ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
        throw new InvalidRequestException("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다.");
    }

### 2. 해결 방안
이 문제를 해결하기 위해 두가지 수정이 필요했습니다.
- Todo가 없을 때 발생하는 예외 메시지는 서비스 로직에서 명확히 "Todo not found"로 정의되어 있습니다. -> 테스트코드도 동일하게 "Todo not found" 변환해주었습니다.
- todo.getUser()가 null일 경우를 대비하여 아래 조건문에 null이 들어왔을 경우를 추가로 제어해주었습니다.


        // 수정 코드
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> managerService.getManagers(todoId));
        assertEquals("Todo not found", exception.getMessage());
        // Todo가 없을때 발생하는 예외 메세지이므로 Todo not found 로 변환

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        // todo.getUser() == null 추가 해주지않으면 nullpointexception이 발생
        if (todo.getUser() == null || !ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
        throw new InvalidRequestException("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다.");
        }



### 3. 해결 완료
ManagerServiceTest의 테스트 케이스와 ManagerService의 비즈니스 로직을 모두 수정하여 문제를 해결했습니다.
- 테스트 코드에서는 assertEquals의 기대 메시지를 서비스 로직과 일치하는 "Todo not found"로 변경했습니다.
- 서비스 로직에서는 todo.getUser() 호출 전에 todo.getUser() == null 조건을 추가하여 NullPointerException 발생 가능성을 원천적으로 차단했습니다.


<br>
<br>

