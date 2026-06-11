import request from '@/utils/request'

export function listReservation(query) {
  return request({ url: '/space/reservation/list', method: 'get', params: query })
}

export function listMyReservation(query) {
  return request({ url: '/space/reservation/my/list', method: 'get', params: query })
}

export function listPendingReservation(query) {
  return request({ url: '/space/reservation/pending/list', method: 'get', params: query })
}

export function listCancelPendingReservation(query) {
  return request({ url: '/space/reservation/cancel-pending/list', method: 'get', params: query })
}

export function getReservation(reservationId) {
  return request({ url: '/space/reservation/' + reservationId, method: 'get' })
}

export function addReservation(data) {
  return request({ url: '/space/reservation', method: 'post', data: data })
}

export function updateReservation(data) {
  return request({ url: '/space/reservation', method: 'put', data: data })
}

export function delReservation(reservationId) {
  return request({ url: '/space/reservation/' + reservationId, method: 'delete' })
}

export function cancelReservation(reservationId) {
  return request({ url: '/space/reservation/' + reservationId + '/cancel', method: 'put' })
}

export function approveReservation(reservationId, data) {
  return request({ url: '/space/reservation/' + reservationId + '/approve', method: 'put', data: data || {} })
}

export function rejectReservation(reservationId, data) {
  return request({ url: '/space/reservation/' + reservationId + '/reject', method: 'put', data: data || {} })
}

export function approveCancelReservation(reservationId, data) {
  return request({ url: '/space/reservation/' + reservationId + '/cancel-audit/approve', method: 'put', data: data || {} })
}

export function rejectCancelReservation(reservationId, data) {
  return request({ url: '/space/reservation/' + reservationId + '/cancel-audit/reject', method: 'put', data: data || {} })
}

export function approveReservationItem(itemId, data) {
  return request({ url: '/space/reservation/item/' + itemId + '/approve', method: 'put', data: data || {} })
}

export function rejectReservationItem(itemId, data) {
  return request({ url: '/space/reservation/item/' + itemId + '/reject', method: 'put', data: data || {} })
}

export function approveCancelReservationItem(itemId, data) {
  return request({ url: '/space/reservation/item/' + itemId + '/cancel-audit/approve', method: 'put', data: data || {} })
}

export function rejectCancelReservationItem(itemId, data) {
  return request({ url: '/space/reservation/item/' + itemId + '/cancel-audit/reject', method: 'put', data: data || {} })
}
