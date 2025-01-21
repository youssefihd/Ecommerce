<template>
  <div class="update-customer p-4">
    <h2 class="text-2xl font-bold">{{ customer.id ? 'Update Customer' : 'Add Customer' }}</h2>
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
  </div>
</template>

<script>
import { ref, onMounted, inject } from 'vue';
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

        if (customer.value.id) {
          // Update existing customer
          await apiClient.put(`/customers/${customer.value.id}`, customer.value);
        } else {
          // Add new customer
          await apiClient.post(`/customers`, customer.value);
        }
        
        // Redirect after success
        router.push('/customers');
      } catch (error) {
        console.error('Error saving customer:', error);
      }
    };

    onMounted(() => {
      const routeCustomer = router.currentRoute.value.params.customer;
      if (routeCustomer) {
        customer.value = { ...routeCustomer }; // Populate form with existing customer data
      } else {
        console.error('No customer data found in route parameters');
      }
    });

    return { customer, submit };
  },
};
</script>

<style scoped>
/* Add any styles you need here */
</style>
