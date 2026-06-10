import request from '@/utils/request'

export function listBlackout(query) {
  return request({ url: '/space/blackout/list', method: 'get', params: query })
}

export function getBlackout(blackoutId) {
  return request({ url: '/space/blackout/' + blackoutId, method: 'get' })
}

export function addBlackout(data) {
  return request({ url: '/space/blackout', method: 'post', data: data })
}

export function updateBlackout(data) {
  return request({ url: '/space/blackout', method: 'put', data: data })
}

export function delBlackout(blackoutId) {
  return request({ url: '/space/blackout/' + blackoutId, method: 'delete' })
}
