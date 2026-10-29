import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../model/User';
import { BASE_URL } from '../app.token';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private baseUrl: string;
  private baseAuthUrl: string;

  constructor(private http: HttpClient,@Inject(BASE_URL) baseUrl: string) {
        this.baseAuthUrl = baseUrl + '/auth-service/v1/auth';
        this.baseUrl = baseUrl + '/user-service/v1/users';
  }

  // Get all users
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.baseUrl);
  }

  // Sign up a new user
  signUpUser(user: User): Observable<User> {
    return this.http.post<User>(`${this.baseAuthUrl}/register`, user);
  }

  // Sign in a user
  signInUser(user: User): Observable<User> {
    return this.http.post<User>(`${this.baseAuthUrl}/login`, user);
  }
  
  // Sign out a user
  signOutUser(userId: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/signout`, { userId });
  }

  // Delete a user by ID
  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${userId}`);
  }

  // Update an existing user
  updateUser(userId: number, user: User): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/${userId}`, user);
  }
}