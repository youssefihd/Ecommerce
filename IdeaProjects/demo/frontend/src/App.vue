<template>
  <div id="app" class="container">
    <nav class="navbar">
      <span class="nav-title">E-commerce Application</span>
    </nav>

    <div class="main-content">
      <transition name="route" mode="out-in">
        <router-view :key="$route.fullPath"/> <!-- Note: Use 'router-view' instead of 'RouterView' -->
      </transition>
    </div>
  </div>
</template>

<script>
export default {
  name: 'App',
};
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.navbar {
  background-color: #004d99;
  padding: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
}

.nav-title {
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

/* Transition styles */
.route-enter-from {
  transform: translateX(100px);
  opacity: 0;
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
</style>
