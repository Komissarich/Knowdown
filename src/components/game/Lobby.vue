<template>
    
      
  <v-container style= "max-width:900px">

    <v-row>
      <v-col cols="12" md="6">
        <v-card  color="teal-lighten-4" title="Players"  style="height: 400px;  overflow-y: auto;">
          <v-card-text>
         <v-list-item
              v-for="player in players"
              :key="player.id"
              :title="player.name"
              color="primary"
              variant="plain"
              prepend-icon="mdi-account"
            ></v-list-item>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card  style="width: 300px;">
            <div style="text-align: center;"> <h2 class="mx-auto"> {{ route.query.lobbyName }}</h2></div>
            
          <v-card-text class="medium font text-white-600">
          Room id:  {{ route.query.lobbyId }}
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12" md="6">
        <v-card style="height: 400px; " color="blue-grey">
          <v-card-title>Game chat</v-card-title>
          <v-card-text>
            <v-textarea readonly
      :model-value="value"
      variant="solo"
      rows="12"
      class="mt-3"
      no-resize
    ></v-textarea>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card style="width: 300px;">
          <v-card-title>Actions</v-card-title>
          <v-card-actions>
            <v-btn color="primary">START</v-btn>
            <v-btn color="error">EXIT</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>


<script setup>
    import { useRoute } from 'vue-router'
    import { useUserStore } from '@/stores/user';
    import { onMounted, ref } from 'vue';
    const userStore = useUserStore()
    const players = ref([])
    const route = useRoute()
    players.value.push({name: userStore.username})
    onMounted(() => {
        for (var i = 0; i < route.query.maxPlayers; i++) {
            players.value.push({name: "Empty slot"})
        }
    })

</script>