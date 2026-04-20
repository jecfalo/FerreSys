import { OrderDetail } from "./OrderDetail";
import { PaidState } from "./PaidState";

export interface ReferenceOrder {
    id: number;
    paidState: PaidState;
    creationDate: string;
    updateDate: string;
    totalAmount: number;
    amountPaid: number;
    changeDue: number;
    clientName: string;
    documentClient: string;
    sellerName: string;
    cashRegiterName: string;
    itemDetail: OrderDetail[];
    active: boolean;
}