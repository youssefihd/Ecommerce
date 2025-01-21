<template>
  <div class="customer-page p-4 bg-gray-100">
    <router-link to="/" class="btn mb-4 flex items-center">
      <i class="pi pi-arrow-left mr-2"></i> <!-- PrimeIcons left arrow -->
      Back to Dashboard
    </router-link>
    
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-2xl font-bold text-blue-600">👥 Customers List</h2>
      <router-link to="/add-customer" class="btn">
        Add Customer
      </router-link>
    </div>
    
    <transition name="fade" mode="out-in">
      <DataTable
        v-if="customers.length"
        :value="customers"
        responsiveLayout="scroll"
        class="mt-4 shadow-md"
        tableStyle="min-width: 50rem"
      >
        <Column field="id" header="ID" />
        <Column field="name" header="First Name" />
        <Column field="lastName" header="Last Name" />
        <Column field="email" header="Email" />
        <Column header="Actions">
          <template #body="slotProps">
            <Button 
              label="Update" 
              icon="pi pi-pencil" 
              class="mr-2 bg-sky-500 text-white"
              @click="updateCustomer(slotProps.data)" 
            />
            <Button 
              label="Delete" 
              icon="pi pi-trash" 
              class="bg-pink-500 text-white"
              @click="deleteCustomer(slotProps.data.id)" 
            />
          </template>
        </Column>
      </DataTable>
      <div v-else class="mt-4">
        <p>No customers found.</p>
      </div>
    </transition>
  </div>
</template>

<script>
import { ref, onMounted, inject } from 'vue';
import { useRouter } from 'vue-router';
import createApiClient from '@/apiClient';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';

export default {
  name: 'CustomersPage',
  components: {
    DataTable,
    Column,
    Button,
  },
  setup() {
    const customers = ref([]);
    const keycloak = inject('$keycloak');
    const router = useRouter();

    onMounted(async () => {
      await fetchCustomers();
    });

    const fetchCustomers = async () => {
      try {
        if (keycloak.isTokenExpired()) {
          await keycloak.updateToken(30);
        }
        const token = keycloak.token;
        const apiClient = createApiClient(token);
        const response = await apiClient.get('/customers');

        if (response.status === 200 && Array.isArray(response.data)) {
          customers.value = response.data;
        } else {
          console.warn('Expected an array, but got:', response.data);
        }
      } catch (error) {
        console.error('Error fetching customers:', error.response ? error.response.data : error.message);
      }
    };

    const deleteCustomer = async (id) => {
      try {
        if (keycloak.isTokenExpired()) {
          await keycloak.updateToken(30);
        }
        const token = keycloak.token;
        const apiClient = createApiClient(token);
        await apiClient.delete(`/customers/${id}`);
        customers.value = customers.value.filter(customer => customer.id !== id);
        console.log(`Customer with ID ${id} deleted.`);
      } catch (error) {
        console.error('Error deleting customer:', error.response ? error.response.data : error.message);
      }
    };

    const updateCustomer = (customer) => {
      router.push({ name: 'UpdateCustomer', params: { customer } });
    };

    return { customers, deleteCustomer, updateCustomer };
  },
};
</script>

<style scoped>
.customer-page {
  /* Styles for the customers page */
}

.btn {
  @apply bg-blue-600 text-white font-bold py-2 px-4 rounded; /* Tailwind CSS styles */
}

.bg-sky-500 {
  background-color: #0ea5e9; /* Tailwind CSS for sky blue */
}

.bg-pink-500 {
  background-color: #ec4899; /* Tailwind CSS for pink */
}

/* Transition styles */
.fade-enter, .fade-leave-active {
  opacity: 0;
  position: absolute; /* Ensure it doesn't affect layout */
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease;
}
</style>
