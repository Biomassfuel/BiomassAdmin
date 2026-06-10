import request from '@/utils/request'

export function listTimePeriod(query) {
  return request({ url: '/space/time-period/list', method: 'get', params: query })
}

export function getTimePeriod(periodId) {
  return request({ url: '/space/time-period/' + periodId, method: 'get' })
}

export function addTimePeriod(data) {
  return request({ url: '/space/time-period', method: 'post', data: data })
}

export function updateTimePeriod(data) {
  return request({ url: '/space/time-period', method: 'put', data: data })
}

export function delTimePeriod(periodId) {
  return request({ url: '/space/time-period/' + periodId, method: 'delete' })
}
