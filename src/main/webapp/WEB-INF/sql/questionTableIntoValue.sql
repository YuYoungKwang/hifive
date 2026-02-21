-- 1. 카테고리 이름을 담을 수 있게 크기를 10에서 50으로 키웁니다.
ALTER TABLE Questions_categories MODIFY (question_name VARCHAR2(50));

-- 2. 카테고리 번호를 10번 이상 넣을 수 있게 숫자를 1자리에서 3자리로 키웁니다.
ALTER TABLE Questions_categories MODIFY (question_no NUMBER(3));

-- 3. (참고) 다른 테이블들도 번호를 넣어야 하므로 미리 키워두는 것이 좋습니다.
ALTER TABLE Questions MODIFY (question_no NUMBER(3), question_number NUMBER(3));
ALTER TABLE Qusetion_options MODIFY (question_no NUMBER(3), question_number NUMBER(3));


-- ============= Questions_categories =============

INSERT INTO Questions_categories (question_no, question_name) VALUES (1, '선호지형');
INSERT INTO Questions_categories (question_no, question_name) VALUES (2, '계획스타일');
INSERT INTO Questions_categories (question_no, question_name) VALUES (3, '이동수단');
INSERT INTO Questions_categories (question_no, question_name) VALUES (4, '숙소스타일');
INSERT INTO Questions_categories (question_no, question_name) VALUES (5, '예산규모');
INSERT INTO Questions_categories (question_no, question_name) VALUES (6, '동행자');
INSERT INTO Questions_categories (question_no, question_name) VALUES (7, '식도락');
INSERT INTO Questions_categories (question_no, question_name) VALUES (8, '여행목적');
INSERT INTO Questions_categories (question_no, question_name) VALUES (9, '활동강도');
INSERT INTO Questions_categories (question_no, question_name) VALUES (10, '선호분위기');

-- ============= Question =============

INSERT INTO Questions (question_no, question_number, question_content, question_img) VALUES (1, 1, '어떤 여행지를 선호하세요?', 'q1.jpg');
INSERT INTO Questions (question_no, question_number, question_content, question_img) VALUES (2, 2, '여행 계획 스타일은?', 'q2.jpg');
INSERT INTO Questions (question_no, question_number, question_content, question_img) VALUES (3, 3, '주로 어떤 이동 수단을 쓰나요?', 'q3.jpg');
INSERT INTO Questions (question_no, question_number, question_content, question_img) VALUES (4, 4, '숙소는 어떤 스타일을 선호하시나요?', 'q4.jpg');
INSERT INTO Questions (question_no, question_number, question_content, question_img) VALUES (5, 5, '여행 예산 스타일은 어떤가요?', 'q5.jpg');
INSERT INTO Questions (question_no, question_number, question_content, question_img) VALUES (6, 6, '주로 누구와 여행하시나요?', 'q6.jpg');
INSERT INTO Questions (question_no, question_number, question_content, question_img) VALUES (7, 7, '여행 중 음식 스타일은 어떤가요?', 'q7.jpg');
INSERT INTO Questions (question_no, question_number, question_content, question_img) VALUES (8, 8, '이번 여행의 가장 큰 목적은?', 'q8.jpg');
INSERT INTO Questions (question_no, question_number, question_content, question_img) VALUES (9, 9, '평소 여행의 활동 강도는 어느 정도인가요?', 'q9.jpg');
INSERT INTO Questions (question_no, question_number, question_content, question_img) VALUES (10, 10, '선호하는 여행 시기나 분위기는?', 'q10.jpg');

-- Q1. 선호지형
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (1, 1, 1, '산 · 계곡 · 자연', 'opt1_1.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (2, 1, 1, '바다 · 해변 · 섬', 'opt1_2.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (3, 1, 1, '도시 · 골목 · 문화', 'opt1_3.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (4, 1, 1, '시골 · 온천 · 전원', 'opt1_4.jpg');

-- Q2. 계획스타일
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (5, 2, 2, '분 단위 계획형', 'opt2_1.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (6, 2, 2, '주요 스팟만 정하는 형', 'opt2_2.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (7, 2, 2, '발길 닿는 대로 즉흥형', 'opt2_3.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (8, 2, 2, '남이 짜주는 대로 따라 가는 형', 'opt2_4.jpg');

-- Q3. 이동수단
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (9, 3, 3, '자가용 · 렌트카', 'opt3_1.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (10, 3, 3, '대중교통', 'opt3_2.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (11, 3, 3, '택시', 'opt3_3.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (12, 3, 3, '뚜벅이', 'opt3_4.jpg');

-- Q4. 숙소스타일
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (13, 4, 4, '호텔 · 리조트', 'opt4_1.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (14, 4, 4, '펜션 · 에어비앤비', 'opt4_2.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (15, 4, 4, '캠핑 · 글램핑', 'opt4_3.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (16, 4, 4, '게스트하우스', 'opt4_4.jpg');

-- Q5. 예산규모
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (17, 5, 5, '알뜰 여행', 'opt5_1.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (18, 5, 5, '가성비 우선', 'opt5_2.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (19, 5, 5, '예산보다 만족도', 'opt5_3.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (20, 5, 5, '예산 제한 없는 플렉스', 'opt5_4.jpg');

-- Q6. 동행자
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (21, 6, 6, '혼자(혼행)', 'opt6_1.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (22, 6, 6, '연인 · 커플', 'opt6_2.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (23, 6, 6, '가족', 'opt6_3.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (24, 6, 6, '친구 · 지인', 'opt6_4.jpg');

-- Q7. 식도락
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (25, 7, 7, '미식 · 맛집 탐방', 'opt7_1.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (26, 7, 7, '현지 음식 위주', 'opt7_2.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (27, 7, 7, '간편식 · 편의점', 'opt7_3.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (28, 7, 7, '해산물 · 고기 특화', 'opt7_4.jpg');

-- Q8. 여행목적
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (29, 8, 8, '완전한 힐링 · 휴식', 'opt8_1.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (30, 8, 8, '액티비티 · 도전', 'opt8_2.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (31, 8, 8, '사진 · 감성', 'opt8_3.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (32, 8, 8, '문화 · 역사 탐방', 'opt8_4.jpg');

-- Q9. 활동강도
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (33, 9, 9, '숙소 근처에서만 머물기', 'opt9_1.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (34, 9, 9, '하루에 1~2곳만 천천히', 'opt9_2.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (35, 9, 9, '부지런히 여러 곳 찍기', 'opt9_3.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (36, 9, 9, '아침부터 밤까지 꽉찬 일정', 'opt9_4.jpg');

-- Q10. 선호분위기
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (37, 10, 10, '사람 북적이는 핫플레이스', 'opt10_1.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (38, 10, 10, '한적하고 조용한 숨은 명소', 'opt10_2.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (39, 10, 10, '계절감이 뚜렷한 곳', 'opt10_3.jpg');
INSERT INTO Qusetion_options (option_id, question_number, question_no, option_text, option_img) VALUES (40, 10, 10, '야경이 예쁜 밤 중심', 'opt10_4.jpg');

COMMIT;

SELECT
Q.QUESTION_NUMBER AS "번호",
C.QUESTION_NAME AS "카테고리",
Q.QUESTION_CONTENT AS "질문내용",
O.OPTION_TEXT AS "선택지"
FROM QUESTIONS Q
JOIN QUESTIONS_CATEGORIES C ON Q.QUESTION_NO = C.QUESTION_NO
JOIN QUSETION_OPTIONS O ON Q.QUESTION_NUMBER = O.QUESTION_NUMBER
ORDER BY Q.QUESTION_NUMBER, O.OPTION_ID;