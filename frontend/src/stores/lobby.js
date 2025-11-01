import { defineStore } from "pinia";
import { ref } from "vue";

export const useLobbyStore = defineStore(
  "lobby",
  () => {
    const lobby_id = ref("");
    const lobby_name = ref("");
    const isPrivate = ref(false);
    const maxPlayers = ref("");
    const creator = ref("");

    function createLobby(lobby_id, lobby_name, isPrivate, maxPlayers, creator) {
      this.lobby_id = lobby_id;
      this.lobby_name = lobby_name;
      this.isPrivate = isPrivate;
      this.maxPlayers = maxPlayers;
      this.creator = creator;
    }

    function clearLobby(name) {
      this.lobby_id = null;
      this.lobby_name = null;
      this.isPrivate = false;
      this.maxPlayers = 0;
      this.creator = null;
    }

    return {
      lobby_id,
      lobby_name,
      isPrivate,
      maxPlayers,
      creator,
      clearLobby,
      createLobby,
    };
  },
  {
    persist: {
      storage: localStorage,
    },
  }
);
