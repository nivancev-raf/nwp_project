import { Order } from './order';    

export interface ErrorMessage {
    id: number;
    date: string;
    operation: string; 
    message: string;
    orderId: number;
}
