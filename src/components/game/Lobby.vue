<template>
    
      
  <v-container style= "max-width:900px">
    <!-- Первый ряд: список игроков (слева) и настройки/код (справа) -->
    <v-row>
      <v-col cols="12" md="6">
        <!-- Область со списком игроков -->
        <v-card style="height: 400px;  overflow-y: auto;">
          <v-card-title>Players</v-card-title>
          <v-card-text>

         <v-list-item
              v-for="player in players"
              :key="player.id"
              :title="player.name"
              prepend-icon="mdi-account"
            ></v-list-item>
          </v-card-text>
            
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <!-- Область с настройками и кодом для игры -->
        <v-card style="width: 300px;">
        <v-card-title>Link</v-card-title>
          <v-card-text class="medium font text-white-600">
          Room id:  {{ room_id }}
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Второй ряд: чат (слева) и кнопки (справа) -->
    <v-row>
      <v-col cols="12" md="6">
        <!-- Область с игровым чатом -->
        <v-card style="height: 300px;">
          <v-card-title>Game chat</v-card-title>
          <v-card-text>
            <!-- <textarea readonly></textarea> -->
            <v-textarea readonly
      :model-value="value"
      variant="solo"
      rows="15"
      no-resize
    ></v-textarea>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <!-- Область с кнопками старта и выхода -->
        <v-card style="width: 300px;">
          <v-card-title>Actions</v-card-title>
          <v-card-actions>
            <v-btn color="primary">Старт</v-btn>
            <v-btn color="error">Выход</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>


</template>


<script setup>
import { useUserStore } from '@/stores/user';
import { ref } from 'vue';
const userStore = useUserStore()
const players = ref([])

players.value.push({name: userStore.username})
for (var i = 0; i < 30; i++) {
    players.value.push({name: "Empty slot"})
}

const room_id = crypto.randomUUID().substring(0, 6)
console.log(room_id)

</script>