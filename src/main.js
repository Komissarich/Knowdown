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

const app = createApp(App)

registerPlugins(app)

app.use(router)

app.mount('#app')
