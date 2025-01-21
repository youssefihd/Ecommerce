<template>
    <div class="customer-page p-4 bg-gray-100">
      <div class="update-customer p-4">
        <router-link to="/" class="btn mb-4 flex items-center">
          <i class="pi pi-arrow-left mr-2"></i> <!-- PrimeIcons left arrow -->
          Back to Dashboard
        </router-link>
        <h2 class="text-2xl font-bold">Update Customer</h2>
        <form @submit.prevent="submit">
          <div class="mb-4">
            <label for="name" class="block mb-1">First Name:</label>
            <InputText v-model="customer.name" id="name" required class="w-full" />
          </div>
          <div class="mb-4">
            <label for="lastName" class="block mb-1">Last Name:</label>
            <InputText v-model="customer.lastName" id="lastName" required class="w-full" />
          </div>
          <div class="mb-4">
            <label for="email" class="block mb-1">Email:</label>
            <InputText v-model="customer.email" id="email" required type="email" class="w-full" />
          </div>
          <Button type="submit" label="Save Customer" class="bg-sky-500 text-white" />
          <router-link to="/customers">
            <Button label="Cancel" class="ml-2 p-button-danger" />
          </router-link>
        </form>
        <div v-if="message" class="mt-4 text-red-600">{{ message }}</div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, inject } from 'vue';
  import { useRouter } from 'vue-router';
  import createApiClient from '@/apiClient';
  import InputText from 'primevue/inputtext';
  import Button from 'primevue/button';
  
  export default {
    name: 'UpdateCustomer',
    components: {
      InputText,
      Button,
    },
    setup() {
      const customer = ref({ name: '', lastName: '', email: '' });
      const message = ref('');
      const router = useRouter();
      const keycloak = inject('$keycloak');
  
      const submit = async () => {
        if (!keycloak) {
          console.error('Keycloak is not defined');
          return;
        }
  
        try {
          const token = keycloak.token;
          const apiClient = createApiClient(token);
          
          // Use POST for creating or updating a customer
          await apiClient.post(`/customers`, customer.value);
          
          // Redirect after success
          router.push('/customers');
        } catch (error) {
          if (error.response) {
            if (error.response.status === 409) {
              message.value = error.response.data; // Show the server's error message
            } else {
              message.value = 'An unexpected error occurred: ' + error.message;
            }
          } else {
            message.value = 'Network error. Please check your connection.';
          }
          console.error('Error saving customer:', error);
        }
      };
  
      return { customer, submit, message };
    },
  };
  </script>
  
  <style scoped>
  form div {
    margin-bottom: 1em;
  }
  
  label {
    display: block;
    margin-bottom: 0.5em;
  }
  
  .product-image {
    width: 50px;
    height: 50px;
    margin-right: 8px;
  }
  
  .text-red-600 {
    color: red;
  }
  </style>
  