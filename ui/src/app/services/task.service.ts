import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { Task, TaskStatus } from '../models/task';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);
  private readonly API_BASE = 'http://localhost:8080';

  async load(): Promise<Task[]> {
    return firstValueFrom(this.http.get<Task[]>(`${this.API_BASE}/tasks`)).catch(
      () => [] as Task[]
    );
  }

  // CRUD
  async add(task: Omit<Task, 'id'>) {
    const created = await firstValueFrom(
      this.http.post<ApiTask>(`${this.API_BASE}/tasks`, task)
    ).catch(() => undefined);
  }

  async update(id: string, patch: Partial<Omit<Task, 'id'>>) {
    await firstValueFrom(this.http.put<ApiTask>(`${this.API_BASE}/tasks/${id}`, patch)).catch(
      () => undefined
    );
  }

  async remove(id: string) {
    await firstValueFrom(this.http.delete(`${this.API_BASE}/tasks/${id}`)).catch(() => undefined);
  }
}

interface ApiTask {
  id?: string;
  title: string;
  description?: string;
  status: TaskStatus;
  createdAt?: string;
  updatedAt?: string;
}
