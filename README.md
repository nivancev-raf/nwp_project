# RAF Food Ordering System

A full-stack web application that simulates a food ordering platform.  
Users can place and track orders, schedule future deliveries, and manage their account, while administrators oversee all orders and system errors.  

Developed as part of the **Advanced Web Programming** course at RAF.

---

## Features
- Place, cancel, schedule, and track food orders  
- Real-time order status updates (ORDERED → PREPARING → IN_DELIVERY → DELIVERED)  
- Search and filter orders (by status, date, user)  
- Permission-based access control for all operations  
- Error logging for failed scheduled orders (e.g. max concurrent orders exceeded)  

---

## Tech Stack
- **Backend:** Java (Spring Boot) / JBoss, Relational Database (MySQL/PostgreSQL), JWT authentication  
- **Frontend:** Angular, REST API integration, real-time updates (polling)  

---

## Getting Started
1. Clone the repo  
2. Configure database in `application.properties`  
3. Run backend (Spring Boot / JBoss)  
4. Run frontend (`npm install` + `ng serve` or `npm start`)  

---

## License
Educational project for the Advanced Web Programming course at RAF.  
