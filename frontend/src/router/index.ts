import { defineRouter } from '#q-app/wrappers';
import {
  createMemoryHistory,
  createRouter,
  createWebHashHistory,
  createWebHistory,
} from 'vue-router';
import { Notify } from 'quasar';
import { useAuthStore } from 'src/stores/authStore';
import routes from './routes';

/*
 * If not building with SSR mode, you can
 * directly export the Router instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Router instance.
 */

export default defineRouter((/* { store, ssrContext } */) => {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : (process.env.VUE_ROUTER_MODE === 'history' ? createWebHistory : createWebHashHistory);

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,

    // Leave this as is and make changes in quasar.conf.js instead!
    // quasar.conf.js -> build -> vueRouterMode
    // quasar.conf.js -> build -> publicPath
    history: createHistory(process.env.VUE_ROUTER_BASE),
  });

  // Block unauthenticated access to protected routes.
  Router.beforeEach(async (to) => {
    if (!to.meta.requiresAuth) return true;

    const authStore = useAuthStore();
    await authStore.fetchUser(); // idempotent — cached via hasFetchedUser

    if (authStore.isAuthenticated) return true;

    Notify.create({
      type: 'warning',
      message: 'Please log in to access this page.',
      position: 'top',
      timeout: 3000,
    });
    return { name: 'Home' };
  });

  // Re-sync auth state after each navigation so the header/drawer stay accurate.
  // The 100 ms delay lets Vue finish rendering the incoming route before the
  // fetch triggers a potential re-render (avoids a flash of unauthenticated UI).
  Router.afterEach(() => {
    setTimeout(() => {
      useAuthStore().fetchUser().catch(console.error);
    }, 100);
  });

  return Router;
});
