<template>
  <div class="loginForm">
    <v-card outlined class="loginCard">
        <v-card-title>
          <h3>
            WETO Login
          </h3>
        </v-card-title>
        <v-card-text>
          <v-form v-model="isValid" ref="form">
            <v-text-field label="Email" v-model="email" :rules="emailRules" outlined required></v-text-field>
            <v-text-field label="Password" v-model="password" type="password" :rules="passwordRules" outlined required></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-btn @click="submitLogin" class="text--white primary" color="#4e008e" :disabled="!isValid">Login</v-btn>
        </v-card-actions>
      <v-alert type="error" dismissible v-if="errors.length != 0">The username or password you entered was incorrect.</v-alert>
    </v-card>
  </div>
</template>

<script>
  import api from '../backend-api'

  export default {
    name: 'login',
    data: () => ({
      email: null,
      emailRules: [
        v => !!v || 'E-mail is required',
        v => /.+@.+\..+/.test(v) || 'E-mail must be valid',
      ],
      password: null,
      passwordRules: [
        v => !!v || 'Password is required'
      ],
      isValid: true,
      backendResponse: [],
      errors: [],
    }),
    created() {
      this.checkLogin();
    },
    methods: {
      checkLogin() {
        api.pollLogin().then(() => {
          this.$router.replace("/courses") // Redirect logged in user
        })
      },
      submitLogin() {
        api.submitLogin(this.email, this.password).then(() => {
          this.$router.push("/courses")
        }).catch(err => {
          this.errors.push(err);
        })
      }
    }
  }
</script>

<style>
  .loginForm {
    display: flex;
    justify-content: center;
    align-content: center;
  }
  .loginCard {
    width: 30rem;
    display: flex;
    flex-direction: column;
  }

  .loginCard > * {
    justify-content: center;
  }
</style>