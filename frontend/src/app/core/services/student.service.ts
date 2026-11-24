import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_ENDPOINTS } from '../constants/api.constants';
import { Student, StudentRequest, StudentStatus } from '../models/student.model';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = API_ENDPOINTS.STUDENTS;

  getAll(search?: string, status?: StudentStatus): Observable<Student[]> {
    let params = new HttpParams();
    if (search && search.trim().length > 0) {
      params = params.set('search', search.trim());
    }
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<Student[]>(this.baseUrl, { params });
  }

  getById(id: number): Observable<Student> {
    return this.http.get<Student>(`${this.baseUrl}/${id}`);
  }

  create(payload: StudentRequest): Observable<Student> {
    return this.http.post<Student>(this.baseUrl, payload);
  }

  update(id: number, payload: StudentRequest): Observable<Student> {
    return this.http.put<Student>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
