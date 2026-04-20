export interface ModelTable{
    header: string;
    key: string;
    type: 'text' | 'date' | 'boolean' | 'custom' | 'number';
    transform?: (value: any) => string;
}