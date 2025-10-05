<template>
    
      
  <v-container style= "max-width:900px">

    <v-row>
      <v-col cols="12" md="6">
        <v-card  color="teal-lighten-4" title="Players"  style="height: 400px;  overflow-y: auto;">
          <v-card-text>
           <template v-for="(player, index) in players" :key="player.id">
             <v-hover
              v-slot="{ isHovering, props }"
              
            >
                <v-list-item
                  :elevation="isHovering ? 6 : 0"
                  :class="{'on-hover': isHovering }"
                  :title="player.name"
                  prepend-avatar="https://randomuser.me/api/portraits/men/78.jpg"
                  color="teal-lighten-4"
                  variant='outlined'
                  v-bind="props"
                  class="mt-3"
                ></v-list-item>
                </v-hover>
                <v-divider v-if="index < players.length - 1" />
                 
        </template>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card  style="width: 300px;">
            <div style="text-align: center;"> <h2 class="mx-auto"> {{ route.params.lobbyName }}</h2></div>
            
          <v-card-text class="medium font text-white-600">
            Room id: {{ route.params.lobbyId }}
            <hr></hr>
             Link: <a href="">https://knowdown/game/lobby/{{ route.params.lobbyId }}</a>
            <v-img :src="`https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=${route.params.lobbyId}`" ></v-img>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12" md="6">
        <v-card style="height: 480px; " class="opacity-90	" color="blue-grey">
          <v-card-title>Game chat</v-card-title>
          <v-card-text>
            <v-textarea 
            append-inner-icon="mdi-comment"
            readonly
            v-model:model-value="chat"
            variant="solo"
            rows="12"
            class="mt-3"
            no-resize
          ></v-textarea>
            <div class="d-flex flex-row">
              <v-text-field v-model="message"  label="Enter message" variant="outlined"></v-text-field>
           <v-btn label="send" class="mt-2 ml-2" color="green" icon="mdi-send" @click="SendMessage"></v-btn>
            </div>
          
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
    import { onMounted, onUnmounted, ref } from 'vue';
    import { Client, Stomp } from '@stomp/stompjs';
    const userStore = useUserStore()
    const players = ref([])
    const route = useRoute()
    var chat = ref("")
    const message = ref("")
    players.value.push({id: 0, name: userStore.username})
    onMounted(() => {
        for (var i = 1; i <= route.params.maxPlayers; i++) {
          players.value.push({id: i, name: "Empty slot"})
        }
    })

  
      
    const client = new Client({
    brokerURL: 'ws://localhost:8080/ws', // Replace with your WebSocket URL
  
    debug: function (str) {
      console.log(str); // Optional: enable debug logging
    },
    reconnectDelay: 5000, // Optional: reconnect after 5 seconds on disconnect
    heartbeatIncoming: 4000, // Optional: server heartbeat interval
    heartbeatOutgoing: 4000, // Optional: client heartbeat interval
  });
   
client.onStompError = function (frame) {
  console.error('Broker reported error:', frame.headers['message']);
  console.error('Additional details:', frame.body);
};
   client.onConnect = function (frame) {
  console.log('Connected to STOMP broker!');


  client.subscribe("/topic/messages", function (message) {
    console.log('Received message:', message.body);
    var parsed_message = JSON.parse(message.body)
    chat.value += parsed_message.username + ": " +  parsed_message.message + "\n"
    console.log(chat)
  });


  
};
  client.activate()
    

    onUnmounted(() => {
      console.log("Client deactivated")
     client.deactivate()
    })
    


    function SendMessage() {
      console.log("try to send " + message.value)
      client.publish({destination: "/app/chat", body: JSON.stringify({username: userStore.username, message: message.value})} )
    }

</script>