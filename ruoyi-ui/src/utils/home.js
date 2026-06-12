const ROLE_PRIORITY = ['admin', 'space_admin', 'space_teacher', 'space_student']

export function hasRole(roles, role) {
  return Array.isArray(roles) && roles.includes(role)
}

export function hasPermi(permissions, permission) {
  if (!Array.isArray(permissions)) return false
  return permissions.includes('*:*:*') || permissions.includes(permission)
}

export function hasAnyPermi(permissions, permissionList) {
  return Array.isArray(permissionList) && permissionList.some(permission => hasPermi(permissions, permission))
}

export function resolveHomeRole(roles) {
  if (!Array.isArray(roles)) return 'unassigned'
  return ROLE_PRIORITY.find(role => roles.includes(role)) || 'unassigned'
}

export function isSpaceHomeRole(role) {
  return ['space_admin', 'space_teacher', 'space_student', 'unassigned'].includes(role)
}

