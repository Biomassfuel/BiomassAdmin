-- 主子表示例：采购单 / 采购明细
-- 用途：测试代码生成模板“主子表（详情页）”
-- 主表：demo_purchase_order
-- 子表：demo_purchase_item
-- 子表外键：purchase_id

drop table if exists demo_purchase_item;
drop table if exists demo_purchase_order;

create table demo_purchase_order (
  purchase_id     bigint(20)    not null auto_increment comment '采购单ID',
  purchase_no     varchar(64)   not null comment '采购单号',
  supplier_name   varchar(100)  not null comment '供应商名称',
  contact_person  varchar(64)   default null comment '联系人',
  contact_phone   varchar(32)   default null comment '联系电话',
  purchase_status char(1)       default '0' comment '采购状态（0草稿 1已提交 2已入库）',
  total_amount    decimal(12,2) default 0.00 comment '采购总额',
  purchase_date   datetime      default null comment '采购日期',
  remark          varchar(500)  default null comment '备注',
  create_by       varchar(64)   default '' comment '创建者',
  create_time     datetime      default null comment '创建时间',
  update_by       varchar(64)   default '' comment '更新者',
  update_time     datetime      default null comment '更新时间',
  primary key (purchase_id),
  unique key uk_demo_purchase_no (purchase_no)
) engine=innodb auto_increment=100 comment='示例采购单';

create table demo_purchase_item (
  item_id       bigint(20)    not null auto_increment comment '明细ID',
  purchase_id   bigint(20)    not null comment '采购单ID',
  material_name varchar(100)  not null comment '物料名称',
  material_code varchar(64)   default null comment '物料编码',
  unit_name     varchar(20)   default null comment '单位',
  quantity      decimal(10,2) default 1.00 comment '采购数量',
  unit_price    decimal(10,2) default 0.00 comment '采购单价',
  line_amount   decimal(12,2) default 0.00 comment '明细金额',
  need_date     datetime      default null comment '需求日期',
  remark        varchar(500)  default null comment '备注',
  create_by     varchar(64)   default '' comment '创建者',
  create_time   datetime      default null comment '创建时间',
  update_by     varchar(64)   default '' comment '更新者',
  update_time   datetime      default null comment '更新时间',
  primary key (item_id),
  key idx_demo_purchase_item_purchase_id (purchase_id),
  constraint fk_demo_purchase_item_order
    foreign key (purchase_id) references demo_purchase_order (purchase_id)
    on delete cascade
) engine=innodb auto_increment=1000 comment='示例采购明细';

insert into demo_purchase_order
  (purchase_id, purchase_no, supplier_name, contact_person, contact_phone, purchase_status, total_amount, purchase_date, remark, create_by, create_time)
values
  (100, 'PO20260608001', '测试供应商A', '张三', '13800000001', '0', 1560.00, '2026-06-08 09:00:00', '草稿采购单', 'admin', sysdate()),
  (101, 'PO20260608002', '测试供应商B', '李四', '13800000002', '1', 3288.50, '2026-06-08 10:30:00', '已提交采购单', 'admin', sysdate());

insert into demo_purchase_item
  (item_id, purchase_id, material_name, material_code, unit_name, quantity, unit_price, line_amount, need_date, remark, create_by, create_time)
values
  (1000, 100, '测试物料A', 'MAT-A001', '吨', 2.00, 380.00, 760.00, '2026-06-12 00:00:00', '第一条采购明细', 'admin', sysdate()),
  (1001, 100, '测试物料B', 'MAT-B001', '件', 8.00, 100.00, 800.00, '2026-06-15 00:00:00', '第二条采购明细', 'admin', sysdate()),
  (1002, 101, '测试物料C', 'MAT-C001', '箱', 5.00, 657.70, 3288.50, '2026-06-18 00:00:00', '已提交采购明细', 'admin', sysdate());
