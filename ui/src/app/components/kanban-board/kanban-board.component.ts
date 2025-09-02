import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { MaterialModule } from '../../material/material.module';
import { Task, TaskStatus } from '../../models/task';
import { TaskService } from '../../services/task.service';
import { KanbanColumnComponent } from '../kanban-column/kanban-column.component';
import { MatDialog } from '@angular/material/dialog';
import { TaskDialogComponent, TaskDialogData } from '../task-dialog/task-dialog.component';

@Component({
  selector: 'app-kanban-board',
  standalone: true,
  imports: [CommonModule, MaterialModule, KanbanColumnComponent],
  templateUrl: './kanban-board.component.html',
  styleUrl: './kanban-board.component.css',
})
export class KanbanBoardComponent implements OnInit {
  private taskService = inject(TaskService);
  private dialog = inject(MatDialog);

  public todo = signal<Task[]>([]);
  public inProgress = signal<Task[]>([]);
  public done = signal<Task[]>([]);

  onDrop(event: CdkDragDrop<Task[]>, targetStatus: TaskStatus) {
    const task: Task | undefined = event.item.data as Task | undefined;
    if (!task) return;
    if (task.status !== targetStatus) {
      this.taskService
        .update(task.id, { ...task, status: targetStatus })
        .then(() => this.ngOnInit());
    }
  }

  openCreate(defaultStatus: TaskStatus) {
    const ref = this.dialog.open(TaskDialogComponent, {
      data: { defaultStatus } satisfies TaskDialogData,
    });
    ref.afterClosed().subscribe((result?: Partial<Task>) => {
      if (result && result.title) {
        this.taskService
          .add({
            title: result.title!,
            description: result.description,
            status: result.status ?? defaultStatus,
          })
          .then(() => this.ngOnInit());
      }
    });
  }

  openEdit(task: Task) {
    const ref = this.dialog.open(TaskDialogComponent, { data: { task } satisfies TaskDialogData });
    ref.afterClosed().subscribe((result?: Partial<Task>) => {
      if (result) {
        this.taskService.update(task.id, result).then(() => this.ngOnInit());
      }
    });
  }

  delete(task: Task) {
    this.taskService.remove(task.id).then(() => this.ngOnInit());
  }

  ngOnInit(): void {
    this.taskService.load().then((tasks) => {
      this.todo.set(tasks.filter((task) => task.status === 'TODO'));
      this.inProgress.set(tasks.filter((task) => task.status === 'IN_PROGRESS'));
      this.done.set(tasks.filter((task) => task.status === 'DONE'));
    });
  }
}
