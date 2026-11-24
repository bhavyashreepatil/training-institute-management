import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_ENDPOINTS } from '../constants/api.constants';
import { Trainer, TrainerRequest } from '../models/trainer.model';

@Injectable({
  providedIn: 'root'
})
export class TrainerService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = API_ENDPOINTS.TRAINERS;

  getAll(search?: string, activeOnly?: boolean): Observable<Trainer[]> {
    let params = new HttpParams();
    if (search && search.trim().length > 0) {
      params = params.set('search', search.trim());
    }
    if (activeOnly !== undefined) {
      params = params.set('activeOnly', String(activeOnly));
    }
    return this.http.get<Trainer[]>(this.baseUrl, { params });
  }

  getById(id: number): Observable<Trainer> {
    return this.http.get<Trainer>(`${this.baseUrl}/${id}`);
  }

  create(payload: TrainerRequest): Observable<Trainer> {
    return this.http.post<Trainer>(this.baseUrl, payload);
  }

  update(id: number, payload: TrainerRequest): Observable<Trainer> {
    return this.http.put<Trainer>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
