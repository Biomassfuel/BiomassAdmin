-- 空间预约：自动刷新已结束状态定时任务
-- 适用于已经执行过 space_reservation_20260609.sql 的数据库。

insert into sys_job (
  job_name,
  job_group,
  invoke_target,
  cron_expression,
  misfire_policy,
  concurrent,
  status,
  create_by,
  create_time,
  remark
)
select
  '空间预约已结束刷新',
  'DEFAULT',
  'spaceReservationTask.refreshFinishedReservations',
  '0 * * * * ?',
  '3',
  '1',
  '0',
  'admin',
  sysdate(),
  '每分钟刷新已结束预约状态'
where not exists (
  select 1
  from sys_job
  where invoke_target = 'spaceReservationTask.refreshFinishedReservations'
);
