import request from '@/utils/request'

export function listOrg(query) {
  return request({ url: '/space/org/list', method: 'get', params: query })
}

export function getOrg(orgId) {
  return request({ url: '/space/org/' + orgId, method: 'get' })
}

export function addOrg(data) {
  return request({ url: '/space/org', method: 'post', data: data })
}

export function updateOrg(data) {
  return request({ url: '/space/org', method: 'put', data: data })
}

export function delOrg(orgId) {
  return request({ url: '/space/org/' + orgId, method: 'delete' })
}
