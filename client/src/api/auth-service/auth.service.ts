import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import {AuthResponse, LoginRequest, RegisterRequest, User} from './auth.models';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/users';
  private readonly TOKEN_KEY = 'access_token';

  private accessToken: string | null = null;

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.restoreSession(); // <-- add this
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

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap(response => this.handleAuthSuccess(response))
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/register`, data).pipe(
      tap(response => this.handleAuthSuccess(response))
    );
  }

  logout(): void {
    this.clearSession();
    this.router.navigate(['/login']);
  }

  getAccessToken(): string | null {
    return this.accessToken;
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.getValue();
  }

  isLoggedIn(): boolean {
    return this.isAuthenticatedSubject.getValue();
  }

  private handleAuthSuccess(response: AuthResponse): void {
    this.accessToken = response.token;
    localStorage.setItem(this.TOKEN_KEY, response.token); // <-- persist it
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
