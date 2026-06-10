import request from '@/utils/request'

export function listImportBatch(query) {
  return request({ url: '/space/import-batch/list', method: 'get', params: query })
}

export function getImportBatch(batchId) {
  return request({ url: '/space/import-batch/' + batchId, method: 'get' })
}

export function addImportBatch(data) {
  return request({ url: '/space/import-batch', method: 'post', data: data })
}

export function updateImportBatch(data) {
  return request({ url: '/space/import-batch', method: 'put', data: data })
}

export function delImportBatch(batchId) {
  return request({ url: '/space/import-batch/' + batchId, method: 'delete' })
}
