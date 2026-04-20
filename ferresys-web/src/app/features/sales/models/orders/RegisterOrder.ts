import { RegisterOrderDetail } from "./RegisterOrderDetail";

export interface RegisterOrder {
    clientId: number;
    sellerId: number;
    cashRegisterId: number;
    details: RegisterOrderDetail[];
}