import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('pages/IndexPage.vue'),
      },

      {
        path: 'gyms',
        name: 'GymsList',
        component: () => import('pages/GymsListPage.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'gyms/:id',
        name: 'GymDetail',
        component: () => import('pages/GymDetailPage.vue'),
        props: true,
        meta: { requiresAuth: true },
      },
      {
        path: 'routes',
        name: 'RoutesList',
        component: () => import('pages/RoutesListPage.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'routes/:id',
        name: 'RouteDetail',
        component: () => import('pages/RouteDetailPage.vue'),
        props: true,
        meta: { requiresAuth: true },
      },
      {
        path: 'climb-logs',
        name: 'ClimbLogs',
        component: () => import('pages/ClimbLogsPage.vue'),
        meta: { requiresAuth: true },
      },

      {
        path: 'profile',
        name: 'Profile',
        component: () => import('pages/ProfilePage.vue'),
        meta: { requiresAuth: true },
      },
    ],
  },

  {
    path: '/:catchAll(.*)*',
    name: 'NotFound',
    component: () => import('pages/ErrorNotFound.vue'),
  },
]

export default routes
