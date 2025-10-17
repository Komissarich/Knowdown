<template>
  <div>
    <VAppBar color="primary">
      <template v-slot:image>
        <v-img
          gradient="to top right, rgba(19,84,122,.8), rgba(128,208,199,.8)"
        ></v-img>
      </template>
      <VAppBarNavIcon
        variant="text"
        @click.stop="drawer = !drawer"
      ></VAppBarNavIcon>
      <VToolbarTitle
        ><VIcon icon="mdi-brain"></VIcon
        ><router-link to="/" style="text-decoration: none; color: inherit">
          Knowdown
        </router-link>
      </VToolbarTitle>
      <VBtn
        v-tooltip="'Statistics'"
        variant="text"
        icon="mdi-chart-box-outline"
        @click="router.push('/statistics')"
      >
      </VBtn>
      <VBtn
        v-tooltip="'Search game'"
        icon="mdi-magnify"
        variant="text"
        @click="router.push('/lobby/search')"
      ></VBtn>
      <VBtn
        v-tooltip="'Create game'"
        variant="text"
        icon="mdi-dice-3-outline"
        @click="router.push('/lobby/create')"
      >
      </VBtn>
      <VBtn
        v-tooltip="'Profile'"
        variant="text"
        icon="mdi-account"
        @click="router.push('/users/me')"
      >
      </VBtn>
    </VAppBar>

    <VNavigationDrawer
      v-model="drawer"
      :location="$vuetify.display.mobile ? 'bottom' : undefined"
      temporary
    >
      <VList lines="two">
        <VListItem
          link
          to="/users/me"
          prepend-avatar="https://randomuser.me/api/portraits/men/78.jpg"
          title="John Doe"
        ></VListItem>

        <VDivider></VDivider>
        <VListItem
          link
          to="/lobby/search"
          prepend-icon="mdi-magnify"
          title="Search game"
        ></VListItem>
        <VListItem
          link
          to="/lobby/create"
          prepend-icon="mdi-dice-3-outline"
          title="Create game"
        ></VListItem>
        <VListItem
          link
          to="/statistics"
          prepend-icon="mdi-chart-box-outline"
          title="Statistics"
        ></VListItem>
      </VList>
    </VNavigationDrawer>
  </div>
</template>

<script setup>
import router from "@/router/router";
import { ref, watch } from "vue";
const drawer = ref(false);
const group = ref(null);

watch(group, () => {
  drawer.value = false;
});
</script>
