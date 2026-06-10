import request from '@/utils/request'

export function listReservationItem(query) {
  return request({ url: '/space/reservation/item/list', method: 'get', params: query })
}

export function getReservationItem(itemId) {
  return request({ url: '/space/reservation/item/' + itemId, method: 'get' })
}

export function addReservationItem(data) {
  return request({ url: '/space/reservation/item', method: 'post', data: data })
}

export function updateReservationItem(data) {
  return request({ url: '/space/reservation/item', method: 'put', data: data })
}

export function delReservationItem(itemId) {
  return request({ url: '/space/reservation/item/' + itemId, method: 'delete' })
}
