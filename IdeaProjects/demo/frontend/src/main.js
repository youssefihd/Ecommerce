import { createApp } from 'vue';
import App from './App.vue';
import router from './router/index';
import keycloak from './keycloak'; // Keep only the keycloak instance

import './assets/tailwind.css';
import Lara from '@primevue/themes/lara';
import 'primeicons/primeicons.css';
import PrimeVue from "primevue/config";

const app = createApp(App);
app.use(PrimeVue, {
    theme: {
        preset: Lara
    }
});

// Provide Keycloak instance globally
app.provide('$keycloak', keycloak); 
app.use(router);
app.mount('#app');
