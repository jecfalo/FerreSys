export interface ModelForms{
    name: string;
    label: string;
    type: 'text' | 'number' | 'date' | 'select' | 'radio' | 'checkbox' | 'textarea' | 'password' | 'email' | 'searchable-select';
    validators?: any[];
    options?: any[];
    //buscador
    displayKey?: string; //lo que ve
    valueKey?: string; //lo que guarda el form
    minDate?: string;
    maxDate?: string;
    colClass?: string;
    readonly?: boolean;
}