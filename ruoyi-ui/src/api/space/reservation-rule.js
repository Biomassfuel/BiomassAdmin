import request from '@/utils/request'

export function listReservationRule(query) {
  return request({ url: '/space/reservation/rule/list', method: 'get', params: query })
}

export function getReservationRule(ruleId) {
  return request({ url: '/space/reservation/rule/' + ruleId, method: 'get' })
}

export function addReservationRule(data) {
  return request({ url: '/space/reservation/rule', method: 'post', data: data })
}

export function updateReservationRule(data) {
  return request({ url: '/space/reservation/rule', method: 'put', data: data })
}

export function delReservationRule(ruleId) {
  return request({ url: '/space/reservation/rule/' + ruleId, method: 'delete' })
}
