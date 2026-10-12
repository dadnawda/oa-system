-- ============================================================
-- 企业核心业务管理系统（OA）数据库初始化脚本
-- 数据库: web    字符集: utf8mb4    引擎: InnoDB
-- ============================================================

DROP DATABASE IF EXISTS web;
CREATE DATABASE web DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE web;

-- ------------------------------------------------------------
-- 1. 用户表 user —— 登录账户，主键即员工工号（与 emp 1对1）
-- ------------------------------------------------------------
CREATE TABLE `user` (
    `emp_id`      BIGINT       NOT NULL                COMMENT '员工工号(主键,逻辑外键 -> emp.id,登录名)',
    `password`    VARCHAR(100) NOT NULL                COMMENT '登录密码(MD5/BCrypt加密存储)',
    `role`        VARCHAR(16)  NOT NULL DEFAULT 'staff' COMMENT '角色: admin-管理员 / staff-普通员工',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`emp_id`)
) ENGINE=InnoDB COMMENT='用户表(主键=员工工号,与员工1对1)';

-- ------------------------------------------------------------
-- 2. 部门表 dept
-- ------------------------------------------------------------
CREATE TABLE `dept` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID(部门号)',
    `name`        VARCHAR(32)  NOT NULL                COMMENT '部门名称',
    `description` VARCHAR(200) DEFAULT NULL            COMMENT '部门描述',
    `deleted`     TINYINT      NOT NULL DEFAULT 0      COMMENT '是否已删除: 0-正常 / 1-已删除(留档不显示)',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_dept_name` (`name`)
) ENGINE=InnoDB COMMENT='部门表(逻辑删除留档)';

-- ------------------------------------------------------------
-- 3. 员工表 emp —— 员工信息管理主体
-- ------------------------------------------------------------
CREATE TABLE `emp` (
    `id`          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`        VARCHAR(32)    NOT NULL                COMMENT '员工姓名',
    `gender`      TINYINT        NOT NULL DEFAULT 1      COMMENT '性别: 1-男 / 2-女',
    `phone`       VARCHAR(11)    DEFAULT NULL            COMMENT '手机号',
    `job`         VARCHAR(50)    DEFAULT NULL            COMMENT '职位',
    `image`       VARCHAR(255)   DEFAULT NULL            COMMENT '头像图片URL',
    `entry_date`  DATE           DEFAULT NULL            COMMENT '入职日期',
    `status`      TINYINT        NOT NULL DEFAULT 1      COMMENT '在职状态: 1-在职 / 0-离职(留档不显示)',
    `dept_id`     BIGINT         DEFAULT NULL            COMMENT '所属部门ID(逻辑外键 -> dept.id)',
    `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_emp_dept_id` (`dept_id`)
) ENGINE=InnoDB COMMENT='员工表';

-- ------------------------------------------------------------
-- 4. 工作经历表 emp_expr —— 员工的一对多子表
-- ------------------------------------------------------------
CREATE TABLE `emp_expr` (
    `id`         BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `emp_id`     BIGINT  NOT NULL                COMMENT '员工ID(逻辑外键 -> emp.id)',
    `begin_date` DATE    DEFAULT NULL            COMMENT '开始时间',
    `end_date`   DATE    DEFAULT NULL            COMMENT '结束时间',
    `dept_id`    BIGINT  DEFAULT NULL            COMMENT '所在部门ID(逻辑外键 -> dept.id)',
    `job`        VARCHAR(50) DEFAULT NULL        COMMENT '担任职位',
    PRIMARY KEY (`id`),
    KEY `idx_emp_expr_emp_id` (`emp_id`)
) ENGINE=InnoDB COMMENT='工作经历表(员工子表)';

-- ------------------------------------------------------------
-- 5. 公告表 notice —— 公告发布与查看
-- ------------------------------------------------------------
CREATE TABLE `notice` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`        VARCHAR(100)  NOT NULL                COMMENT '公告标题',
    `content`      TEXT                                  COMMENT '公告内容',
    `user_id`      BIGINT        NOT NULL                COMMENT '发布人ID(逻辑外键 -> user.id)',
    `publish_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `status`       TINYINT       NOT NULL DEFAULT 1      COMMENT '状态: 0-草稿 / 1-已发布',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_notice_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='公告表';

-- ------------------------------------------------------------
-- 6. 工资表 salary —— 员工月度工资发放记录
-- ------------------------------------------------------------
CREATE TABLE `salary` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `emp_id`       BIGINT        NOT NULL                COMMENT '员工ID(逻辑外键 -> emp.id)',
    `salary_month` VARCHAR(7)    NOT NULL                COMMENT '工资月份(yyyy-MM)',
    `base_salary`  DECIMAL(10,2) NOT NULL DEFAULT 0      COMMENT '基本工资(元)',
    `bonus`        DECIMAL(10,2) NOT NULL DEFAULT 0      COMMENT '奖金/绩效(元)',
    `deduction`    DECIMAL(10,2) NOT NULL DEFAULT 0      COMMENT '扣款(社保/缺勤等,元)',
    `actual_pay`   DECIMAL(10,2) NOT NULL DEFAULT 0      COMMENT '实发工资(元)',
    `pay_date`     DATE          DEFAULT NULL            COMMENT '发放日期',
    `status`       TINYINT       NOT NULL DEFAULT 0      COMMENT '状态: 0-待发放 / 1-已发放',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_salary_emp_month` (`emp_id`, `salary_month`)
) ENGINE=InnoDB COMMENT='工资表(员工月度记录)';

-- ------------------------------------------------------------
-- 7. 请假审批表 leave_request —— 员工请假 + 管理员审批
-- ------------------------------------------------------------
CREATE TABLE `leave_request` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `emp_id`         BIGINT       NOT NULL                COMMENT '请假员工ID(逻辑外键 -> emp.id)',
    `leave_type`     TINYINT      NOT NULL DEFAULT 1      COMMENT '请假类型: 1-事假 / 2-病假 / 3-年假 / 4-婚假 / 5-其他',
    `begin_date`     DATE         NOT NULL                COMMENT '开始日期',
    `end_date`       DATE         NOT NULL                COMMENT '结束日期',
    `days`           DECIMAL(4,1) NOT NULL                COMMENT '请假天数(支持0.5天)',
    `reason`         VARCHAR(500) DEFAULT NULL            COMMENT '请假原因',
    `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '审批状态: 0-待审批 / 1-已通过 / 2-已驳回',
    `approver_id`    BIGINT       DEFAULT NULL            COMMENT '审批人ID(逻辑外键 -> user.id,管理员)',
    `approve_time`   DATETIME     DEFAULT NULL            COMMENT '审批时间',
    `approve_remark` VARCHAR(200) DEFAULT NULL            COMMENT '审批意见',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_leave_emp_id` (`emp_id`),
    KEY `idx_leave_status` (`status`)
) ENGINE=InnoDB COMMENT='请假审批表';

-- ------------------------------------------------------------
-- 8. 职位调整申请表 job_transfer —— 员工申请调岗/调职 + 管理员审核
-- ------------------------------------------------------------
CREATE TABLE `job_transfer` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `emp_id`         BIGINT       NOT NULL                COMMENT '申请员工ID(逻辑外键 -> emp.id)',
    `current_job`    VARCHAR(50)  DEFAULT NULL            COMMENT '当前职位(申请时快照)',
    `target_job`     VARCHAR(50)  NOT NULL                COMMENT '申请调整后的职位',
    `target_dept_id` BIGINT       DEFAULT NULL            COMMENT '目标部门ID(逻辑外键 -> dept.id,可空=只调职位不换部门)',
    `reason`         VARCHAR(500) DEFAULT NULL            COMMENT '申请原因',
    `status`         TINYINT      NOT NULL DEFAULT 0      COMMENT '审批状态: 0-待审批 / 1-已通过 / 2-已驳回',
    `approver_id`    BIGINT       DEFAULT NULL            COMMENT '审批人ID(逻辑外键 -> user.id,管理员)',
    `approve_time`   DATETIME     DEFAULT NULL            COMMENT '审批时间',
    `approve_remark` VARCHAR(200) DEFAULT NULL            COMMENT '审批意见',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_job_transfer_emp_id` (`emp_id`),
    KEY `idx_job_transfer_status` (`status`)
) ENGINE=InnoDB COMMENT='职位调整申请表(调岗审核)';

-- ============================================================
-- 初始化测试数据
-- ============================================================

-- 用户（主键=员工工号即登录名，姓名从 emp 表联查，密码均为 123456 的 MD5；员工1=管理员，员工2=普通员工）
INSERT INTO `user` (`emp_id`, `password`, `role`) VALUES
(1, 'e10adc3949ba59abbe56e057f20f883e', 'admin'),
(2, 'e10adc3949ba59abbe56e057f20f883e', 'staff');

-- 部门
INSERT INTO `dept` (`name`, `description`) VALUES
('研发部',   '负责产品研发与技术维护'),
('市场部',   '负责市场推广与客户运营'),
('人事部',   '负责招聘、培训与员工关系'),
('财务部',   '负责财务核算与预算管理');

-- 员工（基本工资统一通过 salary 表按月设置）
INSERT INTO `emp` (`name`, `gender`, `phone`, `job`, `entry_date`, `dept_id`) VALUES
('张伟',  1, '13900000001', 'Java工程师',   '2023-03-01', 1),
('李娜',  2, '13900000002', '前端工程师',   '2023-06-15', 1),
('王强',  1, '13900000003', '市场专员',     '2024-01-10', 2),
('赵敏',  2, '13900000004', '人事主管',     '2022-09-01', 3),
('刘洋',  1, '13900000005', '会计',         '2023-11-20', 4);

-- 工作经历（dept_id 关联内部部门）
INSERT INTO `emp_expr` (`emp_id`, `begin_date`, `end_date`, `dept_id`, `job`) VALUES
(1, '2020-07-01', '2023-02-28', 1, '后端开发实习生'),
(2, '2021-07-01', '2023-06-14', 1, '前端实习生'),
(4, '2019-03-01', '2022-08-31', 3, '人事专员');

-- 公告
INSERT INTO `notice` (`title`, `content`, `user_id`, `status`) VALUES
('关于国庆节放假安排的通知', '根据国务院办公厅通知精神，2026年国庆节放假时间为10月1日至10月7日，共7天。', 1, 1),
('新一期员工技能培训报名开始', '本期培训主题为 Spring Boot 4 全栈开发，请有意向的员工于本周五前在系统内报名。', 1, 1),
('办公室空调维护通知', '本周六上午将进行中央空调例行维护，届时办公区域空调暂停使用。', 1, 0);

-- 工资（2026-09 月度记录，前3人已发放）
INSERT INTO `salary` (`emp_id`, `salary_month`, `base_salary`, `bonus`, `deduction`, `actual_pay`, `pay_date`, `status`) VALUES
(1, '2026-09', 12000.00, 3000.00, 1500.00, 13500.00, '2026-10-10', 1),
(2, '2026-09', 10500.00, 2500.00, 1400.00, 11600.00, '2026-10-10', 1),
(3, '2026-09', 6500.00,  1500.00, 1000.00, 7000.00,  '2026-10-10', 1),
(4, '2026-09', 10000.00, 2000.00, 1300.00, 10700.00, NULL, 0),
(5, '2026-09', 7500.00,  1000.00, 1100.00, 7400.00,  NULL, 0);

-- 请假审批单（待审批/已通过/已驳回 各一）
INSERT INTO `leave_request` (`emp_id`, `leave_type`, `begin_date`, `end_date`, `days`, `reason`, `status`) VALUES
(1, 1, '2026-10-20', '2026-10-21', 2.0, '家中有事需要处理', 0),
(2, 2, '2026-10-14', '2026-10-15', 2.0, '感冒发烧需要休息', 1),
(3, 3, '2026-11-02', '2026-11-03', 2.0, '临时出国旅游',     2);

-- 已通过/已驳回的单据补审批信息
UPDATE `leave_request` SET `approver_id` = 1, `approve_time` = '2026-10-13 10:00:00', `approve_remark` = '注意休息，早日康复' WHERE `emp_id` = 2;
UPDATE `leave_request` SET `approver_id` = 1, `approve_time` = '2026-10-13 10:05:00', `approve_remark` = '年假额度不足，驳回' WHERE `emp_id` = 3;

-- 职位调整申请（待审批/已通过/已驳回 各一；已通过的同步演示 emp 职位变更）
INSERT INTO `job_transfer` (`emp_id`, `current_job`, `target_job`, `target_dept_id`, `reason`, `status`) VALUES
(3, '市场专员',   '市场经理',   NULL, '希望承担更多管理职责', 0),
(2, '前端工程师', '高级前端工程师', NULL, '工作三年，申请职级晋升', 1),
(5, '会计',       '研发部测试工程师', 1, '对软件测试感兴趣，自学已获证书', 2);

UPDATE `job_transfer` SET `approver_id` = 1, `approve_time` = '2026-10-12 11:00:00', `approve_remark` = '同意晋升，下月生效' WHERE `emp_id` = 2;
UPDATE `job_transfer` SET `approver_id` = 1, `approve_time` = '2026-10-12 11:05:00', `approve_remark` = '当前岗位人手不足，暂不同意' WHERE `emp_id` = 5;
