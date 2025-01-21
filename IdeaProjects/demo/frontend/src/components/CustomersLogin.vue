<template>
    <div class="customer-login p-4 bg-gray-100">
      <h2 class="text-2xl font-bold">Customer Login</h2>
      <form @submit.prevent="login" class="mt-4">
        <div class="mb-4">
          <label for="username" class="block mb-1">Username:</label>
          <InputText v-model="username" id="username" placeholder="Enter your username" required class="w-full" />
        </div>
        <div class="mb-4">
          <label for="password" class="block mb-1">Password:</label>
          <InputText type="password" v-model="password" id="password" placeholder="Enter your password" required class="w-full" />
        </div>
        <Button label="Login" type="submit" class="bg-sky-500 text-white" />
        <p v-if="errorMessage" class="text-red-500 mt-2">{{ errorMessage }}</p>
      </form>
    </div>
  </template>
  
  <script>
  import { ref } from 'vue';
  import axios from 'axios';
  
  export default {
    name: 'CustomersLogin',
    setup() {
      const username = ref('');
      const password = ref('');
      const errorMessage = ref('');
  
      const login = async () => {
        try {
          const response = await axios.post('http://localhost:8080/auth/realms/YOUR_REALM/protocol/openid-connect/token', {
            client_id: 'YOUR_CLIENT_ID',
            username: username.value,
            password: password.value,
            grant_type: 'password',
          });
  
          const { access_token } = response.data;
          localStorage.setItem('access_token', access_token);
          // Redirect to customers page after successful login
          this.$router.push('/customers');
        } catch (error) {
          errorMessage.value = 'Invalid username or password.';
          console.error('Login error:', error.response ? error.response.data : error.message);
        }
      };
  
      return { username, password, login, errorMessage };
    },
  };
  </script>
  
  <style scoped>
  /* Add your styles here */
  </style>
  