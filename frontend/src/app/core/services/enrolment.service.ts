import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_ENDPOINTS } from '../constants/api.constants';
import {
  AttendanceUpdateRequest,
  Enrolment,
  EnrolmentRequest
} from '../models/enrolment.model';

@Injectable({
  providedIn: 'root'
})
export class EnrolmentService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = API_ENDPOINTS.ENROLMENTS;

  getAll(filters?: { batchId?: number; studentId?: number }): Observable<Enrolment[]> {
    let params = new HttpParams();
    if (filters?.batchId !== undefined) {
      params = params.set('batchId', String(filters.batchId));
    }
    if (filters?.studentId !== undefined) {
      params = params.set('studentId', String(filters.studentId));
    }
    return this.http.get<Enrolment[]>(this.baseUrl, { params });
  }

  getById(id: number): Observable<Enrolment> {
    return this.http.get<Enrolment>(`${this.baseUrl}/${id}`);
  }

  create(payload: EnrolmentRequest): Observable<Enrolment> {
    return this.http.post<Enrolment>(this.baseUrl, payload);
  }

  update(id: number, payload: EnrolmentRequest): Observable<Enrolment> {
    return this.http.put<Enrolment>(`${this.baseUrl}/${id}`, payload);
  }

  updateAttendance(id: number, payload: AttendanceUpdateRequest): Observable<Enrolment> {
    return this.http.patch<Enrolment>(`${this.baseUrl}/${id}/attendance`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
