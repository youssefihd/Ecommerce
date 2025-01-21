import { createRouter, createWebHistory } from 'vue-router';
import { inject } from 'vue'; 
import MyDashboard from '../components/MyDashBoard.vue';
import ProductsPage from '../components/ProductsPage.vue';
import CustomersPage from '../components/CustomersPage.vue';
import OrderView from '../components/OrderView.vue';
import AddCustomer from '../components/AddCustomer.vue';
import AddProduct from '../components/AddProduct.vue';
import AddOrder from '../components/AddOrder.vue';
import UpdateCustomer from '../components/UpdateCustomer.vue';
import UpdateOrder from '../components/UpdateOder.vue';
import CategoryView from '../components/CategoryView.vue'; 
import AddCategory from '../components/AddCategory.vue';
import SeeMyOrders from '../components/SeeMyOrders.vue'; 
import ProductDetails from '../components/ProductDetails.vue'; 
import CustomersLogin from '../components/CustomersLogin.vue'; 

const routes = [
  { path: '/', component: MyDashboard },
  { path: '/products', component: ProductsPage },
  { path: '/customers', component: CustomersPage, meta: { requiresAuth: true } },
  { path: '/orders', component: OrderView, meta: { requiresAuth: true } }, // Added requiresAuth
  { path: '/add-customer', component: AddCustomer },
  { path: '/add-product', component: AddProduct },
  { path: '/add-order', component: AddOrder },
  { path: '/customers/login', component: CustomersLogin },
  {
    path: '/update-customer/:customer',
    name: 'UpdateCustomer',
    component: UpdateCustomer,
    props: true,
  },
  {
    path: '/products/:productId',
    name: 'ProductDetails',
    component: ProductDetails,
  },
  {
    path: '/see-my-orders',
    name: 'SeeMyOrders',
    component: SeeMyOrders,
  },
  {
    path: '/add-order/:productId',
    name: 'AddOrder',
    component: AddOrder,
  },
  {
    path: '/update-order/:orderId',
    name: 'UpdateOrder',
    component: UpdateOrder,
  },
  {
    path: '/categories',
    name: 'CategoryView',
    component: CategoryView,
  },
  {
    path: '/add-category',
    name: 'AddCategory',
    component: AddCategory,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Navigation guard
router.beforeEach(async (to, from, next) => {
  const keycloak = inject('$keycloak'); 

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!keycloak || !keycloak.token) {
      // Initialize Keycloak here and handle the login redirect
      try {
        await keycloak.init({ onLoad: 'login-required' });
      } catch (error) {
        console.error('Failed to initialize Keycloak', error);
        next(false); // Cancel navigation
        return;
      }
    }
  }
  next(); // Proceed to the route
});

export default router;
