<template> 
<div class="d-flex justify-center align-center" style="height: 60vh;">
<v-card>
    <v-card-title>Game settings</v-card-title>
    <v-range-slider
    v-model="value"
    step="1"
    thumb-label="always"
  ></v-range-slider>
</v-card>



</div>







</template>

<script setup>
  import { ref } from 'vue'

  const value = ref([0, 30])
</script>