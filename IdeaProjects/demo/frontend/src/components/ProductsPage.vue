<template>
  <div class="p-4 bg-gray-100">
    <router-link to="/" class="btn mb-4 flex items-center">
      <i class="pi pi-arrow-left mr-2"></i>
      Back to Dashboard
    </router-link>
    <div class="flex justify-between items-center mb-4">
      <h1 class="text-2xl font-bold">Products</h1>
      <router-link to="/add-product" class="btn btn-primary">
        Add Product
      </router-link>
    </div>

    <transition name="fade">
      <div v-if="products.length">
        <DataView :value="products" layout="list">
          <template #list="slotProps">
            <div class="flex flex-col">
              <div v-for="(item, index) in slotProps.items" :key="index">
                <div class="flex flex-col sm:flex-row sm:items-center p-6 gap-4" :class="{ 'border-t border-surface-200 dark:border-surface-700': index !== 0 }">
                  <div class="md:w-40 relative">
                    <img class="block xl:block mx-auto rounded w-full" :src="item.imgurl" :alt="item.name" />
                    <div class="absolute bg-black/70 rounded-border" style="left: 4px; top: 4px">
                      <Tag :value="getSeverity(item.inventoryStatus)" :severity="getSeverity(item)" />
                    </div>
                  </div>
                  <div class="flex flex-col md:flex-row justify-between md:items-center flex-1 gap-6">
                    <div class="flex flex-row md:flex-col justify-between items-start gap-2">
                      <div>
                        <span class="font-medium text-surface-500 dark:text-surface-400 text-sm">{{ item.category }}</span>
                        <div class="text-lg font-medium mt-2">{{ item.name }}</div>
                      </div>
                      <div class="bg-surface-100 p-1" style="border-radius: 30px">
                        <div class="bg-surface-0 flex items-center gap-2 justify-center py-1 px-2" style="border-radius: 30px; box-shadow: 0px 1px 2px 0px rgba(0, 0, 0, 0.04), 0px 1px 2px 0px rgba(0, 0, 0, 0.06)">
                          <span class="text-surface-900 font-medium text-sm">{{ item.rating }}</span>
                          <i class="pi pi-star-fill text-yellow-500"></i>
                        </div>
                      </div>
                    </div>
                    <div class="flex flex-col md:items-end gap-8">
                      <span class="text-xl font-semibold">Price : {{ item.price }} </span>
                      <span class="text-xl font-semibold">Currency : {{ item.currency }} </span>
                      <div class="flex flex-row-reverse md:flex-row gap-2">
                        <Button icon="pi pi-heart" outlined></Button>
                        <Button 
                          icon="pi pi-shopping-cart" 
                          label="Buy Now" 
                          :disabled="item.inventoryStatus === 'OUTOFSTOCK'" 
                          class="flex-auto md:flex-initial whitespace-nowrap"
                        />
                        <Button 
                          label="Delete" 
                          style="background-color: #EF4444; color: white;" 
                          @click="() => deleteProduct(item.id)"
                        >
                          <i class="pi pi-trash"></i> Delete
                        </Button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </DataView>
      </div>
      <div v-else>
        <p>No products available.</p>
      </div>
    </transition>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import DataView from 'primevue/dataview';
import Button from 'primevue/button';
import Tag from 'primevue/tag';
import createApiClient from '@/apiClient';

export default {
  name: 'ProductsPage',
  components: {
    DataView,
    Button,
    Tag,
  },
  setup() {
    const products = ref([]);

    onMounted(async () => {
      await fetchProducts();
    });

    const fetchProducts = async () => {
      try {
        const apiClient = createApiClient(); // No need for token now
        const response = await apiClient.get('/products');

        if (response.status === 200 && Array.isArray(response.data)) {
          products.value = response.data;
        } else {
          console.warn('Expected an array, but got:', response.data);
        }
      } catch (error) {
        console.error('Error fetching products:', error.response ? error.response.data : error.message);
      }
    };

    const deleteProduct = async (id) => {
      try {
        const apiClient = createApiClient(); // No need for token now
        await apiClient.delete(`/products/${id}`);
        products.value = products.value.filter(product => product.id !== id);
      } catch (error) {
        console.error('Error deleting product:', error.response ? error.response.data : error.message);
      }
    };

    const getSeverity = (status) => {
      switch (status) {
        case 'INSTOCK':
          return 'success';
        case 'LOWSTOCK':
          return 'warn';
        case 'OUTOFSTOCK':
          return 'danger';
        default:
          return null;
      }
    };

    return { products, deleteProduct, getSeverity };
  },
};
</script>

<style scoped>
.product-image {
  width: 50px; /* Adjust as needed */
  height: 50px; /* Adjust as needed */
  object-fit: cover; /* Maintain aspect ratio */
}
.btn {
  @apply bg-blue-600 text-white font-bold py-2 px-4 rounded; /* Tailwind CSS styles */
}
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}
</style>
