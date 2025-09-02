import { Component, EventEmitter, Input, Output, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CdkDropList, CdkDrag, CdkDragDrop } from '@angular/cdk/drag-drop';
import { MaterialModule } from '../../material/material.module';
import { Task, TaskStatus } from '../../models/task';

@Component({
  selector: 'app-kanban-column',
  standalone: true,
  imports: [CommonModule, CdkDropList, CdkDrag, MaterialModule],
  templateUrl: './kanban-column.component.html',
  styleUrl: './kanban-column.component.css',
})
export class KanbanColumnComponent {
  @Input() title = '';
  @Input() status!: TaskStatus;
  @Input() tasks: Task[] = [];
  @Input() connectedTo: string | string[] = [];

  @Output() dropped = new EventEmitter<CdkDragDrop<Task[]>>();
  @Output() edit = new EventEmitter<Task>();
  @Output() remove = new EventEmitter<Task>();

  trackById = (_: number, t: Task) => t.id;
}
