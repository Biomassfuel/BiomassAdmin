-- ----------------------------
-- 房间预约模块新增“所有预约”菜单与公开预约查询权限
-- 适用场景：已执行旧版 space_reservation_20260609.sql 的数据库增量升级
-- ----------------------------

delete from sys_role_menu where menu_id in (2017, 2061);
delete from sys_menu where menu_id in (2017, 2061);

insert into sys_menu values(2017, '所有预约', '2010', 2, 'all', 'space/reservation/all', '', 'SpaceAllReservation', 1, 0, 'C', '0', '0', 'space:reservationItem:publicList', 'list', 'admin', sysdate(), '', null, '已通过预约房间列表');
insert into sys_menu values(2061, '所有预约公开查询', '2017', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'space:reservationItem:publicList', '#', 'admin', sysdate(), '', null, '所有预约页面加载已通过预约房间与记录权限');

update sys_menu
   set order_num = 3,
       update_by = 'admin',
       update_time = sysdate()
 where menu_id = 2012;

update sys_menu
   set order_num = 4,
       update_by = 'admin',
       update_time = sysdate()
 where menu_id = 2013;

update sys_menu
   set order_num = 5,
       update_by = 'admin',
       update_time = sysdate()
 where menu_id = 2014;

update sys_menu
   set order_num = 6,
       update_by = 'admin',
       update_time = sysdate()
 where menu_id = 2015;

update sys_menu
   set order_num = 7,
       update_by = 'admin',
       update_time = sysdate()
 where menu_id = 2016;

update sys_menu
   set menu_name = '占用公开场次查询',
       perms = 'space:reservationItem:publicList',
       remark = '房间占用详情页加载公开预约场次权限',
       update_by = 'admin',
       update_time = sysdate()
 where menu_id = 2057;

insert into sys_role_menu(role_id, menu_id)
select 100, menu_id from sys_menu where menu_id in (2017, 2061)
   and exists (select 1 from sys_role where role_id = 100)
   and not exists (
     select 1 from sys_role_menu srm where srm.role_id = 100 and srm.menu_id = sys_menu.menu_id
   );

insert into sys_role_menu(role_id, menu_id)
select 101, menu_id from sys_menu where menu_id in (2017, 2061)
   and exists (select 1 from sys_role where role_id = 101)
   and not exists (
     select 1 from sys_role_menu srm where srm.role_id = 101 and srm.menu_id = sys_menu.menu_id
   );

insert into sys_role_menu(role_id, menu_id)
select 102, menu_id from sys_menu where menu_id in (2017, 2061)
   and exists (select 1 from sys_role where role_id = 102)
   and not exists (
     select 1 from sys_role_menu srm where srm.role_id = 102 and srm.menu_id = sys_menu.menu_id
   );

insert into sys_role_menu(role_id, menu_id)
select role_id, 2057 from sys_role where role_id in (100, 101, 102)
   and not exists (
     select 1 from sys_role_menu srm where srm.role_id = sys_role.role_id and srm.menu_id = 2057
   );
