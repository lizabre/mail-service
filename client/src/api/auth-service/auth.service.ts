import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import {AuthResponse, LoginRequest, RegisterRequest, User} from './auth.models';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';

/**
 * Service for managing user authentication and session state.
 * Handles login, registration, logout and JWT token persistence.
 */
@Injectable({providedIn: 'root'})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/v1.0/users';
  private readonly TOKEN_KEY = 'access_token';

  private accessToken: string | null = null;
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.restoreSession();
  }

  /**
   * Authenticates a user with email and password.
   * @param credentials The login request payload.
   * @returns An observable emitting the {@link AuthResponse}.
   */
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap(response => this.handleAuthSuccess(response))
    );
  }

  /**
   * Registers a new user account.
   * @param data The registration request payload.
   * @returns An observable emitting the {@link AuthResponse}.
   */
  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/register`, data).pipe(
      tap(response => this.handleAuthSuccess(response))
    );
  }

  /**
   * Returns the current JWT access token.
   * @returns The token string or null if not authenticated.
   */
  getAccessToken(): string | null {
    return this.accessToken;
  }


  /**
   * Returns whether the user is currently authenticated.
   * @returns True if authenticated, false otherwise.
   */
  isLoggedIn(): boolean {
    return this.isAuthenticatedSubject.getValue();
  }

  private restoreSession(): void {
    const token = localStorage.getItem(this.TOKEN_KEY);
    const storedUser = localStorage.getItem('user');

    if (token && !this.isTokenExpired(token)) {
      this.accessToken = token;
      this.currentUserSubject.next(JSON.parse(storedUser as string));
      this.isAuthenticatedSubject.next(true);
    } else {
      this.clearSession();
    }
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 < Date.now();
    } catch {
      return true;
    }
  }

  private handleAuthSuccess(response: AuthResponse): void {
    this.accessToken = response.token;
    localStorage.setItem(this.TOKEN_KEY, response.token);
    const user: User = {
      id: response.id,
      firstName: response.firstName,
      lastName: response.lastName,
      email: response.email,
    };
    localStorage.setItem('user', JSON.stringify(user));
    this.isAuthenticatedSubject.next(true);
    this.currentUserSubject.next(user);
  }

  private clearSession(): void {
    this.accessToken = null;
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem('user');
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);
  }
}
