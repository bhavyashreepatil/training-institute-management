import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_ENDPOINTS } from '../constants/api.constants';
import { Course, CourseRequest } from '../models/course.model';

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = API_ENDPOINTS.COURSES;

  getAll(search?: string, activeOnly?: boolean): Observable<Course[]> {
    let params = new HttpParams();
    if (search && search.trim().length > 0) {
      params = params.set('search', search.trim());
    }
    if (activeOnly !== undefined) {
      params = params.set('activeOnly', String(activeOnly));
    }
    return this.http.get<Course[]>(this.baseUrl, { params });
  }

  getById(id: number): Observable<Course> {
    return this.http.get<Course>(`${this.baseUrl}/${id}`);
  }

  create(payload: CourseRequest): Observable<Course> {
    return this.http.post<Course>(this.baseUrl, payload);
  }

  update(id: number, payload: CourseRequest): Observable<Course> {
    return this.http.put<Course>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
