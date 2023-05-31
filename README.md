# 1. 프로젝트 개요

게시판 작성을 위한 REST API 개발

# 2. 사용기술 및 개발환경

Laguage: java
Framework: Spring boot
DB: MySQL

# 3 주요기능

1) 회원 가입(중복가입 방지, 이메일 인증)
2) 로그인 (아이디/비밀번호 검증)
3) 게시판 리스트 출력(목록, 좋아요, 조회수)
4) 게시글 등록, 수정, 삭제  

 검증 기능은 자바코드로 구성 되어 있으며, 팀 프로젝트 진행 시 스프링 인터셉터 사용 예정
 SQL 쿼리는 mybtis 또는 JPA 사용 예정
# 4 ERD

![이미지](https://github.com/trsnathon/spring_board/blob/main/ERD.PNG?raw=true)

5 # API 명세서

![이미지](https://raw.githubusercontent.com/trsnathon/spring_board/4e8f2f8a694dadc58d25d4b3e119c6afacd8c505/api%20%EB%AA%85%EC%84%B8%EC%84%9C.PNG)
