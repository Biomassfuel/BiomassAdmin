export function statusText(status) {
  return {
    '0': '草稿',
    '1': '待审核',
    '2': '已通过',
    '3': '部分通过',
    '4': '已驳回',
    '5': '已取消',
    '6': '已结束'
  }[status] || status || '-'
}

export function reservationTypeText(type) {
  return {
    '0': '普通预约',
    '1': '长期预约'
  }[type] || type || '-'
}

export function itemStatusText(status) {
  return {
    '1': '待审核',
    '2': '已通过',
    '3': '已驳回',
    '4': '冲突待处理',
    '5': '已取消',
    '6': '已结束'
  }[status] || status || '-'
}

export function auditActionText(action) {
  return {
    '0': '提交',
    '1': '通过',
    '2': '驳回',
    '4': '取消',
    '5': '场次通过',
    '6': '场次驳回',
    '7': '发起取消审核',
    '8': '取消审核通过',
    '9': '取消审核驳回',
    'A': '场次取消通过',
    'B': '场次取消驳回'
  }[action] || action || '-'
}

export const reservationTypeOptions = [
  { label: '普通预约', value: '0' },
  { label: '长期预约', value: '1' }
]

export const reservationStatusOptions = [
  { label: '草稿', value: '0' },
  { label: '待审核', value: '1' },
  { label: '已通过', value: '2' },
  { label: '部分通过', value: '3' },
  { label: '已驳回', value: '4' },
  { label: '已取消', value: '5' },
  { label: '已结束', value: '6' }
]

export const itemStatusOptions = [
  { label: '待审核', value: '1' },
  { label: '已通过', value: '2' },
  { label: '已驳回', value: '3' },
  { label: '冲突待处理', value: '4' },
  { label: '已取消', value: '5' },
  { label: '已结束', value: '6' }
]

export const auditActionOptions = [
  { label: '提交', value: '0' },
  { label: '通过', value: '1' },
  { label: '驳回', value: '2' },
  { label: '取消', value: '4' },
  { label: '场次通过', value: '5' },
  { label: '场次驳回', value: '6' },
  { label: '发起取消审核', value: '7' },
  { label: '取消审核通过', value: '8' },
  { label: '取消审核驳回', value: '9' },
  { label: '场次取消通过', value: 'A' },
  { label: '场次取消驳回', value: 'B' }
]

export const standardPeriodCodes = ['MORNING', 'AFTERNOON', 'EVENING']
export const standardPeriodNames = ['上午', '下午', '晚间']

export function standardPeriods(rows) {
  const list = rows || []
  const matched = list.filter(item => {
    return standardPeriodCodes.includes(item.periodCode) || standardPeriodNames.includes(item.periodName)
  })
  return (matched.length ? matched : list).slice(0, 3)
}

export function weekdayValue(dateText) {
  const date = parseLocalDate(dateText)
  return date ? String(date.getDay()) : ''
}

export function weekdayText(value) {
  return ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][Number(value)] || value
}

export function formatDate(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

export function parseLocalDate(dateText) {
  if (!dateText) return null
  const parts = String(dateText).split('-').map(Number)
  if (parts.length !== 3 || parts.some(Number.isNaN)) return null
  const date = new Date(parts[0], parts[1] - 1, parts[2])
  if (date.getFullYear() !== parts[0] || date.getMonth() !== parts[1] - 1 || date.getDate() !== parts[2]) {
    return null
  }
  return date
}

function parseTime(timeText) {
  if (!timeText) return null
  const parts = String(timeText).trim().split(':').map(Number)
  if (parts.length < 2 || parts.length > 3 || parts.some(Number.isNaN)) return null
  const [hours, minutes, seconds = 0] = parts
  if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59 || seconds < 0 || seconds > 59) {
    return null
  }
  return { hours, minutes, seconds }
}

export function parseLocalDateTime(dateText, timeText) {
  const date = parseLocalDate(dateText)
  const time = parseTime(timeText)
  if (!date || !time) return null
  date.setHours(time.hours, time.minutes, time.seconds, 0)
  return date
}

function todayStart() {
  const now = new Date()
  return new Date(now.getFullYear(), now.getMonth(), now.getDate())
}

export function disablePastDate(time) {
  return time.getTime() < todayStart().getTime()
}

export function isBeforeToday(dateText) {
  const date = parseLocalDate(dateText)
  return !!date && date.getTime() < todayStart().getTime()
}

export function isReservationStarted(item, now = new Date()) {
  const startTime = parseLocalDateTime(item && item.bookingDate, item && item.startTime)
  return !!startTime && startTime.getTime() <= now.getTime()
}

export function findStartedReservation(items, now = new Date()) {
  return (items || []).find(item => isReservationStarted(item, now)) || null
}
