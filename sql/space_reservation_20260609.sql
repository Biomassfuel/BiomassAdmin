-- ----------------------------
-- 空间预约系统业务初始化脚本
-- 适用框架：RuoYi-Vue2 / Spring Boot / MySQL
-- 说明：
-- 1. 本脚本只包含空间预约业务表、字典、菜单权限和基础数据初始化。
-- 2. 不修改 RuoYi 原始表结构，不添加数据库外键，便于 RuoYi 代码生成器使用。
-- 3. 预约占用以 space_reservation_item 场次明细表为准；主表只表示一次申请。
-- 4. 待审核和已通过场次都应参与业务层冲突校验。
-- ----------------------------

-- ----------------------------
-- 1、学校/组织信息表
-- ----------------------------
drop table if exists space_org;
create table space_org (
  org_id            bigint(20)      not null auto_increment    comment '组织ID',
  org_code          varchar(30)     not null                   comment '组织编码，如 UM、MPU、IFTM',
  org_name          varchar(100)    not null                   comment '组织名称，如澳门大学、澳门理工大学、澳门旅游大学',
  org_short_name    varchar(50)     default ''                 comment '组织简称，用于列表和看板展示',
  org_type          char(1)         default '1'                comment '组织类型（0平台 1学校 2部门 3外部单位）',
  dept_id           bigint(20)      default null               comment '对应RuoYi部门ID，关联sys_dept.dept_id，用于用户归属和数据权限',
  contact_name      varchar(50)     default ''                 comment '联系人姓名',
  contact_phone     varchar(30)     default ''                 comment '联系电话',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (org_id),
  unique key uk_space_org_code (org_code)
) engine=innodb auto_increment=100 comment = '空间预约-学校/组织信息表';

-- ----------------------------
-- 2、楼栋信息表
-- ----------------------------
drop table if exists space_building;
create table space_building (
  building_id       bigint(20)      not null auto_increment    comment '楼栋ID',
  building_code     varchar(30)     not null                   comment '楼栋编码，如 BUILDING_6',
  building_name     varchar(100)    not null                   comment '楼栋名称，如6#楼',
  campus_name       varchar(100)    default ''                 comment '校区或园区名称',
  address           varchar(255)    default ''                 comment '楼栋地址',
  floor_count       int(4)          default 0                  comment '楼层数量',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (building_id),
  unique key uk_space_building_code (building_code)
) engine=innodb auto_increment=100 comment = '空间预约-楼栋信息表';

-- ----------------------------
-- 3、房间类型表
-- ----------------------------
drop table if exists space_room_type;
create table space_room_type (
  type_id           bigint(20)      not null auto_increment    comment '房间类型ID',
  type_code         varchar(30)     not null                   comment '房间类型编码，如 MULTI_HALL、LARGE_CLASSROOM',
  type_name         varchar(50)     not null                   comment '房间类型名称，如多功能厅、大教室',
  capacity_level    varchar(30)     default ''                 comment '容量等级说明，如100人级别、40人级别',
  order_num         int(4)          default 0                  comment '显示顺序',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (type_id),
  unique key uk_space_room_type_code (type_code)
) engine=innodb auto_increment=100 comment = '空间预约-房间类型表';

-- ----------------------------
-- 4、房间信息表
-- ----------------------------
drop table if exists space_room;
create table space_room (
  room_id           bigint(20)      not null auto_increment    comment '房间ID',
  room_code         varchar(30)     not null                   comment '房间编号，如101、301',
  room_name         varchar(100)    not null                   comment '房间名称，默认与房间编号一致，可后续维护为教室名称',
  building_id       bigint(20)      default null               comment '楼栋ID，关联space_building.building_id，业务层维护一致性',
  building_name     varchar(100)    default ''                 comment '楼栋名称快照，便于列表展示',
  floor_no          varchar(20)     default ''                 comment '楼层，如1F、3F',
  type_id           bigint(20)      default null               comment '房间类型ID，关联space_room_type.type_id，业务层维护一致性',
  room_type         varchar(50)     default ''                 comment '房间类型名称快照，如多功能厅、大教室',
  area              decimal(10,2)   default null               comment '面积，单位平方米',
  capacity_min      int(6)          default 0                  comment '最小容纳人数，容量为范围时保存下限',
  capacity_max      int(6)          default 0                  comment '最大容纳人数，容量为范围时保存上限',
  capacity_desc     varchar(50)     default ''                 comment '容量描述，如42-47人、93人',
  assigned_org_id   bigint(20)      default null               comment '建议分配组织ID，三校共享或弹性备用可为空',
  assigned_org_name varchar(100)    default ''                 comment '建议分配组织名称，如澳门大学、三校共享、弹性备用',
  equipment_desc    varchar(500)    default ''                 comment '设备配置描述，冗余展示字段，明细见space_room_equipment',
  location_desc     varchar(255)    default ''                 comment '位置描述，如6#楼3F',
  bookable          char(1)         default '0'                comment '是否可预约（0可预约 1不可预约）',
  status            char(1)         default '0'                comment '房间状态（0启用 1停用 2维护）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (room_id),
  unique key uk_space_room_code (room_code),
  key idx_space_room_type (type_id),
  key idx_space_room_floor (building_id, floor_no),
  key idx_space_room_status (status, bookable, del_flag)
) engine=innodb auto_increment=100 comment = '空间预约-房间信息表';

-- ----------------------------
-- 5、设备字典表
-- ----------------------------
drop table if exists space_equipment;
create table space_equipment (
  equipment_id      bigint(20)      not null auto_increment    comment '设备ID',
  equipment_code    varchar(30)     not null                   comment '设备编码，如 PROJECTOR、AUDIO、WHITEBOARD',
  equipment_name    varchar(50)     not null                   comment '设备名称，如投影、音响、白板',
  order_num         int(4)          default 0                  comment '显示顺序',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (equipment_id),
  unique key uk_space_equipment_code (equipment_code)
) engine=innodb auto_increment=100 comment = '空间预约-设备字典表';

-- ----------------------------
-- 6、房间设备关联表
-- ----------------------------
drop table if exists space_room_equipment;
create table space_room_equipment (
  room_equipment_id bigint(20)      not null auto_increment    comment '房间设备关联ID',
  room_id           bigint(20)      not null                   comment '房间ID，关联space_room.room_id，业务层维护一致性',
  equipment_id      bigint(20)      not null                   comment '设备ID，关联space_equipment.equipment_id，业务层维护一致性',
  quantity          int(6)          default 1                  comment '设备数量',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (room_equipment_id),
  unique key uk_space_room_equipment (room_id, equipment_id),
  key idx_space_room_equipment_room (room_id),
  key idx_space_room_equipment_equipment (equipment_id)
) engine=innodb auto_increment=100 comment = '空间预约-房间设备关联表';

-- ----------------------------
-- 7、标准时段表
-- ----------------------------
drop table if exists space_time_period;
create table space_time_period (
  period_id         bigint(20)      not null auto_increment    comment '标准时段ID',
  period_code       varchar(30)     not null                   comment '时段编码，如 MORNING、AFTERNOON、EVENING',
  period_name       varchar(50)     not null                   comment '时段名称，如上午、下午、晚间',
  start_time        time            not null                   comment '开始时间',
  end_time          time            not null                   comment '结束时间',
  order_num         int(4)          default 0                  comment '显示顺序',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (period_id),
  unique key uk_space_time_period_code (period_code)
) engine=innodb auto_increment=100 comment = '空间预约-标准时段表';

-- ----------------------------
-- 8、预约申请主表
-- ----------------------------
drop table if exists space_reservation;
create table space_reservation (
  reservation_id    bigint(20)      not null auto_increment    comment '预约申请ID',
  reservation_no    varchar(50)     not null                   comment '预约编号，业务唯一，如YY20260901001',
  reservation_type  char(1)         not null                   comment '预约类型（0单次预约 1长期固定预约）',
  applicant_id      bigint(20)      default null               comment '申请人用户ID，关联sys_user.user_id，业务层维护一致性',
  applicant_name    varchar(50)     default ''                 comment '申请人姓名快照，避免用户改名影响历史记录',
  applicant_role    varchar(30)     default ''                 comment '申请人角色快照，如老师、管理员',
  applicant_phone   varchar(30)     default ''                 comment '申请人联系电话快照',
  org_id            bigint(20)      default null               comment '申请人所属组织ID，关联space_org.org_id，业务层维护一致性',
  org_name          varchar(100)    default ''                 comment '申请人所属组织名称快照',
  title             varchar(200)    not null                   comment '预约主题',
  purpose           varchar(100)    default ''                 comment '预约用途，如课程教学、会议研讨、迎新活动、考试安排',
  people_count      int(6)          default 0                  comment '预约人数',
  detail_remark     varchar(1000)   default ''                 comment '预约详细备注',
  status            char(1)         default '1'                comment '预约主状态（0草稿 1待审核 2已通过 3部分通过 4已驳回 5已取消 6已结束）',
  submit_time       datetime                                   comment '提交时间',
  auditor_id        bigint(20)      default null               comment '最后审核人用户ID，关联sys_user.user_id，业务层维护一致性',
  auditor_name      varchar(50)     default ''                 comment '最后审核人姓名快照',
  audit_time        datetime                                   comment '最后审核时间',
  reject_reason     varchar(1000)   default ''                 comment '最后驳回原因',
  version           int(11)         default 0                  comment '乐观锁版本号，用于审核、取消等状态更新并发控制',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (reservation_id),
  unique key uk_space_reservation_no (reservation_no),
  key idx_space_reservation_applicant (applicant_id, submit_time),
  key idx_space_reservation_status (status, submit_time),
  key idx_space_reservation_type (reservation_type, status)
) engine=innodb auto_increment=100 comment = '空间预约-预约申请主表';

-- ----------------------------
-- 9、长期预约规则表
-- ----------------------------
drop table if exists space_reservation_rule;
create table space_reservation_rule (
  rule_id           bigint(20)      not null auto_increment    comment '预约规则ID',
  reservation_id    bigint(20)      not null                   comment '预约申请ID，关联space_reservation.reservation_id，业务层维护一致性',
  rule_type         char(1)         not null                   comment '规则类型（0每周固定 1每日固定 2自定义日期）',
  room_id           bigint(20)      default null               comment '默认房间ID，单房间长期预约时使用，多房间以明细表为准',
  room_code         varchar(30)     default ''                 comment '默认房间编号快照',
  start_date        date            not null                   comment '规则开始日期',
  end_date          date            not null                   comment '规则结束日期',
  weekdays          varchar(30)     default ''                 comment '每周重复星期，0周日 1周一 2周二 3周三 4周四 5周五 6周六，多个用英文逗号分隔',
  custom_dates_text text                                       comment '自定义日期集合，建议保存JSON字符串，如[\"2026-09-01\",\"2026-09-08\"]',
  start_time        time            not null                   comment '每日开始时间',
  end_time          time            not null                   comment '每日结束时间',
  rule_desc         varchar(500)    default ''                 comment '规则描述，用于审核页回显，如每周一/三/五 14:00-18:00',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (rule_id),
  key idx_space_reservation_rule_reservation (reservation_id),
  key idx_space_reservation_rule_date (start_date, end_date)
) engine=innodb auto_increment=100 comment = '空间预约-长期预约规则表';

-- ----------------------------
-- 10、预约场次明细表
-- ----------------------------
drop table if exists space_reservation_item;
create table space_reservation_item (
  item_id           bigint(20)      not null auto_increment    comment '预约场次ID',
  reservation_id    bigint(20)      not null                   comment '预约申请ID，关联space_reservation.reservation_id，业务层维护一致性',
  room_id           bigint(20)      not null                   comment '房间ID，关联space_room.room_id，业务层维护一致性',
  room_code         varchar(30)     not null                   comment '房间编号快照',
  room_name         varchar(100)    default ''                 comment '房间名称快照',
  booking_date      date            not null                   comment '预约日期',
  weekday           char(1)         default ''                 comment '星期（0周日 1周一 2周二 3周三 4周四 5周五 6周六）',
  start_time        time            not null                   comment '开始时间',
  end_time          time            not null                   comment '结束时间',
  item_status       char(1)         default '1'                comment '场次状态（1待审核 2已通过 3已驳回 4冲突待处理 5已取消 6已结束）',
  conflict_flag     char(1)         default '0'                comment '冲突标识（0无冲突 1有冲突）',
  conflict_reason   varchar(1000)   default ''                 comment '冲突原因，如与某预约编号某场次时间重叠',
  conflict_item_id  bigint(20)      default null               comment '冲突场次ID，关联space_reservation_item.item_id，业务层维护一致性',
  audit_time        datetime                                   comment '本场次审核时间',
  auditor_id        bigint(20)      default null               comment '本场次审核人用户ID',
  auditor_name      varchar(50)     default ''                 comment '本场次审核人姓名快照',
  reject_reason     varchar(1000)   default ''                 comment '本场次驳回原因',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (item_id),
  key idx_space_reservation_item_reservation (reservation_id),
  key idx_space_reservation_item_date (booking_date, start_time, end_time),
  key idx_space_reservation_item_conflict (room_id, booking_date, item_status, start_time, end_time),
  key idx_space_reservation_item_status (item_status, conflict_flag)
) engine=innodb auto_increment=100 comment = '空间预约-预约场次明细表';

-- ----------------------------
-- 11、预约审核日志表
-- ----------------------------
drop table if exists space_audit_log;
create table space_audit_log (
  log_id            bigint(20)      not null auto_increment    comment '审核日志ID',
  reservation_id    bigint(20)      not null                   comment '预约申请ID，关联space_reservation.reservation_id，业务层维护一致性',
  item_id           bigint(20)      default null               comment '预约场次ID，关联space_reservation_item.item_id；为空表示主申请级别审核',
  audit_action      char(1)         not null                   comment '审核动作（0提交申请 1审核通过 2审核驳回 3部分通过 4取消申请 5单场次通过 6单场次驳回）',
  before_status     char(1)         default ''                 comment '操作前状态',
  after_status      char(1)         default ''                 comment '操作后状态',
  auditor_id        bigint(20)      default null               comment '操作人用户ID',
  auditor_name      varchar(50)     default ''                 comment '操作人姓名快照',
  audit_opinion     varchar(1000)   default ''                 comment '审核意见或操作说明',
  audit_time        datetime                                   comment '审核时间',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (log_id),
  key idx_space_audit_log_reservation (reservation_id, audit_time),
  key idx_space_audit_log_item (item_id),
  key idx_space_audit_log_action (audit_action)
) engine=innodb auto_increment=100 comment = '空间预约-预约审核日志表';

-- ----------------------------
-- 12、预约消息通知表
-- ----------------------------
drop table if exists space_message;
create table space_message (
  message_id        bigint(20)      not null auto_increment    comment '消息ID',
  receiver_id       bigint(20)      not null                   comment '接收人用户ID，关联sys_user.user_id，业务层维护一致性',
  receiver_name     varchar(50)     default ''                 comment '接收人姓名快照',
  biz_type          varchar(30)     default ''                 comment '业务类型，如reservation、audit',
  biz_id            bigint(20)      default null               comment '业务主键ID，如预约申请ID',
  message_title     varchar(200)    not null                   comment '消息标题',
  message_content   varchar(1000)   default ''                 comment '消息内容',
  read_flag         char(1)         default '0'                comment '阅读状态（0未读 1已读）',
  read_time         datetime                                   comment '阅读时间',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (message_id),
  key idx_space_message_receiver (receiver_id, read_flag, create_time),
  key idx_space_message_biz (biz_type, biz_id)
) engine=innodb auto_increment=100 comment = '空间预约-预约消息通知表';

-- ----------------------------
-- 13、房间日期并发锁表
-- ----------------------------
drop table if exists space_room_day_lock;
create table space_room_day_lock (
  room_id           bigint(20)      not null                   comment '房间ID，关联space_room.room_id，业务层维护一致性',
  booking_date      date            not null                   comment '预约日期',
  lock_version      int(11)         default 0                  comment '锁版本号，用于业务层乐观锁或辅助排查',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (room_id, booking_date)
) engine=innodb comment = '空间预约-房间日期并发锁表';

-- ----------------------------
-- 14、房间维护/停用时段表
-- ----------------------------
drop table if exists space_blackout;
create table space_blackout (
  blackout_id       bigint(20)      not null auto_increment    comment '维护停用ID',
  room_id           bigint(20)      not null                   comment '房间ID，关联space_room.room_id，业务层维护一致性',
  room_code         varchar(30)     default ''                 comment '房间编号快照',
  start_time        datetime        not null                   comment '停用开始时间',
  end_time          datetime        not null                   comment '停用结束时间',
  reason            varchar(500)    default ''                 comment '停用或维护原因',
  status            char(1)         default '0'                comment '状态（0生效 1失效）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (blackout_id),
  key idx_space_blackout_room_time (room_id, start_time, end_time),
  key idx_space_blackout_status (status)
) engine=innodb auto_increment=100 comment = '空间预约-房间维护/停用时段表';

-- ----------------------------
-- 15、Excel导入批次记录表
-- ----------------------------
drop table if exists space_import_batch;
create table space_import_batch (
  batch_id          bigint(20)      not null auto_increment    comment '导入批次ID',
  import_type       varchar(30)     not null                   comment '导入类型，如room、schedule、reservation',
  file_name         varchar(255)    default ''                 comment '导入文件名称',
  file_path         varchar(500)    default ''                 comment '文件存储路径或对象存储地址',
  total_count       int(11)         default 0                  comment '总记录数',
  success_count     int(11)         default 0                  comment '成功记录数',
  fail_count        int(11)         default 0                  comment '失败记录数',
  import_status     char(1)         default '0'                comment '导入状态（0待处理 1处理中 2成功 3部分成功 4失败）',
  error_msg         text                                       comment '错误信息，保存导入失败明细或摘要',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (batch_id),
  key idx_space_import_batch_type (import_type, import_status, create_time)
) engine=innodb auto_increment=100 comment = '空间预约-Excel导入批次记录表';

-- ----------------------------
-- 初始化RuoYi部门数据
-- 说明：
-- 1. 200为澳琴国际教育大学城根部门，挂在RuoYi默认根部门BiomassAdmin(100)下。
-- 2. 201-203为三所学校部门，用户管理中老师/学生账号建议挂到对应学校部门。
-- 3. 不把楼层、房间放入sys_dept，房间资源仍由space_room维护。
-- ----------------------------
delete from sys_dept where dept_id in (200, 201, 202, 203);
insert into sys_dept values(200, 100, '0,100',     '澳琴国际教育大学城', 10, 'admin', '15888888888', 'admin@biomass.local', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(201, 200, '0,100,200', '澳门大学',           1, 'admin', '15888888888', 'admin@biomass.local', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(202, 200, '0,100,200', '澳门理工大学',       2, 'admin', '15888888888', 'admin@biomass.local', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(203, 200, '0,100,200', '澳门旅游大学',       3, 'admin', '15888888888', 'admin@biomass.local', '0', '0', 'admin', sysdate(), '', null);

-- ----------------------------
-- 初始化学校/组织数据
-- ----------------------------
insert into space_org values(1, 'UM',   '澳门大学',     '澳大',   '1', 201, '', '', '0', '0', 'admin', sysdate(), '', null, '三校排课中的澳门大学，对应RuoYi部门201');
insert into space_org values(2, 'MPU',  '澳门理工大学', '澳理大', '1', 202, '', '', '0', '0', 'admin', sysdate(), '', null, '三校排课中的澳门理工大学，对应RuoYi部门202');
insert into space_org values(3, 'IFTM', '澳门旅游大学', '澳旅大', '1', 203, '', '', '0', '0', 'admin', sysdate(), '', null, '三校排课中的澳门旅游大学，对应RuoYi部门203');

-- ----------------------------
-- 初始化楼栋数据
-- ----------------------------
insert into space_building values(1, 'BUILDING_6', '6#楼', '澳琴国际教育大学城', '德智广场教学点', 6, '0', '0', 'admin', sysdate(), '', null, '澳琴国际教育大学城三校排课使用楼栋');

-- ----------------------------
-- 初始化房间类型数据
-- ----------------------------
insert into space_room_type values(1, 'MULTI_HALL',       '多功能厅', '100人级别', 1, '0', 'admin', sysdate(), '', null, '1F-2F多功能厅，可用于大型课程');
insert into space_room_type values(2, 'LARGE_CLASSROOM',  '大教室',   '40人级别',  2, '0', 'admin', sysdate(), '', null, '3F-6F大教室，容量约42-47人');
insert into space_room_type values(3, 'MEDIUM_CLASSROOM', '中教室',   '30人级别',  3, '0', 'admin', sysdate(), '', null, '3F-6F中教室，容量约24-30人');
insert into space_room_type values(4, 'SMALL_CLASSROOM',  '小教室',   '25人级别',  4, '0', 'admin', sysdate(), '', null, '3F-6F小教室，容量约26-28人');

-- ----------------------------
-- 初始化设备数据
-- ----------------------------
insert into space_equipment values(1, 'PROJECTOR',  '投影', 1, '0', 'admin', sysdate(), '', null, '教室常用投影设备');
insert into space_equipment values(2, 'AUDIO',      '音响', 2, '0', 'admin', sysdate(), '', null, '多功能厅音响设备');
insert into space_equipment values(3, 'WHITEBOARD', '白板', 3, '0', 'admin', sysdate(), '', null, '教室白板');

-- ----------------------------
-- 初始化标准预约时段
-- ----------------------------
insert into space_time_period values(1, 'MORNING',   '上午', '08:30:00', '13:30:00', 1, '0', 'admin', sysdate(), '', null, '标准上午教学时段');
insert into space_time_period values(2, 'AFTERNOON', '下午', '14:00:00', '18:00:00', 2, '0', 'admin', sysdate(), '', null, '标准下午教学时段');
insert into space_time_period values(3, 'EVENING',   '晚间', '18:30:00', '22:30:00', 3, '0', 'admin', sysdate(), '', null, '标准晚间教学时段');

-- ----------------------------
-- 初始化44间房间数据
-- 房间容量来自《澳琴国际教育大学城三校排课（20260527）.xlsx》
-- ----------------------------
insert into space_room values(1,  '101', '101', 1, '6#楼', '1F', 1, '多功能厅', 120.00, 93,  93,  '93人',    null, '三校共享',     '投影、音响', '6#楼1F', '0', '0', '0', 'admin', sysdate(), '', null, '可用于大型课程，100人级别');
insert into space_room values(2,  '102', '102', 1, '6#楼', '1F', 1, '多功能厅', 188.00, 120, 120, '120人',   null, '三校共享',     '投影、音响', '6#楼1F', '0', '0', '0', 'admin', sysdate(), '', null, '可用于大型课程，100人级别');
insert into space_room values(3,  '201', '201', 1, '6#楼', '2F', 1, '多功能厅', 158.00, 110, 110, '110人',   null, '三校共享',     '投影、音响', '6#楼2F', '0', '0', '0', 'admin', sysdate(), '', null, '可用于大型课程，100人级别');
insert into space_room values(4,  '202', '202', 1, '6#楼', '2F', 1, '多功能厅', 188.00, 120, 120, '120人',   null, '三校共享',     '投影、音响', '6#楼2F', '0', '0', '0', 'admin', sysdate(), '', null, '可用于大型课程，100人级别');
insert into space_room values(5,  '301', '301', 1, '6#楼', '3F', 2, '大教室',    80.00, 42,  47,  '42-47人', 1,    '澳门大学',     '投影、白板', '6#楼3F', '0', '0', '0', 'admin', sysdate(), '', null, '3F大教室，集中安排澳门大学');
insert into space_room values(6,  '304', '304', 1, '6#楼', '3F', 2, '大教室',    80.00, 42,  47,  '42-47人', 1,    '澳门大学',     '投影、白板', '6#楼3F', '0', '0', '0', 'admin', sysdate(), '', null, '3F大教室，集中安排澳门大学');
insert into space_room values(7,  '307', '307', 1, '6#楼', '3F', 2, '大教室',    80.00, 42,  47,  '42-47人', 1,    '澳门大学',     '投影、白板', '6#楼3F', '0', '0', '0', 'admin', sysdate(), '', null, '3F大教室，集中安排澳门大学');
insert into space_room values(8,  '310', '310', 1, '6#楼', '3F', 2, '大教室',    80.00, 42,  47,  '42-47人', 1,    '澳门大学',     '投影、白板', '6#楼3F', '0', '0', '0', 'admin', sysdate(), '', null, '3F大教室，集中安排澳门大学');
insert into space_room values(9,  '305', '305', 1, '6#楼', '3F', 3, '中教室',    67.00, 24,  30,  '24-30人', 1,    '澳门大学',     '投影、白板', '6#楼3F', '0', '0', '0', 'admin', sysdate(), '', null, '3F中教室，集中安排澳门大学');
insert into space_room values(10, '306', '306', 1, '6#楼', '3F', 3, '中教室',    67.00, 24,  30,  '24-30人', 1,    '澳门大学',     '投影、白板', '6#楼3F', '0', '0', '0', 'admin', sysdate(), '', null, '3F中教室，集中安排澳门大学');
insert into space_room values(11, '302', '302', 1, '6#楼', '3F', 4, '小教室',    55.00, 26,  28,  '26-28人', 1,    '澳门大学',     '投影',      '6#楼3F', '0', '0', '0', 'admin', sysdate(), '', null, '3F小教室，集中安排澳门大学');
insert into space_room values(12, '303', '303', 1, '6#楼', '3F', 4, '小教室',    55.00, 26,  28,  '26-28人', 1,    '澳门大学',     '投影',      '6#楼3F', '0', '0', '0', 'admin', sysdate(), '', null, '3F小教室，集中安排澳门大学');
insert into space_room values(13, '308', '308', 1, '6#楼', '3F', 4, '小教室',    55.00, 26,  28,  '26-28人', 1,    '澳门大学',     '投影',      '6#楼3F', '0', '0', '0', 'admin', sysdate(), '', null, '3F小教室，集中安排澳门大学');
insert into space_room values(14, '309', '309', 1, '6#楼', '3F', 4, '小教室',    55.00, 26,  28,  '26-28人', 1,    '澳门大学',     '投影',      '6#楼3F', '0', '0', '0', 'admin', sysdate(), '', null, '3F小教室，集中安排澳门大学');
insert into space_room values(15, '401', '401', 1, '6#楼', '4F', 2, '大教室',    80.00, 42,  47,  '42-47人', 2,    '澳门理工大学', '投影、白板', '6#楼4F', '0', '0', '0', 'admin', sysdate(), '', null, '4F大教室，集中安排澳门理工大学');
insert into space_room values(16, '404', '404', 1, '6#楼', '4F', 2, '大教室',    80.00, 42,  47,  '42-47人', 2,    '澳门理工大学', '投影、白板', '6#楼4F', '0', '0', '0', 'admin', sysdate(), '', null, '4F大教室，集中安排澳门理工大学');
insert into space_room values(17, '407', '407', 1, '6#楼', '4F', 2, '大教室',    80.00, 42,  47,  '42-47人', 2,    '澳门理工大学', '投影、白板', '6#楼4F', '0', '0', '0', 'admin', sysdate(), '', null, '4F大教室，集中安排澳门理工大学');
insert into space_room values(18, '410', '410', 1, '6#楼', '4F', 2, '大教室',    80.00, 42,  47,  '42-47人', 2,    '澳门理工大学', '投影、白板', '6#楼4F', '0', '0', '0', 'admin', sysdate(), '', null, '4F大教室，集中安排澳门理工大学');
insert into space_room values(19, '405', '405', 1, '6#楼', '4F', 3, '中教室',    67.00, 24,  30,  '24-30人', 2,    '澳门理工大学', '投影、白板', '6#楼4F', '0', '0', '0', 'admin', sysdate(), '', null, '4F中教室，集中安排澳门理工大学');
insert into space_room values(20, '406', '406', 1, '6#楼', '4F', 3, '中教室',    67.00, 24,  30,  '24-30人', 2,    '澳门理工大学', '投影、白板', '6#楼4F', '0', '0', '0', 'admin', sysdate(), '', null, '4F中教室，集中安排澳门理工大学');
insert into space_room values(21, '402', '402', 1, '6#楼', '4F', 4, '小教室',    55.00, 26,  28,  '26-28人', 2,    '澳门理工大学', '投影',      '6#楼4F', '0', '0', '0', 'admin', sysdate(), '', null, '4F小教室，集中安排澳门理工大学');
insert into space_room values(22, '403', '403', 1, '6#楼', '4F', 4, '小教室',    55.00, 26,  28,  '26-28人', 2,    '澳门理工大学', '投影',      '6#楼4F', '0', '0', '0', 'admin', sysdate(), '', null, '4F小教室，集中安排澳门理工大学');
insert into space_room values(23, '408', '408', 1, '6#楼', '4F', 4, '小教室',    55.00, 26,  28,  '26-28人', 2,    '澳门理工大学', '投影',      '6#楼4F', '0', '0', '0', 'admin', sysdate(), '', null, '4F小教室，集中安排澳门理工大学');
insert into space_room values(24, '409', '409', 1, '6#楼', '4F', 4, '小教室',    55.00, 26,  28,  '26-28人', 2,    '澳门理工大学', '投影',      '6#楼4F', '0', '0', '0', 'admin', sysdate(), '', null, '4F小教室，集中安排澳门理工大学');
insert into space_room values(25, '501', '501', 1, '6#楼', '5F', 2, '大教室',    80.00, 42,  47,  '42-47人', 3,    '澳门旅游大学', '投影、白板', '6#楼5F', '0', '0', '0', 'admin', sysdate(), '', null, '5F大教室，集中安排澳门旅游大学');
insert into space_room values(26, '504', '504', 1, '6#楼', '5F', 2, '大教室',    80.00, 42,  47,  '42-47人', 3,    '澳门旅游大学', '投影、白板', '6#楼5F', '0', '0', '0', 'admin', sysdate(), '', null, '5F大教室，集中安排澳门旅游大学');
insert into space_room values(27, '507', '507', 1, '6#楼', '5F', 2, '大教室',    80.00, 42,  47,  '42-47人', 3,    '澳门旅游大学', '投影、白板', '6#楼5F', '0', '0', '0', 'admin', sysdate(), '', null, '5F大教室，集中安排澳门旅游大学');
insert into space_room values(28, '510', '510', 1, '6#楼', '5F', 2, '大教室',    80.00, 42,  47,  '42-47人', 3,    '澳门旅游大学', '投影、白板', '6#楼5F', '0', '0', '0', 'admin', sysdate(), '', null, '5F大教室，集中安排澳门旅游大学');
insert into space_room values(29, '505', '505', 1, '6#楼', '5F', 3, '中教室',    67.00, 24,  30,  '24-30人', 3,    '澳门旅游大学', '投影、白板', '6#楼5F', '0', '0', '0', 'admin', sysdate(), '', null, '5F中教室，集中安排澳门旅游大学');
insert into space_room values(30, '506', '506', 1, '6#楼', '5F', 3, '中教室',    67.00, 24,  30,  '24-30人', 3,    '澳门旅游大学', '投影、白板', '6#楼5F', '0', '0', '0', 'admin', sysdate(), '', null, '5F中教室，集中安排澳门旅游大学');
insert into space_room values(31, '502', '502', 1, '6#楼', '5F', 4, '小教室',    55.00, 26,  28,  '26-28人', 3,    '澳门旅游大学', '投影',      '6#楼5F', '0', '0', '0', 'admin', sysdate(), '', null, '5F小教室，集中安排澳门旅游大学');
insert into space_room values(32, '503', '503', 1, '6#楼', '5F', 4, '小教室',    55.00, 26,  28,  '26-28人', 3,    '澳门旅游大学', '投影',      '6#楼5F', '0', '0', '0', 'admin', sysdate(), '', null, '5F小教室，集中安排澳门旅游大学');
insert into space_room values(33, '508', '508', 1, '6#楼', '5F', 4, '小教室',    55.00, 26,  28,  '26-28人', 3,    '澳门旅游大学', '投影',      '6#楼5F', '0', '0', '0', 'admin', sysdate(), '', null, '5F小教室，集中安排澳门旅游大学');
insert into space_room values(34, '509', '509', 1, '6#楼', '5F', 4, '小教室',    55.00, 26,  28,  '26-28人', 3,    '澳门旅游大学', '投影',      '6#楼5F', '0', '0', '0', 'admin', sysdate(), '', null, '5F小教室，集中安排澳门旅游大学');
insert into space_room values(35, '601', '601', 1, '6#楼', '6F', 2, '大教室',    80.00, 42,  47,  '42-47人', null, '弹性备用',     '投影、白板', '6#楼6F', '0', '0', '0', 'admin', sysdate(), '', null, '6F大教室，弹性备用');
insert into space_room values(36, '604', '604', 1, '6#楼', '6F', 2, '大教室',    80.00, 42,  47,  '42-47人', null, '弹性备用',     '投影、白板', '6#楼6F', '0', '0', '0', 'admin', sysdate(), '', null, '6F大教室，弹性备用');
insert into space_room values(37, '607', '607', 1, '6#楼', '6F', 2, '大教室',    80.00, 42,  47,  '42-47人', null, '弹性备用',     '投影、白板', '6#楼6F', '0', '0', '0', 'admin', sysdate(), '', null, '6F大教室，弹性备用');
insert into space_room values(38, '610', '610', 1, '6#楼', '6F', 2, '大教室',    80.00, 42,  47,  '42-47人', null, '弹性备用',     '投影、白板', '6#楼6F', '0', '0', '0', 'admin', sysdate(), '', null, '6F大教室，弹性备用');
insert into space_room values(39, '605', '605', 1, '6#楼', '6F', 3, '中教室',    67.00, 24,  30,  '24-30人', null, '弹性备用',     '投影、白板', '6#楼6F', '0', '0', '0', 'admin', sysdate(), '', null, '6F中教室，弹性备用');
insert into space_room values(40, '606', '606', 1, '6#楼', '6F', 3, '中教室',    67.00, 24,  30,  '24-30人', null, '弹性备用',     '投影、白板', '6#楼6F', '0', '0', '0', 'admin', sysdate(), '', null, '6F中教室，弹性备用');
insert into space_room values(41, '602', '602', 1, '6#楼', '6F', 4, '小教室',    55.00, 26,  28,  '26-28人', null, '弹性备用',     '投影',      '6#楼6F', '0', '0', '0', 'admin', sysdate(), '', null, '6F小教室，弹性备用');
insert into space_room values(42, '603', '603', 1, '6#楼', '6F', 4, '小教室',    55.00, 26,  28,  '26-28人', null, '弹性备用',     '投影',      '6#楼6F', '0', '0', '0', 'admin', sysdate(), '', null, '6F小教室，弹性备用');
insert into space_room values(43, '608', '608', 1, '6#楼', '6F', 4, '小教室',    55.00, 26,  28,  '26-28人', null, '弹性备用',     '投影',      '6#楼6F', '0', '0', '0', 'admin', sysdate(), '', null, '6F小教室，弹性备用');
insert into space_room values(44, '609', '609', 1, '6#楼', '6F', 4, '小教室',    55.00, 26,  28,  '26-28人', null, '弹性备用',     '投影',      '6#楼6F', '0', '0', '0', 'admin', sysdate(), '', null, '6F小教室，弹性备用');

-- ----------------------------
-- 初始化房间设备关联数据
-- 约定：所有房间默认有投影；多功能厅额外有音响；大/中教室额外有白板。
-- ----------------------------
insert into space_room_equipment(room_id, equipment_id, quantity, status, create_by, create_time, remark)
select room_id, 1, 1, '0', 'admin', sysdate(), '初始化：所有教学空间默认配置投影' from space_room;

insert into space_room_equipment(room_id, equipment_id, quantity, status, create_by, create_time, remark)
select room_id, 2, 1, '0', 'admin', sysdate(), '初始化：多功能厅配置音响' from space_room where room_type = '多功能厅';

insert into space_room_equipment(room_id, equipment_id, quantity, status, create_by, create_time, remark)
select room_id, 3, 1, '0', 'admin', sysdate(), '初始化：大教室和中教室配置白板' from space_room where room_type in ('大教室', '中教室');

-- ----------------------------
-- 初始化空间预约业务字典
-- 为避免与RuoYi原始字典冲突，字典类型ID从100开始，字典数据ID从100开始。
-- ----------------------------
delete from sys_dict_data where dict_type in (
  'space_room_type',
  'space_reservation_type',
  'space_reservation_status',
  'space_item_status',
  'space_audit_action',
  'space_rule_type',
  'space_conflict_flag',
  'space_message_read_flag'
);
delete from sys_dict_type where dict_type in (
  'space_room_type',
  'space_reservation_type',
  'space_reservation_status',
  'space_item_status',
  'space_audit_action',
  'space_rule_type',
  'space_conflict_flag',
  'space_message_read_flag'
);

insert into sys_dict_type values(100, '房间类型',     'space_room_type',           '0', 'admin', sysdate(), '', null, '空间预约房间类型字典');
insert into sys_dict_type values(101, '预约类型',     'space_reservation_type',    '0', 'admin', sysdate(), '', null, '空间预约类型字典');
insert into sys_dict_type values(102, '预约状态',     'space_reservation_status',  '0', 'admin', sysdate(), '', null, '空间预约主状态字典');
insert into sys_dict_type values(103, '场次状态',     'space_item_status',         '0', 'admin', sysdate(), '', null, '空间预约场次状态字典');
insert into sys_dict_type values(104, '审核动作',     'space_audit_action',        '0', 'admin', sysdate(), '', null, '空间预约审核动作字典');
insert into sys_dict_type values(105, '长期规则类型', 'space_rule_type',           '0', 'admin', sysdate(), '', null, '空间预约长期规则类型字典');
insert into sys_dict_type values(106, '冲突标识',     'space_conflict_flag',       '0', 'admin', sysdate(), '', null, '空间预约冲突标识字典');
insert into sys_dict_type values(107, '消息阅读状态', 'space_message_read_flag',   '0', 'admin', sysdate(), '', null, '空间预约消息阅读状态字典');

insert into sys_dict_data values(100, 1, '多功能厅',   'MULTI_HALL',       'space_room_type',          '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '多功能厅');
insert into sys_dict_data values(101, 2, '大教室',     'LARGE_CLASSROOM',  'space_room_type',          '', 'success', 'N', '0', 'admin', sysdate(), '', null, '大教室');
insert into sys_dict_data values(102, 3, '中教室',     'MEDIUM_CLASSROOM', 'space_room_type',          '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '中教室');
insert into sys_dict_data values(103, 4, '小教室',     'SMALL_CLASSROOM',  'space_room_type',          '', 'info',    'N', '0', 'admin', sysdate(), '', null, '小教室');
insert into sys_dict_data values(104, 1, '单次预约',   '0',                'space_reservation_type',   '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '单次预约');
insert into sys_dict_data values(105, 2, '长期预约',   '1',                'space_reservation_type',   '', 'success', 'N', '0', 'admin', sysdate(), '', null, '长期固定预约');
insert into sys_dict_data values(106, 0, '草稿',       '0',                'space_reservation_status', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '草稿');
insert into sys_dict_data values(107, 1, '待审核',     '1',                'space_reservation_status', '', 'warning', 'Y', '0', 'admin', sysdate(), '', null, '待审核');
insert into sys_dict_data values(108, 2, '已通过',     '2',                'space_reservation_status', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '已通过');
insert into sys_dict_data values(109, 3, '部分通过',   '3',                'space_reservation_status', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '部分通过');
insert into sys_dict_data values(110, 4, '已驳回',     '4',                'space_reservation_status', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '已驳回');
insert into sys_dict_data values(111, 5, '已取消',     '5',                'space_reservation_status', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '已取消');
insert into sys_dict_data values(112, 6, '已结束',     '6',                'space_reservation_status', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '已结束');
insert into sys_dict_data values(113, 1, '待审核',     '1',                'space_item_status',        '', 'warning', 'Y', '0', 'admin', sysdate(), '', null, '场次待审核');
insert into sys_dict_data values(114, 2, '已通过',     '2',                'space_item_status',        '', 'success', 'N', '0', 'admin', sysdate(), '', null, '场次已通过');
insert into sys_dict_data values(115, 3, '已驳回',     '3',                'space_item_status',        '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '场次已驳回');
insert into sys_dict_data values(116, 4, '冲突待处理', '4',                'space_item_status',        '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '场次冲突待处理');
insert into sys_dict_data values(117, 5, '已取消',     '5',                'space_item_status',        '', 'info',    'N', '0', 'admin', sysdate(), '', null, '场次已取消');
insert into sys_dict_data values(118, 6, '已结束',     '6',                'space_item_status',        '', 'info',    'N', '0', 'admin', sysdate(), '', null, '场次已结束');
insert into sys_dict_data values(119, 0, '提交申请',   '0',                'space_audit_action',       '', 'info',    'N', '0', 'admin', sysdate(), '', null, '提交预约申请');
insert into sys_dict_data values(120, 1, '审核通过',   '1',                'space_audit_action',       '', 'success', 'N', '0', 'admin', sysdate(), '', null, '审核通过');
insert into sys_dict_data values(121, 2, '审核驳回',   '2',                'space_audit_action',       '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '审核驳回');
insert into sys_dict_data values(122, 3, '部分通过',   '3',                'space_audit_action',       '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '长期预约部分通过');
insert into sys_dict_data values(123, 4, '取消申请',   '4',                'space_audit_action',       '', 'info',    'N', '0', 'admin', sysdate(), '', null, '取消申请');
insert into sys_dict_data values(124, 5, '单场次通过', '5',                'space_audit_action',       '', 'success', 'N', '0', 'admin', sysdate(), '', null, '单场次通过');
insert into sys_dict_data values(125, 6, '单场次驳回', '6',                'space_audit_action',       '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '单场次驳回');
insert into sys_dict_data values(126, 1, '每周固定',   '0',                'space_rule_type',          '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '每周固定预约');
insert into sys_dict_data values(127, 2, '每日固定',   '1',                'space_rule_type',          '', 'success', 'N', '0', 'admin', sysdate(), '', null, '每日固定预约');
insert into sys_dict_data values(128, 3, '自定义日期', '2',                'space_rule_type',          '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '自定义多日期预约');
insert into sys_dict_data values(129, 1, '无冲突',     '0',                'space_conflict_flag',      '', 'success', 'Y', '0', 'admin', sysdate(), '', null, '无冲突');
insert into sys_dict_data values(130, 2, '有冲突',     '1',                'space_conflict_flag',      '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '有冲突');
insert into sys_dict_data values(131, 1, '未读',       '0',                'space_message_read_flag',  '', 'warning', 'Y', '0', 'admin', sysdate(), '', null, '消息未读');
insert into sys_dict_data values(132, 2, '已读',       '1',                'space_message_read_flag',  '', 'success', 'N', '0', 'admin', sysdate(), '', null, '消息已读');

-- ----------------------------
-- 初始化空间预约基础角色，暂不初始化菜单树
-- 说明：
-- 1. 当前阶段先不初始化“空间预约管理”总菜单，避免把房间管理、我的预约、审核、统计等不同角色入口混在一起。
-- 2. 这里只保留空间预约管理员、老师、学生三个业务角色基础定义，暂不绑定sys_role_menu。
-- 3. 学生不可提交预约；老师后续应单独配置“我的预约”等入口；管理员后续按房间、审核、统计等模块分别配置。
-- 4. admin超级管理员仍保留RuoYi原有全权限；这里新增的是空间预约业务角色。
-- 5. 不初始化sys_user_role，避免脚本误给现有用户绑定角色；用户和角色关系请在RuoYi角色管理/用户管理中手动分配。
-- 6. data_scope沿用RuoYi约定：1全部数据权限，2自定数据权限，3本部门数据权限，4本部门及以下数据权限，5仅本人数据权限。
-- ----------------------------
delete from sys_role_menu where role_id in (100, 101, 102);
delete from sys_role_menu where menu_id between 2000 and 2099;
delete from sys_menu where menu_id between 2000 and 2099;
delete from sys_role where role_id in (100, 101, 102) or role_key in ('space_admin', 'space_teacher', 'space_student');

insert into sys_role values(100, '空间预约管理员', 'space_admin',   3, '1', 1, 1, '0', '0', 'admin', sysdate(), '', null, '空间预约业务管理员：基础角色保留，菜单权限后续按模块逐步配置');
insert into sys_role values(101, '老师',           'space_teacher', 4, '5', 1, 1, '0', '0', 'admin', sysdate(), '', null, '老师：可提交预约并查看我的预约，菜单权限后续按模块逐步配置');
insert into sys_role values(102, '学生',           'space_student', 5, '5', 1, 1, '0', '0', 'admin', sysdate(), '', null, '学生：不可提交预约，仅保留查看开放占用信息的角色基础定义');

-- ----------------------------
-- 初始化空间预约侧边栏菜单与按钮权限
-- ----------------------------
insert into sys_menu values(2000, '房间管理模块', '0', 4, 'space-room', null, '', 'SpaceRoomManage', 1, 0, 'M', '0', '0', '', 'build', 'admin', sysdate(), '', null, '空间预约-房间管理模块');
insert into sys_menu values(2001, '房间列表', '2000', 1, 'list', 'space/room/index', '', 'SpaceRoom', 1, 0, 'C', '0', '0', 'space:room:list', 'list', 'admin', sysdate(), '', null, '房间列表；新增、导入、启停在页面内完成');
insert into sys_menu values(2004, '房间类型管理', '2000', 2, 'type', 'space/room/type', '', 'SpaceRoomType', 1, 0, 'C', '0', '0', 'space:roomType:list', 'dict', 'admin', sysdate(), '', null, '房间类型管理');
insert into sys_menu values(2005, '设备配置管理', '2000', 3, 'equipment', 'space/room/equipment', '', 'SpaceEquipment', 1, 0, 'C', '0', '0', 'space:equipment:list', 'example', 'admin', sysdate(), '', null, '设备配置管理');

insert into sys_menu values(2010, '房间预约模块', '0', 5, 'space-reservation', null, '', 'SpaceReservationManage', 1, 0, 'M', '0', '0', '', 'date', 'admin', sysdate(), '', null, '空间预约-房间预约模块');
insert into sys_menu values(2011, '房间占用查看', '2010', 1, 'occupancy', 'space/reservation/occupancy', '', 'SpaceOccupancy', 1, 0, 'C', '0', '0', 'space:room:list', 'time', 'admin', sysdate(), '', null, '房间占用查看');
insert into sys_menu values(2012, '我要预约', '2010', 2, 'apply', 'space/reservation/apply', '', 'SpaceReservationApply', 1, 0, 'C', '0', '0', 'space:reservation:add', 'form', 'admin', sysdate(), '', null, '我要预约');
insert into sys_menu values(2013, '长期固定预约', '2010', 3, 'long', 'space/reservation/long', '', 'SpaceLongReservation', 1, 0, 'C', '0', '0', 'space:reservation:add', 'date-range', 'admin', sysdate(), '', null, '长期固定预约');
insert into sys_menu values(2014, '我的预约', '2010', 4, 'my', 'space/reservation/my', '', 'SpaceMyReservation', 1, 0, 'C', '0', '0', 'space:reservation:mine', 'user', 'admin', sysdate(), '', null, '我的预约');
insert into sys_menu values(2015, '预约详情', '2010', 5, 'detail', 'space/reservation/detail', '', 'SpaceReservationDetail', 1, 0, 'C', '1', '0', 'space:reservation:query', 'eye-open', 'admin', sysdate(), '', null, '预约详情');
insert into sys_menu values(2016, '房间占用详情', '2010', 6, 'occupancy-detail/index/:roomId', 'space/reservation/occupancyDetail', '', 'SpaceOccupancyDetail', 1, 0, 'C', '1', '0', 'space:room:query', 'eye-open', 'admin', sysdate(), '', null, '房间占用详情');

insert into sys_menu values(2020, '审核管理模块', '0', 6, 'space-audit', null, '', 'SpaceAuditManage', 1, 0, 'M', '0', '0', '', 'audit', 'admin', sysdate(), '', null, '空间预约-审核管理模块');
insert into sys_menu values(2021, '待审核预约', '2020', 1, 'pending', 'space/audit/pending', '', 'SpaceAuditPending', 1, 0, 'C', '0', '0', 'space:audit:list', 'validCode', 'admin', sysdate(), '', null, '待审核预约');
insert into sys_menu values(2023, '预约记录', '2020', 2, 'record', 'space/audit/record', '', 'SpaceReservationRecord', 1, 0, 'C', '0', '0', 'space:reservation:list', 'documentation', 'admin', sysdate(), '', null, '预约记录');
insert into sys_menu values(2025, '审核日志', '2020', 3, 'log', 'space/audit/log', '', 'SpaceAuditLog', 1, 0, 'C', '0', '0', 'space:auditLog:list', 'log', 'admin', sysdate(), '', null, '审核日志');

insert into sys_menu values(2060, '数据统计模块', '0', 7, 'space-statistics', null, '', 'SpaceStatisticsManage', 1, 0, 'M', '0', '0', '', 'chart', 'admin', sysdate(), '', null, '空间预约-数据统计模块');
insert into sys_menu values(2061, '房间预约数据统计', '2060', 1, 'index', 'space/statistics/index', '', 'SpaceStatistics', 1, 0, 'C', '0', '0', 'space:statistics:list', 'dashboard', 'admin', sysdate(), '', null, '自动统计各房间预约频次、占用率、预约成功/驳回数量，支持日期、房间筛选统计');

insert into sys_menu values(2030, '房间查询', '2001', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'space:room:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2031, '房间新增', '2001', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'space:room:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2032, '房间修改', '2001', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'space:room:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2033, '房间删除', '2001', 4, '', '', '', '', 1, 0, 'F', '0', '0', 'space:room:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2034, '房间导入', '2001', 5, '', '', '', '', 1, 0, 'F', '0', '0', 'space:room:import', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2035, '房间导出', '2001', 6, '', '', '', '', 1, 0, 'F', '0', '0', 'space:room:export', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2036, '类型查询', '2004', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'space:roomType:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2037, '类型新增', '2004', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'space:roomType:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2038, '类型修改', '2004', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'space:roomType:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2039, '类型删除', '2004', 4, '', '', '', '', 1, 0, 'F', '0', '0', 'space:roomType:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2040, '类型导出', '2004', 5, '', '', '', '', 1, 0, 'F', '0', '0', 'space:roomType:export', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2041, '设备查询', '2005', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'space:equipment:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2042, '设备新增', '2005', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'space:equipment:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2043, '设备修改', '2005', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'space:equipment:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2044, '设备删除', '2005', 4, '', '', '', '', 1, 0, 'F', '0', '0', 'space:equipment:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2045, '设备导出', '2005', 5, '', '', '', '', 1, 0, 'F', '0', '0', 'space:equipment:export', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2046, '预约查询', '2015', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'space:reservation:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2047, '场次导出', '2011', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'space:reservationItem:export', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2056, '占用房间查询', '2011', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'space:room:query', '#', 'admin', sysdate(), '', null, '房间占用详情页加载房间详情权限');
insert into sys_menu values(2057, '占用场次查询', '2011', 4, '', '', '', '', 1, 0, 'F', '0', '0', 'space:reservationItem:list', '#', 'admin', sysdate(), '', null, '房间占用详情页加载预约场次权限');
insert into sys_menu values(2048, '取消预约', '2014', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'space:reservation:cancel', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2049, '预约导出', '2023', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'space:reservation:export', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2050, '审核通过', '2021', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'space:audit:approve', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2051, '审核驳回', '2021', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'space:audit:reject', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2052, '审核日志查询', '2025', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'space:auditLog:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2053, '审核日志导出', '2025', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'space:auditLog:export', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2054, '预约房间下拉查询', '2012', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'space:room:list', '#', 'admin', sysdate(), '', null, '我要预约/长期预约页面加载房间下拉权限');
insert into sys_menu values(2055, '预约时段下拉查询', '2012', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'space:timePeriod:list', '#', 'admin', sysdate(), '', null, '我要预约/长期预约页面加载标准时段下拉权限');
insert into sys_menu values(2062, '数据统计导出', '2061', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'space:statistics:export', '#', 'admin', sysdate(), '', null, '');

insert into sys_role_menu(role_id, menu_id)
select 100, menu_id from sys_menu where menu_id between 2000 and 2062;

insert into sys_role_menu(role_id, menu_id) values
(101, 2010), (101, 2011), (101, 2012), (101, 2013), (101, 2014), (101, 2015), (101, 2016),
(101, 2046), (101, 2048), (101, 2054), (101, 2055), (101, 2056), (101, 2057);

insert into sys_role_menu(role_id, menu_id) values
(102, 2010), (102, 2011), (102, 2016), (102, 2056), (102, 2057);

-- ----------------------------
-- 精简RuoYi默认部门和岗位数据
-- 说明：
-- 1. 不删除sys_dept、sys_post、sys_user_post表结构，避免影响RuoYi用户管理、数据权限和代码生成器。
-- 2. 将RuoYi默认admin/demo账号迁移到业务顶级部门“澳琴国际教育大学城”，避免继续挂在“研发部门”等示例部门下。
-- 3. 删除RuoYi默认示例部门101-109，只保留原始顶级部门100和业务部门200-203。
-- 4. 清空默认账号的示例岗位关系，保留一个“默认岗位”用于兼容RuoYi用户管理页面。
-- 5. 空间预约系统当前不使用岗位维度做权限控制，因此隐藏“岗位管理”菜单及其按钮权限，不物理删除菜单。
-- ----------------------------
update sys_user
   set dept_id = 200,
       update_by = 'admin',
       update_time = sysdate(),
       remark = case
                  when user_name = 'admin' then '超级管理员账号已迁移到澳琴国际教育大学城部门'
                  when user_name = 'demo' then '演示账号已迁移到澳琴国际教育大学城部门'
                  else remark
                end
 where user_name in ('admin', 'demo');

delete from sys_role_dept where dept_id in (101, 102, 103, 104, 105, 106, 107, 108, 109);
delete from sys_dept where dept_id in (101, 102, 103, 104, 105, 106, 107, 108, 109);

delete from sys_user_post where user_id in (1, 2);
delete from sys_post where post_id in (1, 2, 3, 4, 100);
insert into sys_post values(100, 'default', '默认岗位', 1, '0', 'admin', sysdate(), '', null, '空间预约系统默认岗位，保留用于兼容RuoYi用户管理');

update sys_menu
   set visible = '1',
       update_by = 'admin',
       update_time = sysdate(),
       remark = '空间预约系统不使用岗位管理，菜单默认隐藏'
 where menu_id = 104
    or parent_id = 104
    or path = 'post'
    or perms like 'system:post:%';
