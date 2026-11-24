import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_ENDPOINTS } from '../constants/api.constants';
import { Batch, BatchRequest, BatchRoster, BatchStatus } from '../models/batch.model';

@Injectable({
  providedIn: 'root'
})
export class BatchService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = API_ENDPOINTS.BATCHES;

  getAll(filters?: { status?: BatchStatus; courseId?: number; trainerId?: number }): Observable<Batch[]> {
    let params = new HttpParams();
    if (filters?.status) {
      params = params.set('status', filters.status);
    }
    if (filters?.courseId !== undefined) {
      params = params.set('courseId', String(filters.courseId));
    }
    if (filters?.trainerId !== undefined) {
      params = params.set('trainerId', String(filters.trainerId));
    }
    return this.http.get<Batch[]>(this.baseUrl, { params });
  }

  getById(id: number): Observable<Batch> {
    return this.http.get<Batch>(`${this.baseUrl}/${id}`);
  }

  getRoster(id: number): Observable<BatchRoster> {
    return this.http.get<BatchRoster>(`${this.baseUrl}/${id}/roster`);
  }

  create(payload: BatchRequest): Observable<Batch> {
    return this.http.post<Batch>(this.baseUrl, payload);
  }

  update(id: number, payload: BatchRequest): Observable<Batch> {
    return this.http.put<Batch>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
