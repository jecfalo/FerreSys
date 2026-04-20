import { PaidState } from "./PaidState";

export interface UpdateOrder {
    id: number;
    state: PaidState;
    amountPaid: number;
}