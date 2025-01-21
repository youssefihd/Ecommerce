<template>
  <div v-if="isLoading" class="p-6 bg-gray-100 min-h-screen">
    <p>Loading product details...</p>
  </div>
  <div v-else-if="product.name" class="p-6 bg-gray-100 min-h-screen">
    <h2 class="text-3xl font-bold mb-4">{{ product.name }}</h2>
    <img :src="product.imgurl" :alt="product.name" class="w-full h-64 object-cover mb-4" />
    <p class="text-lg mb-4">{{ product.description }}</p>
    <p class="text-gray-600 mb-4">Category: {{ product.category }}</p>
    <p class="text-gray-600 mb-4">Price: {{ product.price }}</p>
    <p class="text-gray-600 mb-4">Currency: {{ product.currency }}</p>
    <div class="mb-4">
      <label class="block mb-2">Rate this product:</label>
      <Rating v-model="rating" />
    </div>
    <Button 
      label="Order Now" 
      icon="pi pi-shopping-cart" 
      @click="goToAddOrder" 
      class="bg-blue-600 text-white font-bold mb-4 flex items-center"
    />
    <router-link to="/" class="btn mb-4">
      <i class="pi pi-arrow-left mr-2"></i> Back to Dashboard
    </router-link>
  </div>
  <div v-else class="p-6 bg-gray-100 min-h-screen">
    <p>Product not found.</p>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import Rating from 'primevue/rating';
import Button from 'primevue/button';

export default {
  name: 'ProductDetails',
  components: {
    Rating,
    Button,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const productId = route.params.productId;
    const product = ref({});
    const rating = ref(0);
    const isLoading = ref(true); // Loading state

    onMounted(async () => {
      await fetchProduct();
    });

    const fetchProduct = async () => {
      isLoading.value = true; // Start loading
      try {
        const response = await axios.get(`http://localhost:5051/api/products/${productId}`);
        
        if (response && response.data) {
          product.value = response.data;
        } else {
          console.error('No product data received');
        }
      } catch (error) {
        console.error('Error fetching product:', error);
      } finally {
        isLoading.value = false; // End loading
      }
    };

    const goToAddOrder = () => {
      router.push({ name: 'AddOrder', params: { productId, rating: rating.value } });
    };

    return {
      product,
      rating,
      isLoading,
      goToAddOrder,
    };
  },
};
</script>

<style scoped>
.btn {
  @apply bg-gray-200 text-blue-600 font-bold py-2 px-4 rounded flex items-center; /* Tailwind CSS styles */
}

.btn:hover {
  @apply bg-gray-300; /* Hover effect */
}
</style>
