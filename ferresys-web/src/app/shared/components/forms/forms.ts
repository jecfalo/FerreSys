import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, SimpleChange, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ModelForms } from '../../models/modelForms';

@Component({
  selector: 'app-forms',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './forms.html',
  styleUrl: './forms.css',
})
export class Forms {
  @Input() fields: ModelForms []=[];
  @Input() btnText: string = 'Enviar';
  @Input() showCancel: boolean = true;
  @Input() cancelText: string = 'Cancelar';
  @Input() data: any= null;
  @Input() isLoading: boolean = false;

  @Output() forSubmit = new EventEmitter<any>();
  @Output() formCancel = new EventEmitter<void>();
  //variables para el buscador
  searchTerms: {[key: string]: string} ={}
  dropdownOpen: {[key: string]: boolean}={}

  form!: FormGroup;
  constructor(private fb: FormBuilder){}
  ngOnInit(){
    this.launchForms();
  }
  ngOnChanges(changes: SimpleChanges):void{
    if(changes['data'] && this.form){
      if(this.data){
        //si estamos editando, carga los datos en el form y actualiza los textos del buscador
        this.form.patchValue(this.data);
        this.updateSearchTerms();
      }else{
        //si es nuevo, resetea el form y limpia los buscadores
        this.form.reset();
        this.clearSearchTerms();
      }
    }
  }
  launchForms():void{
    this.form = this.fb.group({});
    this.fields.forEach(
      field =>{
        //si hay data, la ponemos por defecto sino vacio
        const defaultValue = this.data ? this.data[field.name]: '';
        const define = field.type === 'checkbox' ? false: defaultValue;

        this.form.addControl(
          field.name,
          this.fb.control(define, field.validators || [])
        );
      }
    );
    //configuracion inicial de los textos de busqueda
    if(this.data){
      this.updateSearchTerms();
    }else{
      this.clearSearchTerms();
    }
  }
  onSubmit():void{
    if(this.form.valid){
      this.forSubmit.emit(this.form.value);
    }else{
      this.form.markAllAsTouched();
    }
  }
  onCancel():void{
    this.formCancel.emit();
  }
  updateSearchTerms():void{
    this.fields.forEach(
      field=>{
        if(field.type === 'searchable-select' && field.displayKey && field.valueKey && field.options){
          //buscamos el id guardado en la data
          const selectValue = this.data[field.name];
          //buscamos a que opcion pertenece ese id
          const selectedOpt = field.options.find(opt =>
            opt[field.valueKey!]===selectValue
          );
          //si lo encuentra, ponemos el nombre visible en el cajon de busqueda
          this.searchTerms[field.name] = selectedOpt? selectedOpt[field.displayKey!]: '';
        }
      }
    );
  }
  clearSearchTerms():void{
    this.fields.forEach(
      field=>{
        if(field.type === 'searchable-select'){
          this.searchTerms[field.name] = '';
        }
      }
    );
  }
  // --- LÓGICA DEL BUSCADOR DESPLEGABLE (searchable-select) ---

  getFilteredOptions(field: ModelForms): any[] {
    if (!field.options || !field.displayKey) return [];
    // Obtenemos lo que el usuario escribió (convertido a minúsculas para ignorar Mayus/Minus)
    const term = (this.searchTerms[field.name] || '').toLowerCase();
    
    // Filtramos la lista de opciones
    return field.options.filter(opt =>
      String(opt[field.displayKey!]).toLowerCase().includes(term)
    );
  }

  selectOption(field: ModelForms, option: any): void {
    // 1. Guardamos el ID real en el FormGroup (lo que viaja al backend)
    this.form.get(field.name)?.setValue(option[field.valueKey!]);
    // 2. Mostramos el nombre descriptivo en el input visual
    this.searchTerms[field.name] = option[field.displayKey!];
    // 3. Cerramos el menú
    this.dropdownOpen[field.name] = false;
  }

  closeDropdownDelay(fieldName: string): void {
    // Retraso necesario para que el (click) de selectOption alcance a ejecutarse antes de que desaparezca el HTML
    setTimeout(() => {
      this.dropdownOpen[fieldName] = false;
    }, 200);
  }
}
