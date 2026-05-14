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
      },
      {
        path: 'gyms/new',
        name: 'GymCreate',
        component: () => import('pages/GymCreatePage.vue'),
      },
      {
        path: 'gyms/:id',
        name: 'GymDetail',
        component: () => import('pages/GymDetailPage.vue'),
        props: true,
      },
      {
        path: 'gyms/:id/edit',
        name: 'GymEdit',
        component: () => import('pages/GymEditPage.vue'),
        props: true,
      },

      {
        path: 'routes',
        name: 'RoutesList',
        component: () => import('pages/RoutesListPage.vue'),
      },
      {
        path: 'routes/new',
        name: 'RouteCreate',
        component: () => import('pages/RouteCreatePage.vue'),
      },
      {
        path: 'routes/:id',
        name: 'RouteDetail',
        component: () => import('pages/RouteDetailPage.vue'),
        props: true,
      },
      {
        path: 'routes/:id/edit',
        name: 'RouteEdit',
        component: () => import('pages/RouteEditPage.vue'),
        props: true,
      },

      {
        path: 'history',
        name: 'HistoryList',
        component: () => import('pages/HistoryListPage.vue'),
      },

      {
        path: 'profile',
        name: 'Profile',
        component: () => import('pages/ProfilePage.vue'),
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
