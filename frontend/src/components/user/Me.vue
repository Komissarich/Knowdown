<template>
<p>Profile</p>
<p>{{ userStore.username }}</p>
<VBtn @click="logout" color="red">Logout</VBtn>
</template>

<script setup>
import router from '@/router/router';
import { useUserStore } from '@/stores/user';
import { VBtn } from 'vuetify/components';


const userStore = useUserStore()

function logout() {
    
    userStore.logout()
    router.push('/')
}
</script>