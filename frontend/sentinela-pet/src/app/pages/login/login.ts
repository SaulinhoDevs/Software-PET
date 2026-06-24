import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login/login-service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private fb = inject(FormBuilder);
  private loginService = inject(LoginService);
  private router = inject(Router);

  errorMessage: string = '';
  isLoading: boolean = false;

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(4)]],
  });

  onSubmit() {
    if (this.loginForm.invalid) {
      this.errorMessage = 'Por favor, preencha o email e a senha corretamente.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    // Pega os valores do formulário e mapeia para a interface LoginRequest
    const loginRequest = this.loginForm.value;

    this.loginService.logar(loginRequest).subscribe({
      next: (response) => {
        // Usa o método addToken do seu serviço para salvar no localStorage
        // Obs: Certifique-se de que a propriedade que vem do backend chama-se 'token'
        this.loginService.addToken(response.token);

        // Redireciona para a tela principal
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.isLoading = false;

        // Trata os erros de credenciais do Spring Security
        if (err.status === 401 || err.status === 403) {
          this.errorMessage = 'Email ou senha inválidos. Tente novamente.';
        } else {
          this.errorMessage = 'Erro ao conectar com o servidor. Verifique sua conexão.';
        }
      },
    });
  }
}
