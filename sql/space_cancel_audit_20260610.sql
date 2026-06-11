-- 待取消审核模块增量脚本
-- 适用于已经执行过 space_reservation_20260609.sql 的数据库。

alter table space_reservation
  add column audit_type char(1) default '0' comment '当前审核类型（0普通预约审核 1取消审核）' after status;

alter table space_reservation
  drop index idx_space_reservation_status,
  add index idx_space_reservation_status (status, audit_type, submit_time);

insert into sys_dict_data values(133, 7, '发起取消审核', '7', 'space_audit_action', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '已通过预约发起取消审核');
insert into sys_dict_data values(134, 8, '取消审核通过', '8', 'space_audit_action', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '取消审核通过');
insert into sys_dict_data values(135, 9, '取消审核驳回', '9', 'space_audit_action', '', 'danger', 'N', '0', 'admin', sysdate(), '', null, '取消审核驳回');
insert into sys_dict_data values(136, 10, '单场次取消通过', 'A', 'space_audit_action', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '单场次取消审核通过');
insert into sys_dict_data values(137, 11, '单场次取消驳回', 'B', 'space_audit_action', '', 'danger', 'N', '0', 'admin', sysdate(), '', null, '单场次取消审核驳回');

update sys_menu set order_num = 3 where menu_id = 2023;
update sys_menu set order_num = 4 where menu_id = 2025;

insert into sys_menu values(2022, '待取消审核', '2020', 2, 'cancel-pending', 'space/audit/cancelPending', '', 'SpaceCancelAuditPending', 1, 0, 'C', '0', '0', 'space:cancelAudit:list', 'validCode', 'admin', sysdate(), '', null, '待取消审核');
insert into sys_menu values(2058, '待取消审核查询', '2022', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'space:cancelAudit:list', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2059, '同意取消', '2022', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'space:cancelAudit:approve', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2060, '驳回取消', '2022', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'space:cancelAudit:reject', '#', 'admin', sysdate(), '', null, '');

insert into sys_role_menu(role_id, menu_id)
select 100, menu_id
from sys_menu
where menu_id in (2022, 2058, 2059, 2060);
