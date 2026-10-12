-- ============================================================
-- 增量迁移脚本：软删除 + 在职状态（2026-10-12）
-- 用途：在已有数据的 web 库上执行，无需重建库、不丢数据
-- 说明：若执行时报 "Duplicate column name" 表示已迁移过，跳过该句即可
-- ============================================================
USE web;

-- 1. 员工表：加在职状态（1-在职 / 0-离职，删除=标记离职留档）
ALTER TABLE emp
    ADD COLUMN status TINYINT NOT NULL DEFAULT 1
    COMMENT '在职状态: 1-在职 / 0-离职(留档不显示)' AFTER entry_date;

-- 2. 部门表：加逻辑删除标志（0-正常 / 1-已删除留档不显示）
ALTER TABLE dept
    ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0
    COMMENT '是否已删除: 0-正常 / 1-已删除(留档不显示)';

-- 3. 部门名称唯一索引降级为普通索引
--    （软删除后部门名保留在库里，保留唯一索引会挡住新建同名部门；
--      重名校验改由业务层在"未删除部门"范围内进行）
ALTER TABLE dept
    DROP INDEX uk_dept_name,
    ADD INDEX idx_dept_name (name);

-- ============================================================
-- 迁移后数据回填（一般不需要：新列默认值已覆盖，存量员工=在职、部门=正常）
-- UPDATE emp SET status = 1  WHERE status IS NULL;
-- UPDATE dept SET deleted = 0 WHERE deleted IS NULL;
-- ============================================================
