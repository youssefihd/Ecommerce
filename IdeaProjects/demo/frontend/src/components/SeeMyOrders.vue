<template>
  <div class="see-my-orders p-4 bg-gray-100">
    <router-link to="/" class="btn mb-4 flex items-center">
      <i class="pi pi-arrow-left mr-2"></i> Back to Dashboard
    </router-link>
    <h2 class="text-2xl font-bold mb-4">My Orders</h2>
    <DataTable :value="orders" tableStyle="min-width: 50rem" class="shadow-md">
      <Column field="id" header="Order ID" />
      <Column field="orderDate" header="Order Date" />
      <Column field="qnt" header="Quantity" />
      <Column header="Status">
        <template #body="slotProps">
          <span :class="statusClass(slotProps.data.status) + ' p-1 rounded'" >
            {{ slotProps.data.status }}
          </span>
        </template>
      </Column>
      <Column v-if="isAdmin" header="Actions">
        <template #body="slotProps">
          <div class="flex space-x-2">
            <div class="flex items-center">
              <Checkbox v-model="selectedStatuses[slotProps.data.id]" inputId="valid-{{slotProps.data.id}}" name="status" value="VALID" />
              <label :for="'valid-' + slotProps.data.id" class="ml-2">Valid</label>
            </div>
            <div class="flex items-center">
              <Checkbox v-model="selectedStatuses[slotProps.data.id]" inputId="ongoing-{{slotProps.data.id}}" name="status" value="ONGOING" />
              <label :for="'ongoing-' + slotProps.data.id" class="ml-2">Ongoing</label>
            </div>
            <div class="flex items-center">
              <Checkbox v-model="selectedStatuses[slotProps.data.id]" inputId="reimbursed-{{slotProps.data.id}}" name="status" value="REIMBURSED" />
              <label :for="'reimbursed-' + slotProps.data.id" class="ml-2">Reimbursed</label>
            </div>
            <Button label="Submit" @click="updateOrderStatus(slotProps.data.id)" v-if="selectedStatuses[slotProps.data.id]?.length" />
          </div>
        </template>
      </Column>
      <Column v-if="isAdmin" header="Actions">
        <template #body="slotProps">
          <Button 
            label="Delete" 
            icon="pi pi-trash" 
            style="background-color: #EF4444; color: white;"
            @click="deleteOrder(slotProps.data.id)"
          />
        </template>
      </Column>
    </DataTable>

    <div v-if="!orders.length" class="mt-4">
      <p>No orders found.</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, inject } from 'vue';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import Checkbox from 'primevue/checkbox';
import createApiClient from '@/apiClient';

export default {
  name: 'SeeMyOrders',
  components: {
    DataTable,
    Column,
    Button,
    Checkbox,
  },
  setup() {
    const orders = ref([]);
    const keycloak = inject('$keycloak');
    const isAdmin = ref(false);
    const selectedStatuses = ref({});

    onMounted(async () => {
      await fetchMyOrders();
      checkUserRole();
    });

    const checkUserRole = () => {
      isAdmin.value = keycloak.hasRealmRole('ADMIN');
    };

    const fetchMyOrders = async () => {
      try {
        const token = keycloak.token;
        const apiClient = createApiClient(token);
        const response = await apiClient.get('/orders');

        if (response.status === 200 && Array.isArray(response.data)) {
          orders.value = response.data.map(order => ({
            id: order.id || 'N/A',
            orderDate: order.orderDate || 'No Date',
            qnt: order.qnt || 0,
            status: order.status || 'Unknown',
          }));
        }
      } catch (error) {
        console.error('Error fetching orders:', error.response ? error.response.data : error.message);
      }
    };

    const updateOrderStatus = async (orderId) => {
      const statusesToUpdate = selectedStatuses.value[orderId];
      if (!statusesToUpdate || statusesToUpdate.length === 0) return;

      const statusToUpdate = statusesToUpdate[0]; // Use the first selected status

      try {
        const token = keycloak.token;
        const apiClient = createApiClient(token);
        
        const response = await apiClient.put(`/orders/${orderId}/status`, { status: statusToUpdate });
        
        if (response.status === 200) {
          const orderToUpdate = orders.value.find(order => order.id === orderId);
          if (orderToUpdate) {
            orderToUpdate.status = statusToUpdate; // Update status locally
          }
        }
      } catch (error) {
        console.error('Error updating order status:', error.response ? error.response.data : error.message);
      }
    };

    const deleteOrder = async (id) => {
      if (confirm('Are you sure you want to delete this order?')) {
        try {
          const token = keycloak.token;
          const apiClient = createApiClient(token);
          await apiClient.delete(`/orders/${id}`);
          orders.value = orders.value.filter(order => order.id !== id);
        } catch (error) {
          console.error('Error deleting order:', error.response ? error.response.data : error.message);
        }
      }
    };

    const statusClass = (status) => {
      switch (status) {
        case 'VALID':
          return 'bg-green-500'; // Green for Valid
        case 'ONGOING':
          return 'bg-yellow-500'; // Yellow for Ongoing
        case 'REIMBURSED':
          return 'bg-blue-500'; // Blue for Reimbursed
        default:
          return 'bg-gray-500'; // Default color for unknown status
      }
    };

    return { orders, isAdmin, deleteOrder, updateOrderStatus, selectedStatuses, statusClass };
  },
};
</script>

<style scoped>
.see-my-orders {
  /* Add any custom styles here */
}
.btn {
  @apply bg-blue-600 text-white font-bold py-2 px-4 rounded; /* Tailwind CSS styles */
}
</style>
