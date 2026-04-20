export interface Profile{
    id?: number;
    document: string;
    names: string;
    surnames: string;
    email: string;
    typeUser: TypeUser | string;
    createdAt: string | null;
    updateAt: string | null;
    active: boolean;
}
export enum TypeUser{
    CLIENT = "Cliente",
    ADMIN = "Admin",
    SELLER = "Seller"
}