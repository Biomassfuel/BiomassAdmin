import request from '@/utils/request'

export function listAuditLog(query) {
  return request({ url: '/space/audit-log/list', method: 'get', params: query })
}

export function getAuditLog(logId) {
  return request({ url: '/space/audit-log/' + logId, method: 'get' })
}

export function addAuditLog(data) {
  return request({ url: '/space/audit-log', method: 'post', data: data })
}

export function updateAuditLog(data) {
  return request({ url: '/space/audit-log', method: 'put', data: data })
}

export function delAuditLog(logId) {
  return request({ url: '/space/audit-log/' + logId, method: 'delete' })
}
