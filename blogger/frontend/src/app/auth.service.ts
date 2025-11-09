import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = 'http://localhost:9999/auth'; //backend Spring Boot

  constructor(private http: HttpClient) {}

  register(data: any): Observable<any> {
   
    console.log("hada malo hada ");

    return this.http.post(`${this.baseUrl}/home`, data);
  }

 
  login(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/login`, data);
  }
}
  