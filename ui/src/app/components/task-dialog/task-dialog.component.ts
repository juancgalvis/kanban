import { Component, inject, input, signal } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MaterialModule } from '../../material/material.module';
import { Task, TaskStatus } from '../../models/task';

export type TaskDialogData = { task?: Task; defaultStatus?: TaskStatus };

@Component({
  selector: 'app-task-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MaterialModule],
  templateUrl: './task-dialog.component.html',
  styleUrl: './task-dialog.component.css',
})
export class TaskDialogComponent {
  private fb = inject(FormBuilder);
  dialogRef = inject(MatDialogRef<TaskDialogComponent, Partial<Task> | undefined>);
  data = inject<TaskDialogData>(MAT_DIALOG_DATA);

  form = this.fb.nonNullable.group({
    title: ['', Validators.required],
    description: [''],
    status: this.fb.nonNullable.control<TaskStatus>(this.data.defaultStatus ?? 'TODO'),
  });

  constructor() {
    if (this.data.task) {
      const { title, description, status } = this.data.task;
      this.form.patchValue({ title, description: description ?? '', status });
    }
  }

  onSubmit() {
    if (this.form.invalid) return;
    const value = this.form.getRawValue();
    const result: Partial<Task> = { title: value.title, description: value.description, status: value.status };
    this.dialogRef.close(result);
  }
}
