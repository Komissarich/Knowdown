<template>
    <div class="d-flex justify-center align-center" style="height: 60vh;">
  <v-sheet  width="350" height="450" :elevation="16">
    <v-form validate-on="submit lazy" @submit.prevent="submit">
      <v-text-field
        class="mt-10 mx-auto"
        width="320"
        v-model="userName"
        :rules="rules"
        label="Username"
        style="min-width: 50px; display: block;"
      ></v-text-field>

        <v-text-field
       class="mx-auto"
        width="320"
        v-model="userEmail"
        :rules="rules"
        style="min-width: 50px; display: block;"
        label="Password"
      ></v-text-field>



      <v-btn
        :loading="loading"
        class="mt-2 mx-auto"
        text="Login"
        type="submit"
        color="green-lighten-1"
        style="min-width: 140px;display: block;"
        rounded="lg"
        @click="router.push('/login')"
      ></v-btn>
    </v-form>
    <div class="d-flex flex-column justify-center align-center">
        <p class="ma-4 ">Doesn't have an account?</p>
        <v-btn
        class="text-none mb-4"
        
        color="indigo-darken-3"
       
        variant="flat"
        
        style="min-width: 140px;"
        rounded="lg"
        @click="router.push('/register')"
      >
        REGISTER
      </v-btn>
    </div>
   
  </v-sheet>
  </div>
</template>


<script setup>
    import router from '@/router/router'
    import { ref } from 'vue'
    const rules = [value => checkApi(value)]

    const loading = ref(false)
    const userName = ref('')
    const userPassword = ref('')
    async function submit (event) {
        alert('api Login')
        loading.value = true
        const results = await event
        loading.value = false
        
    }

    let timeout = -1
   
    async function checkApi (userName) {
      alert('apiLogin')
        return new Promise(resolve => {
        clearTimeout(timeout)

        timeout = setTimeout(() => {
            if (!userName) return resolve('Please enter a user name.')
                if (userName === 'johnleider') return resolve('User name already taken. Please try another one.')
                    return resolve(true)
                }, 1000)
                })
    }
</script>