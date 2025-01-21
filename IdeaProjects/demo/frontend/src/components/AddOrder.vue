<template>
  <div>
    <h2>Add Order</h2>
    <router-link to="/" class="btn mb-4">Back to Dashboard</router-link>
    <form @submit.prevent="submitOrder">
      <div>
        <label for="customerEmail">Customer Email:</label>
        <InputText 
          v-model="customerEmail" 
          id="customerEmail" 
          placeholder="Enter customer email" 
          required 
          @blur="fetchCustomerByEmail" 
        />
      </div>
      <div v-if="customer">
        <p>Customer Address: {{ customer.adresse }} | Name: {{ customer.name }} {{ customer.lastName }} {{ customer.number }}</p>
      </div>
      <div>
        <label for="orderDate">Order Date:</label>
        <InputText 
          type="date" 
          v-model="orderDate" 
          id="orderDate" 
          required 
        />
      </div>
      <div>
        <label for="productIds">Select Products:</label>
        <MultiSelect
          v-model="selectedProductIds"
          :options="products"
          optionLabel="name"
          optionValue="id"
          placeholder="Select products"
          @change="updateSelectedProducts" 
          required
        />
      </div>
      <div>
        <label>Selected Products:</label>
        <ul>
          <li v-for="product in selectedProducts" :key="product.id">
            <img :src="product.imgurl" alt="Product image" class="product-image" />
            {{ product.name }} - Price: {{ product.price }} {{ product.currency }} 
            <Rating v-model="product.rating" />
            Quantity:
            <InputNumber 
              v-model="product.quantity" 
              min="1" 
              showButtons 
              buttonLayout="horizontal" 
              style="width: 4rem"
            />
          </li>
        </ul>
      </div>
      <div>
        <p>Total Price: {{ totalPrice }} {{ selectedProducts[0]?.currency || '€' }}</p>
      </div>
      <div v-if="message" class="mt-4 text-red-600">{{ message }}</div>
      <Button label="Submit" type="submit" />
    </form>
  </div>
</template>

<script>
import { ref, onMounted, inject, computed } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import MultiSelect from 'primevue/multiselect';
import InputNumber from 'primevue/inputnumber';
import Rating from 'primevue/rating';

export default {
  name: 'AddOrder',
  components: {
    InputText,
    Button,
    MultiSelect,
    InputNumber,
    Rating,
  },
  setup() {
    const route = useRoute();
    const productId = route.params.productId;
    const customerEmail = ref('');
    const customer = ref(null);
    const selectedProductIds = ref([]);
    const selectedProducts = ref([]);
    const products = ref([]);
    const message = ref('');
    const orderDate = ref('');
    const keycloak = inject('$keycloak');

    onMounted(async () => {
      await fetchProducts();
      await fetchSelectedProduct();
    });

    const fetchProducts = async () => {
      try {
        await keycloak.updateToken(30);
        const token = keycloak.token;
        const response = await axios.get('http://localhost:5051/api/products', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        products.value = response.data;
      } catch (error) {
        message.value = 'Error fetching products: ' + (error.response?.data?.message || error.message);
        console.error('Error fetching products:', error);
      }
    };

    const fetchSelectedProduct = async () => {
      if (productId) {
        try {
          await keycloak.updateToken(30);
          const token = keycloak.token;
          const response = await axios.get(`http://localhost:5051/api/products/${productId}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          if (response.data) {
            response.data.quantity = 1;
            response.data.rating = response.data.rating || 0;
            selectedProducts.value.push(response.data);
            selectedProductIds.value.push(response.data.id);
          }
        } catch (error) {
          message.value = 'Error fetching product: ' + (error.response?.data?.message || error.message);
          console.error('Error fetching product:', error);
        }
      }
    };

    const fetchCustomerByEmail = async () => {
      if (!customerEmail.value) return;

      try {
        await keycloak.updateToken(30);
        const token = keycloak.token;

        const response = await axios.get(`http://localhost:5051/api/customers/email/${customerEmail.value}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.data) {
          customer.value = response.data;
        } else {
          message.value = 'Customer not found';
        }
      } catch (error) {
        message.value = 'Error fetching customer: ' + (error.response?.data?.message || error.message);
        console.error('Error fetching customer:', error);
      }
    };

    const updateSelectedProducts = () => {
      selectedProducts.value = products.value.filter(product => selectedProductIds.value.includes(product.id));
    };

    const totalPrice = computed(() => {
      return selectedProducts.value.reduce((total, product) => {
        return total + (product.price * (product.quantity || 1));
      }, 0).toFixed(2);
    });

    const submitOrder = async () => {
      message.value = '';

      if (!customer.value) {
        message.value = 'Please select a valid customer.';
        return;
      }

      const orderData = {
        customer: { id: customer.value.id },
        orderDate: orderDate.value,
        products: selectedProducts.value.map(product => ({
          id: product.id,
          quantity: product.quantity,
          rating: product.rating,
        })),
        qnt: selectedProducts.value.reduce((total, product) => total + (product.quantity || 0), 0),
      };

      try {
        await keycloak.updateToken(30);
        const token = keycloak.token;

        const response = await axios.post('http://localhost:5051/api/orders', orderData, {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });

        console.log('Order added:', response.data);
        resetForm();
      } catch (error) {
        message.value = 'Error adding order: ' + (error.response?.data?.message || error.message);
        console.error('Error adding order:', error);
      }
    };

    const resetForm = () => {
      customerEmail.value = '';
      customer.value = null;
      selectedProductIds.value = [];
      selectedProducts.value = [];
      orderDate.value = '';
    };

    return {
      customerEmail,
      customer,
      selectedProductIds,
      selectedProducts,
      products,
      submitOrder,
      fetchCustomerByEmail,
      message,
      totalPrice,
      updateSelectedProducts,
      orderDate,
    };
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
  width: 300px;
  height: auto;
}

.text-red-600 {
  color: red;
}

.btn {
  @apply bg-blue-600 text-white font-bold py-2 px-4 rounded; /* Tailwind CSS styles */
}
</style>
