<template>
  <div class="customer-page p-4 bg-gray-100">
    <div class="add-customer p-4">
      <router-link to="/" class="btn mb-4 flex items-center">
        <i class="pi pi-arrow-left mr-2"></i>
        Back to Dashboard
      </router-link>
      <h2 class="text-2xl font-bold">Add Customer</h2>
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
        <div class="mb-4">
          <label for="number" class="block mb-1">Phone Number:</label>
          <InputText v-model="customer.number" id="number" required class="w-full" />
        </div>
        <div class="mb-4">
          <label for="adresse" class="block mb-1">Delivery Address:</label>
          <InputText v-model="customer.adresse" id="adresse" required class="w-full" />
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
  name: 'AddCustomer',
  components: {
    InputText,
    Button,
  },
  setup() {
    const customer = ref({ name: '', lastName: '', email: '', number: '', adresse: '' });
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
        
        // Use POST for creating a new customer
        await apiClient.post(`/customers`, customer.value);
        
        // Redirect after success
        router.push('/customers');
      } catch (error) {
        message.value = 'Error saving customer: ' + (error.response?.data?.message || error.message);
        console.error('Error saving customer:', error);
      }
    };

    return { customer, submit, message };
  },
};
</script>

<style scoped>
  .btn {
  @apply bg-blue-600 text-white font-bold py-2 px-4 rounded; /* Tailwind CSS styles */
}
/* Add any styles you need here */
</style>
