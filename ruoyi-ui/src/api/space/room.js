import request from '@/utils/request'

export function listRoom(query) {
  return request({ url: '/space/room/list', method: 'get', params: query })
}

export function listApprovedReservationRoom(query) {
  return request({ url: '/space/room/approved-reservation/list', method: 'get', params: query })
}

export function listRecycleRoom(query) {
  return request({ url: '/space/room/recycle/list', method: 'get', params: query })
}

export function getRoom(roomId) {
  return request({ url: '/space/room/' + roomId, method: 'get' })
}

export function addRoom(data) {
  return request({ url: '/space/room', method: 'post', data })
}

export function updateRoom(data) {
  return request({ url: '/space/room', method: 'put', data })
}

export function delRoom(roomId) {
  return request({ url: '/space/room/' + roomId, method: 'delete' })
}

export function restoreRoom(roomId) {
  return request({ url: '/space/room/restore/' + roomId, method: 'put' })
}

export function forceDelRoom(roomId) {
  return request({ url: '/space/room/force/' + roomId, method: 'delete' })
}

export function importRoomUrl() {
  return '/space/room/importData'
}

export function importRoomTemplateUrl() {
  return '/space/room/importTemplate'
}
