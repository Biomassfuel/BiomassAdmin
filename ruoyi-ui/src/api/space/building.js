import request from '@/utils/request'

export function listBuilding(query) {
  return request({ url: '/space/building/list', method: 'get', params: query })
}

export function getBuilding(buildingId) {
  return request({ url: '/space/building/' + buildingId, method: 'get' })
}

export function addBuilding(data) {
  return request({ url: '/space/building', method: 'post', data: data })
}

export function updateBuilding(data) {
  return request({ url: '/space/building', method: 'put', data: data })
}

export function delBuilding(buildingId) {
  return request({ url: '/space/building/' + buildingId, method: 'delete' })
}
