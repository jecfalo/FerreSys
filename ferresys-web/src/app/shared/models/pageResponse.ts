export interface PageResponse<T>{
    //los datos
    content: T[];
    //informcion de la pagina actual
    pageable: any;
    //total de registros en la base de datos
    totalElements: number;
    //total de paginas disponibles
    totalPages: number;
    //es la ultima pagina?
    last: boolean;
    //tamanio de la pagina
    size: number;
    //numero de pagina actual 
    number: number;
}