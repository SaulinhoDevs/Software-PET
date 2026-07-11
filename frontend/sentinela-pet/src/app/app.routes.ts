import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { AuthLayout } from './layouts/auth-layout/auth-layout';
import { MainLayout } from './layouts/main-layout/main-layout';
import { Inicio } from './pages/inicio/inicio';
import { Painel } from './pages/painel/painel';
import { Agenda } from './pages/agenda/agenda';
import { Pacientes } from './pages/pacientes/pacientes';
import { CadastroPaciente } from './pages/cadastro-paciente/cadastro-paciente';
import { loginGuard } from './auth/login-guard';
import { authGuard } from './auth/auth-guard';
import { Profissionais } from './pages/profissionais/profissionais';
import { CadastroProfissional } from './pages/cadastro-profissional/cadastro-profissional';
import { DetalhePaciente } from './pages/detalhe-paciente/detalhe-paciente';

export const routes: Routes = [
  // Redireciona a raiz para o login
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // Rotas SEM header e sidebar (login)
  {
    path: '',
    component: AuthLayout,
    children: [{ path: 'login', component: Login, canActivate: [loginGuard] }],
  },

  // Rotas COM header e sidebar
  {
    path: '',
    component: MainLayout,
    canActivate: [authGuard],
    children: [
      { path: 'inicio', component: Inicio },
      { path: 'painel', component: Painel },
      { path: 'agenda', component: Agenda },
      { path: 'pacientes', component: Pacientes },
      { path: 'pacientes/detalhes/:id', component: DetalhePaciente },
      { path: 'pacientes/novo', component: CadastroPaciente },
      { path: 'profissionais', component: Profissionais },
      { path: 'profissionais/novo', component: CadastroProfissional },
    ],
  },
];
