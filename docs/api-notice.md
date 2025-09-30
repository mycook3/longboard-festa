# 공지 API 문서

## 기본 정보
- 기본 URL: `http://<host>:<port>` (로컬 개발 기본값은 `http://localhost:8080`)
- 모든 응답은 `ApiResult` 래퍼로 반환됩니다.
  ```json
  {
    "data": { ... },
    "message": "string | null",
    "status": "SUCCESS" | "ERROR" | "LOG"
  }
  ```
- 요청/응답 본문은 기본적으로 `application/json` 을 사용합니다.
- 인증/인가 로직은 아직 적용되지 않았습니다.

## 도메인 규칙 요약
- 공지 중요도(`NoticeImportance`): `NORMAL`, `IMPORTANT`, `CRITICAL` (요청 시 생략하면 `NORMAL` 로 저장)
- 적용 시각(`applyAt`): `yyyy-MM-dd'T'HH:mm:ss` 형식의 `LocalDateTime` (생략하면 요청을 받은 서버 시각)
- 상단 고정(`pinned`): `true` 이면 공지 목록에서 상단 영역에 노출됩니다.
- 예약 공지: `applyAt` 이 현재 시각보다 과거일 경우 요청이 거절됩니다.

## 공지 생성
- **Method / Path**: `POST /api/v1/notices`
- **설명**: 신규 공지를 등록합니다.
- **요청 헤더**: `Content-Type: application/json`
- **요청 본문**
  ```json
  {
    "title": "string",          // 필수
    "content": "string",        // 필수
    "pinned": false,             // 선택 (기본값 false)
    "importance": "IMPORTANT",  // 선택 (기본값 NORMAL)
    "applyAt": "2025-10-02T10:00:00" // 선택 (생략 시 현재 시각)
  }
  ```
- **성공 응답 (예시)**
  ```json
  {
    "data": {
      "id": 1,
      "title": "결선 일정 안내",
      "content": "결선은 10월 2일 오전 10시에 시작합니다.",
      "importance": "IMPORTANT",
      "pinned": false,
      "applyAt": "2025-10-02T10:00:00",
      "createdAt": "2025-09-01T09:00:00",
      "updatedAt": "2025-09-01T09:00:00"
    },
    "message": null,
    "status": "SUCCESS"
  }
  ```

## 공지 목록 조회
- **Method / Path**: `GET /api/v1/notices`
- **설명**: 상단 고정 공지와 일반 공지를 분리해 조회합니다.
- **Query Parameters**
  | 이름 | 타입 | 기본값 | 설명 |
  | --- | --- | --- | --- |
  | `page` | int | `0` | 일반 공지의 페이지 번호 (0부터 시작) |
  | `size` | int | `10` | 페이지당 일반 공지 개수 |
- **성공 응답 (요약 구조)**
  ```json
  {
    "data": {
      "pinned": [
        {
          "id": 5,
          "title": "현장 안내",
          "importance": "CRITICAL",
          "pinned": true,
          "applyAt": "2025-09-20T07:00:00",
          "createdAt": "2025-09-18T12:00:00",
          "updatedAt": "2025-09-18T12:00:00"
        }
      ],
      "publics": {
        "contents": [
          {
            "id": 4,
            "title": "리허설 공지",
            "importance": "NORMAL",
            "pinned": false,
            "applyAt": "2025-09-21T09:00:00",
            "createdAt": "2025-09-19T10:00:00",
            "updatedAt": "2025-09-19T10:00:00"
          }
        ],
        "page": 0,
        "size": 10,
        "totalElements": 6,
        "totalPages": 1,
        "last": true
      }
    },
    "message": null,
    "status": "SUCCESS"
  }
  ```
- **비고**
  - `pinned` 배열에는 상단 고정된 공지가 생성일 내림차순으로 모두 포함됩니다.
  - `publics.contents` 배열에는 일반 공지의 요약 정보만 존재하며 `content` 본문은 포함되지 않습니다.

## 공지 상세 조회
- **Method / Path**: `GET /api/v1/notices/{noticeId}`
- **설명**: 공지 본문을 포함한 상세 정보를 조회합니다.
- **경로 변수**: `noticeId` (Long)
- **성공 응답**: 공지 생성 응답과 동일한 `NoticeResponse` 구조를 반환합니다.

## 공지 수정
- **Method / Path**: `PUT /api/v1/notices/{noticeId}`
- **설명**: 기존 공지 정보를 수정합니다.
- **요청 본문**: 공지 생성과 동일한 필드 구조 (`title`, `content`는 필수)
- **성공 응답**: 수정된 공지의 `NoticeResponse` 구조
- **검증 규칙**
  - `applyAt`이 현재 시각보다 과거라면 400 에러(`InvalidNoticeScheduleException`)
  - 존재하지 않는 공지 ID는 404 에러(`NoticeNotFoundException`)

## 공지 삭제
- **Method / Path**: `DELETE /api/v1/notices/{noticeId}`
- **설명**: 공지 한 건을 삭제합니다.
- **응답 본문**
  ```json
  {
    "data": null,
    "message": null,
    "status": "SUCCESS"
  }
  ```
- **에러 케이스**: 존재하지 않는 ID는 404 (`NoticeNotFoundException`)

## 에러 처리 참고
- 현재 글로벌 예외 처리기는 구현되어 있지 않아, 스프링 기본 예외 응답 또는 스택트레이스가 노출될 수 있습니다.
- 운영 반영 전에는 `@ControllerAdvice`를 통한 공통 에러 응답 정의를 권장합니다.
