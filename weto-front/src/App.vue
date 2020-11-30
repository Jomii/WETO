<template>
  <v-app>
    <toolbar v-if="!login"/>
    <v-content class="ml-4 mr-4 mt-2">
      <router-view/>
    </v-content>
  </v-app>
</template>

<script>
  import Toolbar from './components/Toolbar.vue'
  import api from './backend-api'

  export default {
    name: "App",
    data: function (){
      return {
        errors: []
      }
    },
    components: {
      Toolbar
    },
    created(){
      this.checkLogin()
      this.getUser()
    },
    computed:{
      login() {
        return this.$route.path === '/'
      }
    },
    methods: {
      checkLogin(){
        api.pollLogin().catch(error => {
          this.errors.push(error)
        })
      },
      getUser(){
        api.getUser().then(response => {
          this.$store.commit("logUser", JSON.parse(response.data))
        }).catch(err => {
          this.errors.push(err);
        })
      }
    }
  }
</script>