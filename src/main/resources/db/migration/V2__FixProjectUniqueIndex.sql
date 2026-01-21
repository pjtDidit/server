-- 1) 기존 repo_full_name 단일 UNIQUE가 있으면 제거
-- (인덱스 이름은 실제 이름에 맞춰 수정)
ALTER TABLE projects DROP INDEX uk_projects_repo_full_name;

-- 2) 미삭제면 1, 삭제면 NULL이 되는 컬럼 추가
ALTER TABLE projects
  ADD COLUMN active_key TINYINT
  GENERATED ALWAYS AS (CASE WHEN deleted_at IS NULL THEN 1 ELSE NULL END) STORED;

-- 3) (repo_full_name, active_key) 유니크 인덱스
CREATE UNIQUE INDEX uq_projects_repo_active
ON projects (repo_full_name, active_key);