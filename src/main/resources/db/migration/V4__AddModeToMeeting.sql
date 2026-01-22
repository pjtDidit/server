-- 1. 일단 mode 컬럼을 null 허용으로 추가 (데이터 꼬임 방지)
ALTER TABLE meetings ADD COLUMN mode VARCHAR(20) DEFAULT 'CHAT';

-- 2. (선택사항) 기존 status가 ENUM이라 자바랑 안 맞으면 수정
-- ALTER TABLE meetings MODIFY COLUMN status VARCHAR(20) NOT NULL;