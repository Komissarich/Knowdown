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
        label="Email"
      ></v-text-field>

       <v-text-field
       class="mx-auto"
        width="320"
        v-model="userPassword"
        :rules="rules"
        style="min-width: 50px; display: block;"
        label="Password"
      ></v-text-field>

      <v-btn
        :loading="loading"
        class="mt-2 mx-auto"
        color="green-lighten-1"
        text="Register"
        type="submit"
        style="min-width: 140px;display: block;"
        rounded="lg"
        block
      ></v-btn>
    </v-form>
    <div class="d-flex flex-column justify-center align-center">
        <p class="ma-4 ">Already have an account?</p>
        <v-btn
        class="text-none mb-4"
        
        color="indigo-darken-3"
       
        variant="flat"
        
        style="min-width: 140px;"
        rounded="lg"
        @click="router.push('/login')"
      >
        LOG IN
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
    const userEmail = ref('')
    async function submit (event) {
      alert('api Register')
        loading.value = true
        const results = await event
        loading.value = false
        
    }

    let timeout = -1
    async function checkApi (userName) {
     
       
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