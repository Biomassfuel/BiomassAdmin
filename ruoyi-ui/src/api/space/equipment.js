import request from '@/utils/request'

export function listEquipment(query) {
  return request({ url: '/space/equipment/list', method: 'get', params: query })
}

export function getEquipment(equipmentId) {
  return request({ url: '/space/equipment/' + equipmentId, method: 'get' })
}

export function addEquipment(data) {
  return request({ url: '/space/equipment', method: 'post', data: data })
}

export function updateEquipment(data) {
  return request({ url: '/space/equipment', method: 'put', data: data })
}

export function delEquipment(equipmentId) {
  return request({ url: '/space/equipment/' + equipmentId, method: 'delete' })
}
