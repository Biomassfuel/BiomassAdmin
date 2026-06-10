import request from '@/utils/request'

export function listRoomEquipment(query) {
  return request({ url: '/space/room/equipment/list', method: 'get', params: query })
}

export function getRoomEquipment(roomEquipmentId) {
  return request({ url: '/space/room/equipment/' + roomEquipmentId, method: 'get' })
}

export function addRoomEquipment(data) {
  return request({ url: '/space/room/equipment', method: 'post', data: data })
}

export function updateRoomEquipment(data) {
  return request({ url: '/space/room/equipment', method: 'put', data: data })
}

export function delRoomEquipment(roomEquipmentId) {
  return request({ url: '/space/room/equipment/' + roomEquipmentId, method: 'delete' })
}
