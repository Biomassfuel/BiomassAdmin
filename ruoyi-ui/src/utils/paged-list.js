export function fetchAllPages(listRequest, params = {}, options = {}) {
  const pageSize = options.pageSize || 500
  const rows = []

  function fetchPage(pageNum) {
    return listRequest({
      ...params,
      pageNum,
      pageSize
    }).then(response => {
      const pageRows = response.rows || []
      rows.push(...pageRows)

      const total = Number(response.total)
      if (Number.isFinite(total) && total >= 0) {
        return rows.length >= total ? rows : fetchPage(pageNum + 1)
      }

      return pageRows.length < pageSize ? rows : fetchPage(pageNum + 1)
    })
  }

  return fetchPage(1)
}
