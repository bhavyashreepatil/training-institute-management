import { CommonModule } from '@angular/common';
import { Component, ContentChild, Input, TemplateRef } from '@angular/core';

export interface DataTableColumn {
  key: string;
  label: string;
  headerClass?: string;
}

export interface DataTableRowContext<T> {
  $implicit: T;
  index: number;
}

@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './data-table.component.html',
  styleUrl: './data-table.component.scss'
})
export class DataTableComponent<T> {
  @Input({ required: true }) columns: readonly DataTableColumn[] = [];
  @Input({ required: true }) items: readonly T[] = [];
  @Input() trackKey: keyof T | null = null;

  @ContentChild('rowTemplate') rowTemplate?: TemplateRef<DataTableRowContext<T>>;

  trackItem(index: number, item: T): string | number {
    if (this.trackKey !== null) {
      const value = item[this.trackKey];
      if (typeof value === 'string' || typeof value === 'number') {
        return value;
      }
    }
    return index;
  }
}
