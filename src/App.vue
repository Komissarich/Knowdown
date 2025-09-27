
<template>
  <VApp>
  <Navbar></Navbar>
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
  import Navbar from './components/misc/Navbar.vue';
  
  
</script>
