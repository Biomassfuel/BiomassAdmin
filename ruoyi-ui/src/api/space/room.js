import request from '@/utils/request'

export function listRoom(query) {
  return request({ url: '/space/room/list', method: 'get', params: query })
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

export function importRoomUrl() {
  return '/space/room/importData'
}

export function importRoomTemplateUrl() {
  return '/space/room/importTemplate'
}
