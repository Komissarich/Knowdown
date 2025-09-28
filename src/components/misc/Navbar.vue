<template>
<div>
     <VAppBar color="primary"> 
      <VAppBarNavIcon variant="text" @click.stop="drawer = !drawer"></VAppBarNavIcon>
       <VToolbarTitle ><VIcon icon="mdi-brain"></VIcon><router-link 
        to="/" 
        style="text-decoration: none; color: inherit;">
        Knowdown
        </router-link>
        </VToolbarTitle>
        <VBtn variant="text" icon="mdi-chart-box-outline" @click="router.push('/statistics')"> </VBtn>
        <VBtn icon="mdi-magnify" variant="text" @click="router.push('/game/search')"></VBtn>
        <VBtn variant="text" icon="mdi-account" @click="router.push('/users/me')"> </VBtn>
        
     </VAppBar>

      <VNavigationDrawer
        v-model="drawer"
        :location="$vuetify.display.mobile ? 'bottom' : undefined"
        temporary
      >
        <VListItem
          link
          to="/users/me"
          prepend-avatar="https://randomuser.me/api/portraits/men/78.jpg"
          title="John Doe"
        ></VListItem>
        
        <VDivider></VDivider>
        <VListItem
          link
          to="/game/search"
          prepend-icon="mdi-magnify"
          title="Search game"
        ></VListItem>
         <VListItem
          link
          to="/game/create"
          prepend-icon="mdi-dice-3-outline"
          title="Create game"
        ></VListItem>
        <VListItem
          link
          to="/statistics"
          prepend-icon="mdi-chart-box-outline"
          title="Statistics"
        ></VListItem>

      </VNavigationDrawer>

      
</div>
</template>

<script setup>
    import router from '@/router/router'
import { ref, watch } from 'vue'
    const drawer = ref(false)
    const group = ref(null)

    watch(group, () => {
        drawer.value = false
    })
</script>