<template>
  <VApp>
    <Navbar v-if="route.meta.showNavbar"></Navbar>
    <VMain>
      <RouterView v-slot="{ Component }">
        <v-fade-transition hide-on-leave>
          <component :is="Component" />
        </v-fade-transition>
      </RouterView>

    </VMain>
  </VApp>
</template>

<script setup>
import Navbar from "./components/misc/Navbar.vue";

import {useRoute} from "vue-router";
const route = useRoute();
</script>
