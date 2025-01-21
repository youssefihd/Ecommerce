<template>
  <div class="product-page p-4 bg-gray-100">
    <div class="add-product p-4">
      <router-link to="/" class="btn mb-4 flex items-center">
        <i class="pi pi-arrow-left mr-2"></i> Back to Dashboard
      </router-link>
      <h2 class="text-2xl font-bold">Add Product</h2>
      <form @submit.prevent="submitProduct">
        <div class="mb-4">
          <label for="productName" class="block mb-1">Product Name:</label>
          <InputText v-model="product.name" id="productName" placeholder="Enter product name" required class="w-full" />
        </div>
        <div class="mb-4">
          <label for="productPrice" class="block mb-1">Price:</label>
          <InputNumber
            id="productPrice"
            v-model="product.price"
            placeholder="Enter product price"
            required
            mode="decimal"
            class="w-full"
          />
        </div>
        <div class="mb-4">
          <label for="currency" class="block mb-1">Currency:</label>
          <InputText
            v-model="product.currency"
            id="currency"
            placeholder="Enter currency (e.g: MAD, USD, EUR)"
            required
            class="w-full"
          />
        </div>
        <div class="mb-4">
          <label for="imgUrl" class="block mb-1">Image URL:</label>
          <InputText v-model="product.imgurl" id="imgUrl" placeholder="Enter image URL" required class="w-full" />
        </div>
        <div class="mb-4">
          <label for="fileUpload" class="block mb-1">Or Upload Image:</label>
          <FileUpload 
            name="demo[]" 
            url="/api/upload" 
            @upload="onAdvancedUpload" 
            :multiple="true" 
            accept="image/*" 
            :maxFileSize="1000000"
          >
            <template #empty>
              <span>Drag and drop files here to upload, or click to select files.</span>
            </template>
          </FileUpload>
        </div>
        <div class="mb-4">
          <label for="inStock" class="block mb-1">In Stock:</label>
          <InputNumber
            id="inStock"
            v-model="product.inStock"
            placeholder="Enter stock quantity"
            required
            mode="decimal"
            class="w-full"
            :min="0"
          />
        </div>
        <div class="mb-4">
          <label for="productDescription" class="block mb-1">Description:</label>
          <InputText
            v-model="product.description"
            id="productDescription"
            placeholder="Enter product description"
            required
            class="w-full"
          />
        </div>
        <div class="mb-4">
          <label for="productCategory" class="block mb-1">Category:</label>
          <InputText
            v-model="product.category"
            id="productCategory"
            placeholder="Enter product category"
            required
            class="w-full"
          />
        </div>
        <div class="flex justify-between">
          <Button label="Submit" type="submit" class="bg-sky-500 text-white" />
          <router-link to="/products">
            <Button label="Cancel" class="ml-2 p-button-danger" />
          </router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, inject } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import InputText from 'primevue/inputtext';
import InputNumber from 'primevue/inputnumber';
import Button from 'primevue/button';
import FileUpload from 'primevue/fileupload';

export default {
  name: 'AddProduct',
  components: {
    InputText,
    InputNumber,
    Button,
    FileUpload,
  },
  setup() {
    const product = ref({ name: '', price: null, imgurl: '', inStock: 0, description: '', category: '', currency: '' });
    const router = useRouter();
    const keycloak = inject('$keycloak');

    const submitProduct = async () => {
      try {
        if (!keycloak) {
          throw new Error('Keycloak instance not found');
        }

        await keycloak.updateToken(30);
        const token = keycloak.token;

        await axios.post('http://localhost:5051/api/products', {
          name: product.value.name,
          price: product.value.price,
          imgurl: product.value.imgurl,
          inStock: product.value.inStock,
          description: product.value.description,
          category: product.value.category,
          currency: product.value.currency,
        }, {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        });

        console.log('Product added:', product.value);
        router.push('/products'); // Redirect to products page after successful addition
      } catch (error) {
        console.error('Error adding product:', error);
      }
    };

    const onAdvancedUpload = (event) => {
      console.log('Files uploaded:', event.files);
      // Here you could add logic to handle uploaded files
    };

    return {
      product,
      submitProduct,
      onAdvancedUpload,
    };
  },
};
</script>

<style scoped>
.btn {
  @apply bg-blue-600 text-white font-bold py-2 px-4 rounded; /* Tailwind CSS styles */
}
</style>
