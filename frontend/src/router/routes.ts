import type { RouteRecordRaw } from 'vue-router'
import MainLayout from 'layouts/MainLayout.vue'

export const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '',
        component: () => import('pages/IndexPage.vue'),
        name: 'Home',
      },
    ],
  },
  {
    path: '/gyms',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'GymsList',
        component: () => import('pages/GymsListPage.vue'),
      },
      {
        path: ':id',
        name: 'GymDetail',
        component: () => import('pages/GymDetailPage.vue'),
      },
      {
        path: 'new',
        name: 'GymCreate',
        component: () => import('pages/GymCreatePage.vue'),
      },
      {
        path: ':id/edit',
        name: 'GymEdit',
        component: () => import('pages/GymEditPage.vue'),
      },
    ],
  },
  {
    path: '/routes',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'RoutesList',
        component: () => import('pages/RoutesListPage.vue'),
      },
      {
        path: ':id',
        name: 'RouteDetail',
        component: () => import('pages/RouteDetailPage.vue'),
      },
      {
        path: 'new',
        name: 'RouteCreate',
        component: () => import('pages/RouteCreatePage.vue'),
      },
      {
        path: ':id/edit',
        name: 'RouteEdit',
        component: () => import('pages/RouteEditPage.vue'),
      },
    ],
  },
  {
    path: '/history',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'HistoryList',
        component: () => import('pages/HistoryListPage.vue'),
      },
    ],
  },
  {
    path: '/profile',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'Profile',
        component: () => import('pages/ProfilePage.vue'),
      },
    ],
  },
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
]

export default routes
