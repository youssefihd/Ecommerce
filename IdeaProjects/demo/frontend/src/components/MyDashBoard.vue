<template>
  <div class="p-6 bg-gray-100 min-h-screen">
    <h1 class="text-3xl font-bold mb-6">My First E-com App.</h1>
    <p class="text-lg mb-6">Welcome, {{ username }}! What are you Shopping Today?</p>

    <nav class="flex justify-between items-center mb-6">
      <div class="flex space-x-6">
        <router-link to="/customers" class="flex items-center text-blue-600 hover:text-blue-800" :class="{ 'font-bold': $route.path === '/customers' }">
          <i class="pi pi-users mr-2"></i>
          Customers
        </router-link>
        <router-link to="/products" class="flex items-center text-blue-600 hover:text-blue-800" :class="{ 'font-bold': $route.path === '/products' }">
          <i class="pi pi-box mr-2"></i>
          Products
        </router-link>
        <router-link to="/orders" class="flex items-center text-blue-600 hover:text-blue-800" :class="{ 'font-bold': $route.path === '/orders' }">
          <i class="pi pi-shopping-cart mr-2"></i>
          Orders
        </router-link>
        <router-link to="/categories" class="flex items-center text-blue-600 hover:text-blue-800" :class="{ 'font-bold': $route.path === '/categories' }">
          <i class="pi pi-tag mr-2"></i>
          Categories
        </router-link>
      </div>
      <Button label="Logout" severity="danger" rounded @click="logout" v-if="isAuthenticated" class="custom-logout-button">
        Logout
        <i class="pi pi-sign-out"></i>
      </Button>
      <router-link to="/see-my-orders" class="btn btn-secondary mb-4 custom-button">
  <i class="fas fa-box"></i> View My Orders
</router-link>

    </nav>

    <div class="mb-6 flex items-center">
      <InputText v-model="searchTerm" placeholder="Search products..." class="mr-2 w-1/3" @input="debouncedSearch" />
      <Dropdown
        v-model="selectedCategory"
        :options="categories"
        optionLabel="name"
        placeholder="Select Category"
        class="mr-2 w-1/3"
        @change="filterProducts"
      />
      <Button @click="clearCategory" class="mr-2">Clear Category</Button>
      <Button @click="searchProducts"><i class="pi pi-search"></i> Search</Button>
    </div>

    <transition name="route" mode="out-in">
      <div key="products" class="mt-8">
        <h2 class="text-2xl font-bold mb-4">Our Products</h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          <div 
            v-for="product in filteredProducts" 
            :key="product.id" 
            class="relative border rounded-lg overflow-hidden shadow-md cursor-pointer group"
          >
            <img :src="product.imgurl" :alt="product.name" class="w-full h-48 object-cover" />
            <div class="p-4">
              <h3 class="font-semibold">{{ product.name }}</h3>
              <p class="text-gray-600">{{ product.description }}</p>
            </div>
            <div class="absolute bottom-4 left-4 right-4 flex justify-between opacity-0 group-hover:opacity-100 transition-opacity">
              <Button 
                icon="pi pi-eye" 
                class="p-button-text" 
                @click.stop="goToProductDetails(product.id)"
                tooltip="View Product"
              />
              <Button 
                icon="pi pi-shopping-cart" 
                class="p-button-text" 
                @click.stop="goToAddOrder(product.id)" 
                tooltip="Add to Cart"
              />
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { ref, onMounted, watch, inject } from 'vue';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Dropdown from 'primevue/dropdown';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { debounce } from 'lodash-es';

export default {
  name: 'MyDashBoard',
  components: {
    Button,
    InputText,
    Dropdown,
  },
  setup() {
    const products = ref([]);
    const filteredProducts = ref([]);
    const searchTerm = ref('');
    const selectedCategory = ref(null);
    const categories = ref([]);
    const router = useRouter();
    const username = ref('User');
    const isAuthenticated = ref(false);
    const keycloak = inject('$keycloak'); // Inject the Keycloak instance

    const logout = () => {
      keycloak.logout({
        redirectUri: window.location.origin,
      });
    };

    onMounted(async () => {
      if (keycloak && keycloak.idTokenParsed) {
        username.value = keycloak.idTokenParsed.preferred_username || 'User';
        isAuthenticated.value = true; // Set to true when authenticated
      }
      await fetchProducts(); // Fetch products on mount
    });

    const fetchProducts = async () => {
      try {
        const response = await axios.get('http://localhost:5051/api/products');
        products.value = response.data;
        filteredProducts.value = response.data;
        extractCategories(response.data);
      } catch (error) {
        console.error('Error fetching products:', error.response ? error.response.data : error.message);
      }
    };

    const extractCategories = (products) => {
      const uniqueCategories = new Set(products.map(product => product.category));
      categories.value = Array.from(uniqueCategories).map(category => ({ name: category }));
    };

    const goToAddOrder = (productId) => {
      router.push({ name: 'AddOrder', params: { productId } });
    };

    const goToProductDetails = (productId) => {
      router.push({ name: 'ProductDetails', params: { productId } });
    };

    const searchProducts = () => {
      filterProducts();
    };

    const filterProducts = () => {
      filteredProducts.value = products.value.filter(product => {
        const matchesSearch = product.name.toLowerCase().includes(searchTerm.value.toLowerCase());
        const matchesCategory = selectedCategory.value ? selectedCategory.value.name === product.category : true;
        return matchesSearch && matchesCategory;
      });
    };

    const clearCategory = () => {
      selectedCategory.value = null; // Clear the selected category
      filterProducts(); // Reset the filter to show all products
    };

    const debouncedSearch = debounce(() => {
      searchProducts();
    }, 300);

    watch(selectedCategory, () => {
      filterProducts();
    });

    return {
      username,
      products,
      filteredProducts,
      searchTerm,
      selectedCategory,
      categories,
      goToAddOrder,
      goToProductDetails,
      searchProducts,
      clearCategory,
      debouncedSearch,
      logout,
      isAuthenticated,
    };
  },
};
</script>

<style scoped>
.custom-logout-button {
  background-color: #dc2626;
  color: white;
  border-radius: 0.375rem;
  padding: 0.5rem 1rem;
  font-weight: bold;
}

.custom-logout-button:hover {
  background-color: #b91c1c;
}

/* Transition styles */
.route-enter-from {
  opacity: 0;
  transform: translateX(100px);
}
.route-enter-active {
  transition: all 0.5s ease-out;
}
.route-leave-to {
  opacity: 0;
  transform: translateX(-100px);
}
.route-leave-active {
  transition: all 0.6s ease-in;
}
.custom-button {
  display: inline-flex;
  align-items: center;
  padding: 10px 15px;
  background-color: #6c757d; /* Bootstrap secondary color */
  color: white;
  text-decoration: none;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.custom-button:hover {
  background-color: #5a6268; /* Darker shade for hover effect */
}

.custom-button i {
  margin-right: 8px; /* Space between icon and text */
}

</style>
