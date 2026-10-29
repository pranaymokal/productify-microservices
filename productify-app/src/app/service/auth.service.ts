import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { BASE_URL } from '../app.token';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
  mobile: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authUrl: string;
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'auth_user';

  private loggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isLoggedIn$ = this.loggedInSubject.asObservable();

  private userSubject = new BehaviorSubject<string | null>(this.getSavedUser());
  public currentUser$ = this.userSubject.asObservable();

  constructor(
    private http: HttpClient,
    @Inject(BASE_URL) baseUrl: string
  ) {
    this.authUrl = baseUrl + '/auth-service/v1/auth';
  }

  private hasToken(): boolean {
    return !!localStorage.getItem(this.TOKEN_KEY);
  }

  private getSavedUser(): string | null {
    return localStorage.getItem(this.USER_KEY);
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.authUrl}/login`, credentials).pipe(
      tap((response) => {
        if (response && response.token) {
          localStorage.setItem(this.TOKEN_KEY, response.token);
          localStorage.setItem(this.USER_KEY, credentials.username);
          this.loggedInSubject.next(true);
          this.userSubject.next(credentials.username);
        }
      })
    );
  }

  register(data: RegisterRequest): Observable<string> {
    return this.http.post(`${this.authUrl}/register`, data, { responseType: 'text' });
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.loggedInSubject.next(false);
    this.userSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getUsername(): string | null {
    return localStorage.getItem(this.USER_KEY);
  }

  isAuthenticated(): boolean {
    return this.hasToken();
  }
}
