-- ----------------------------
-- 房间预约数据统计模块增量脚本
-- 适用场景：已执行空间预约基础脚本的数据库，新增统计侧边栏、按钮权限。
-- 说明：2060/2061 已用于取消审核与所有预约公开查询，统计模块使用 2070-2072。
-- ----------------------------

delete from sys_role_menu
 where menu_id in (
   select menu_id from (
     select menu_id
       from sys_menu
      where menu_id in (2070, 2071, 2072)
         or path = 'space-statistics'
         or component = 'SpaceStatistics'
         or perms in ('space:statistics:list', 'space:statistics:export')
   ) t
 );

delete from sys_menu
 where menu_id in (
   select menu_id from (
     select menu_id
       from sys_menu
      where menu_id in (2070, 2071, 2072)
         or path = 'space-statistics'
         or component = 'SpaceStatistics'
         or perms in ('space:statistics:list', 'space:statistics:export')
   ) t
 );

insert into sys_menu values(2070, '数据统计模块', '0', 7, 'space-statistics', null, '', 'SpaceStatisticsManage', 1, 0, 'M', '0', '0', '', 'chart', 'admin', sysdate(), '', null, '空间预约-数据统计模块');
insert into sys_menu values(2071, '房间预约数据统计', '2070', 1, 'index', 'space/statistics/index', '', 'SpaceStatistics', 1, 0, 'C', '0', '0', 'space:statistics:list', 'dashboard', 'admin', sysdate(), '', null, '自动统计各房间预约频次、占用率、预约成功/驳回数量，支持日期、房间筛选统计');
insert into sys_menu values(2072, '数据统计导出', '2071', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'space:statistics:export', '#', 'admin', sysdate(), '', null, '');

delete from sys_role_menu
 where menu_id in (2070, 2071, 2072)
   and role_id in (
     select role_id from sys_role where role_key in ('space_teacher', 'space_student')
   );

insert into sys_role_menu(role_id, menu_id)
select r.role_id, m.menu_id
  from sys_role r
  join sys_menu m on m.menu_id in (2070, 2071, 2072)
 where (
        r.role_key in ('admin', 'space_admin', 'space_auditor', 'space_audit', 'space_audit_admin')
        or r.role_name in ('超级管理员', '空间预约管理员', '审核员', '空间预约审核员')
        or r.role_name like '%审核员%'
        or r.role_name like '%审核管理%'
       )
   and not exists (
     select 1 from sys_role_menu srm where srm.role_id = r.role_id and srm.menu_id = m.menu_id
   );
