<template>
    <div class="p-4 bg-gray-100">
        <router-link to="/" class="btn mb-4 flex items-center">
            <i class="pi pi-arrow-left mr-2"></i>
            Back to Dashboard
        </router-link>
        
        <h2>Categories</h2>
        <DataTable :value="categories" :paginator="true" :rows="10" :globalFilter="globalFilter">
            <Column field="id" header="ID" />
            <Column field="name" header="Name" />
            <Column field="brand" header="Brand" />
            <Column field="description" header="Description" />
            <Column field="createdDate" header="Created Date" />
            <Column header="Image">
                <template #body="slotProps">
                    <img :src="slotProps.data.imgurl" alt="Category Image" class="product-image" />
                </template>
            </Column>
            <Column header="Actions">
                <template #body="slotProps">
                    <Button 
                        label="Delete" 
                        style="background-color: #EF4444; color: white;" 
                        @click="deleteCategory(slotProps.data.id)"
                    >
                        <i class="pi pi-trash"></i> Delete
                    </Button>
                </template>
            </Column>
        </DataTable>
        
        <div v-if="message">{{ message }}</div>
        
        <router-link to="/add-category">
            <Button label="Add Category" class="add-category-button" />
        </router-link>
    </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';

export default {
    name: 'CategoryView',
    components: { DataTable, Column, Button },
    setup() {
        const categories = ref([]);
        const message = ref('');
        const globalFilter = ref('');

        const fetchCategories = async () => {
            try {
                const response = await axios.get('http://localhost:5051/api/categories');
                categories.value = response.data;
            } catch (error) {
                console.error('Error fetching categories:', error);
                message.value = 'Error fetching categories.';
            }
        };

        const deleteCategory = async (id) => {
            if (confirm('Are you sure you want to delete this category?')) {
                try {
                    await axios.delete(`http://localhost:5051/api/categories/${id}`);
                    categories.value = categories.value.filter(category => category.id !== id);
                    message.value = 'Category deleted successfully.';
                } catch (error) {
                    console.error('Error deleting category:', error);
                    message.value = 'Error deleting category.';
                }
            }
        };

        onMounted(fetchCategories);

        return {
            categories,
            message,
            globalFilter,
            deleteCategory,
        };
    },
};
</script>

<style scoped>
.product-image {
    width: 50px; /* Adjust as needed */
    height: auto; /* Maintain aspect ratio */
}
.data-table {
    margin-top: 20px;
}
.add-category-button {
    background-color: #4CAF50; /* Customize background color */
    color: white;
    border-radius: 0.375rem; /* Rounded corners */
    font-weight: bold;
    margin-top: 20px; /* Spacing above the button */
    padding: 10px 20px; /* Padding for the button */
    transition: background-color 0.3s ease; /* Smooth transition for hover effect */
}

.add-category-button:hover {
    background-color: #45a049; /* Darker shade for hover effect */
}
.btn {
    @apply bg-blue-600 text-white font-bold py-2 px-4 rounded; /* Tailwind CSS styles */
}
</style>
