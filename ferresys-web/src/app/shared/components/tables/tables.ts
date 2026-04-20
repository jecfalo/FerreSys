import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ModelTable } from '../../models/modelTable';

@Component({
  selector: 'app-tables',
  imports: [],
  templateUrl: './tables.html',
  styleUrl: './tables.css',
})
export class Tables {
  @Input() title: string = '';
  @Input() data: any[] = [];
  @Input() columns: ModelTable [] = [];
  @Input() btnNewText: string = 'Nuevo';

  @Output() onView = new EventEmitter<any>();
  @Output() onEdit = new EventEmitter<any>();
  @Output() onDelete = new EventEmitter<any>();
  @Output() onNew = new EventEmitter<void>();
  getCellValue(row: any, col: ModelTable):any{
    const value = row[col.key];
    if(col.transform){
      return col.transform(value);
    }
    if(col.type === 'boolean'){
      return value ? 'Activo' :'Inactivo'
    }
    return value;
  }
}
