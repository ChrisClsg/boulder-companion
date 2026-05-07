<template>
  <q-page class="row items-center justify-evenly">
    <example-component
      title="Example component"
      active
      :todos="todos"
      :meta="meta"
    ></example-component>

    <q-btn
      label="Login"
      color="primary"
      @click="login"
    ></q-btn>

    <q-btn
      label="logout"
      color="primary"
      @click="logout"
    ></q-btn>

    <q-btn
      label="User Info"
      color="primary"
      @click="loadUser"
    ></q-btn>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import type { Todo, Meta } from 'components/models';
import ExampleComponent from 'components/ExampleComponent.vue';
import { api } from 'src/boot/axios';

const login = () => {
  const host:string = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin;

  console.log('Login clicked, redirecting to:', host + '/oauth2/authorization/github');
  window.open(host + '/oauth2/authorization/github', '_self');
}

const logout = () => {
  const host:string = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin;

  window.open(host + '/logout', '_self');
}

const loadUser = () => {
  api.get('/auth/me')
    .then(response => {
      console.log('User data:', response.data);
    })
    .catch(error => {
      console.error('Error loading user data:', error);
    });
}

const todos = ref<Todo[]>([
  {
    id: 1,
    content: 'ct1'
  },
  {
    id: 2,
    content: 'ct2'
  },
  {
    id: 3,
    content: 'ct3'
  },
  {
    id: 4,
    content: 'ct4'
  },
  {
    id: 5,
    content: 'ct5'
  }
]);

const meta = ref<Meta>({
  totalCount: 1200
});
</script>
