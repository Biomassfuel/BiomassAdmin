<template>
  <admin-home v-if="homeRole === 'admin'" />
  <space-role-home v-else />
</template>

<script>
import AdminHome from './AdminHome'
import SpaceRoleHome from './SpaceRoleHome'
import { resolveHomeRole } from '@/utils/home'

export default {
  name: 'Index',
  components: {
    AdminHome,
    SpaceRoleHome
  },
  computed: {
    homeRole() {
      return resolveHomeRole(this.$store.getters.roles)
    }
  }
}
</script>
