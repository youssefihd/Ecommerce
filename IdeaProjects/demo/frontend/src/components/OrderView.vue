<template>
  <div class="order-page p-4 bg-gray-100">
    <router-link to="/" class="btn mb-4 flex items-center">
      <i class="pi pi-arrow-left mr-2"></i> <!-- PrimeIcons left arrow -->
      Back to Dashboard
    </router-link>
    <div class="flex justify-between items-center mb-4">
      <h1 class="text-2xl font-bold">📦 Orders List</h1>
      <router-link to="/add-order" class="btn btn-primary">
        Add Order
      </router-link>
    </div>

    <!-- Button to redirect to SeeMyOrders -->
    <router-link to="/see-my-orders" class="btn btn-secondary mb-4">
      View My Orders
    </router-link>

    <!-- Display Orders -->
    <div v-if="orders.length">
      <DataTable :value="orders" tableStyle="min-width: 50rem" class="shadow-md" v-model:selection="selectedOrders">
        <Column field="id" header="Order ID" />
        <Column field="customerName" header="Customer Name" />
        <Column field="customerEmail" header="Customer Email" />
        <Column field="productName" header="Product" />
        <Column field="orderDate" header="Order Date" />
        <Column field="qnt" header="Quantity" />
        <Column field="status" header="Status" /> <!-- Added status column -->
        <Column header="Actions">
          <template #body="slotProps">
            <div class="flex space-x-2">
              <Button 
                label="Update" 
                icon="pi pi-pencil" 
                style="background-color: #3B82F6; color: white;"
                @click="updateOrder(slotProps.data)" 
              />       
              <Button 
                label="Delete" 
                icon="pi pi-trash" 
                style="background-color: #EF4444; color: white;"
                @click="deleteOrder(slotProps.data.id)" 
              />
            </div>
          </template>
        </Column>
      </DataTable>
    </div>

    <!-- No Orders -->
    <div v-else>
      <p>No orders available.</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, inject } from 'vue';
import { useRouter } from 'vue-router';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import createApiClient from '@/apiClient';

export default {
  name: 'OrdersPage',
  components: {
    DataTable,
    Column,
    Button,
  },
  setup() {
    const orders = ref([]);
    const selectedOrders = ref([]);
    const keycloak = inject('$keycloak');
    const router = useRouter();

    onMounted(async () => {
      await fetchOrders();
    });

    const fetchOrders = async () => {
      try {
        if (keycloak.isTokenExpired()) {
          await keycloak.updateToken(30);
        }
        const token = keycloak.token;

        const apiClient = createApiClient(token);
        const response = await apiClient.get('/orders');

        if (response.status === 200 && Array.isArray(response.data)) {
          orders.value = response.data.map(order => ({
            id: order.id || 'N/A',
            customerName: order.customer ? `${order.customer.name} ${order.customer.lastName}` : 'Unnamed Customer',
            customerEmail: order.customer ? order.customer.email : 'No Email',
            productName: order.products.length ? order.products.map(p => p.name).join(', ') : 'No Product',
            orderDate: order.orderDate || 'No Date',
            qnt: order.qnt || 0,
            status: order.status || 'Unknown', // Add status field here
          }));
        } else {
          console.warn('Expected an array, but got:', response.data);
        }
      } catch (error) {
        console.error('Error fetching orders:', error.response ? error.response.data : error.message);
      }
    };

    const deleteOrder = async (id) => {
      if (confirm('Are you sure you want to delete this order?')) {
        try {
          if (keycloak.isTokenExpired()) {
            await keycloak.updateToken(30);
          }
          const token = keycloak.token;
          const apiClient = createApiClient(token);

          await apiClient.delete(`/orders/${id}`);
          orders.value = orders.value.filter(order => order.id !== id);
        } catch (error) {
          console.error('Error deleting order:', error.response ? error.response.data : error.message);
        }
      }
    };

    const updateOrder = (order) => {
      router.push({ name: 'UpdateOrder', params: { orderId: order.id } });
    };

    const deleteSelectedOrders = async () => {
      for (const order of selectedOrders.value) {
        await deleteOrder(order.id);
      }
      selectedOrders.value = []; // Clear selection after deletion
    };

    return { orders, selectedOrders, deleteOrder, updateOrder, deleteSelectedOrders };
  },
};
</script>

<style scoped>
.btn {
  @apply bg-blue-600 text-white font-bold py-2 px-4 rounded; /* Tailwind CSS styles */
}
.btn-secondary {
  @apply bg-green-600; /* Tailwind CSS styles for the secondary button */
}
</style>
