import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { AuthLayout } from './layouts/auth-layout/auth-layout';
import { MainLayout } from './layouts/main-layout/main-layout';
import { Inicio } from './pages/inicio/inicio';

export const routes: Routes = [
  // Redireciona a raiz para o login
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // Rotas SEM header e sidebar (login, cadastro)
  {
    path: '',
    component: AuthLayout,
    children: [{ path: 'login', component: Login }],
  },

  // Rotas COM header e sidebar
  {
    path: '',
    component: MainLayout,
    children: [{ path: 'inicio', component: Inicio }],
  },
];
