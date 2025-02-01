import { Dish } from "./dish";

export interface Order {
    id: number;
    status: 'ORDERED' | 'PREPARING' | 'IN_DELIVERY' | 'DELIVERED' | 'CANCELED';
    createdBy: number;
    active: boolean;
    dishes: Dish[];
    createdAt: Date;
    lastStatusChange: Date;
}
