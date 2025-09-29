/**
 * main.js
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Plugins
import { registerPlugins } from '@/plugins'
import '@mdi/font/css/materialdesignicons.css'; 
import 'material-design-icons-iconfont/dist/material-design-icons.css'


// Components
import App from './App.vue'

// Composables
import { createApp } from 'vue'

// Styles
import 'unfonts.css'


//Router
import router from './router/router'

//Pinia
import { createPinia } from 'pinia';
import { useUserStore } from './stores/user';

const pinia = createPinia()
const app = createApp(App)


registerPlugins(app)
app.use(pinia)
app.use(router)
const userStore = useUserStore()
userStore.initializeAuth()
app.mount('#app')
