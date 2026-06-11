<template>
  <div class="app-container">
    <approved-room-list />
  </div>
</template>

<script>
import ApprovedRoomList from './ApprovedRoomList'

export default {
  name: 'SpaceAllReservation',
  components: { ApprovedRoomList }
}
</script>
