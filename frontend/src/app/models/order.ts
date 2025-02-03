import { Dish } from "./dish";
import { User } from "./user";

export interface Order {
    id: number;
    status: 'ORDERED' | 'PREPARING' | 'IN_DELIVERY' | 'DELIVERED' | 'CANCELED';
    createdBy: User;
    active: boolean;
    dishes: Dish[];
    createdAt: Date;
    lastStatusChange: Date;
}
