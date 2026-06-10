import request from '@/utils/request'

export function listRoomType(query) {
  return request({ url: '/space/room/type/list', method: 'get', params: query })
}

export function getRoomType(typeId) {
  return request({ url: '/space/room/type/' + typeId, method: 'get' })
}

export function addRoomType(data) {
  return request({ url: '/space/room/type', method: 'post', data: data })
}

export function updateRoomType(data) {
  return request({ url: '/space/room/type', method: 'put', data: data })
}

export function delRoomType(typeId) {
  return request({ url: '/space/room/type/' + typeId, method: 'delete' })
}
