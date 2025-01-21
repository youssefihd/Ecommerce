<template>
    <div class="p-6 bg-white rounded shadow">
        <h2 class="text-2xl font-bold mb-4">Add New Category</h2>
        <div class="input-group">
            <InputText 
                v-model="categoryName" 
                placeholder="Category Name" 
                class="mb-4 input-field" 
            />
            <InputText 
                v-model="brand" 
                placeholder="Brand" 
                class="mb-4 input-field" 
            />
            <TextArea 
                v-model="description" 
                placeholder="Description" 
                rows="5" 
                class="input-field" 
            />
            <InputText 
                v-model="imgurl" 
                placeholder="Image URL" 
                class="mb-4 input-field" 
            />
        </div>
        <div class="button-group">
            <Button label="Add Category" @click="addCategory" class="add-button" />
            <Button label="Cancel" @click="goBack" class="cancel-button" />
        </div>
    </div>
</template>

<script>
import { ref, inject } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import InputText from 'primevue/inputtext';
import TextArea from 'primevue/textarea';
import Button from 'primevue/button';

export default {
    name: 'AddCategory',
    components: {
        InputText,
        TextArea,
        Button,
    },
    setup() {
        const keycloak = inject('$keycloak');
        const categoryName = ref('');
        const brand = ref('');
        const description = ref('');
        const imgurl = ref(''); // New reactive variable for image URL
        const router = useRouter();

        const addCategory = async () => {
            try {
                if (keycloak.isTokenExpired()) {
                    await keycloak.updateToken(30);
                }
                const token = keycloak.token;

                // Get the current date and time
                const createdDate = new Date().toISOString();

                const response = await axios.post('http://localhost:5051/api/categories', {
                    name: categoryName.value,
                    brand: brand.value,
                    description: description.value,
                    imgurl: imgurl.value, // Add imgurl to the request
                    createdDate: createdDate,
                }, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                console.log('Category added:', response.data);
                // Reset form fields after successful addition
                categoryName.value = '';
                brand.value = '';
                description.value = '';
                imgurl.value = ''; // Reset imgurl
            } catch (error) {
                console.error('Error adding category:', error.response ? error.response.data : error.message);
            }
        };

        const goBack = () => {
            router.push({ name: 'CategoryView' });
        };

        return {
            categoryName,
            brand,
            description,
            imgurl,
            addCategory,
            goBack,
        };
    },
};
</script>

<style scoped>
.input-group {
    display: flex;
    flex-direction: column;
}

.input-field {
    width: 100%;
}

.button-group {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
}

.add-button {
    background-color: #4CAF50;
    color: white;
    border-radius: 0.375rem;
    font-weight: bold;
}

.add-button:hover {
    background-color: #45a049;
}

.cancel-button {
    background-color: #dc2626;
    color: white;
    border-radius: 0.375rem;
    font-weight: bold;
}

.cancel-button:hover {
    background-color: #b91c1c;
}
</style>
