import request from '@/utils/request'

export function getStatisticsDashboard(query) {
  return request({ url: '/space/statistics/dashboard', method: 'get', params: query })
}

export function listStatistics(query) {
  return request({ url: '/space/statistics/list', method: 'get', params: query })
}
