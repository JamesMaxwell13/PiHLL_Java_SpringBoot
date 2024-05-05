# Share Monitoring Service
This service allows you to view the status of shares of many companies on most well-known stock exchanges.

## Tech Stack
- Java 17
- Spring Boot
- Maven

## Getting Started
To get started with this project, you need to install database PostgreSQL and create a database named `sharesapp`.

## Installation
1. Clone the repository
2. Create an application.properties file and add in the file your local port and api-key:
```properties
server.port=YOUR_LOCAL_PORT

#Database
spring.datasource.url=jdbc:postgresql://localhost:5432/sharesapp
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

#JPA config
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

#Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.format_sql=true
```
3. Build the project and run the application

## CRUD Operations
---
### There are 3 main objects in the app and database:

1. **Company** - represents a company that is listed on the stock exchange.
2. **User** - represents a users in app, which can buy and monitor shares.
3. **Share** - represents the status of a company's share on a stock exchange.

Every entity has its own CRUD operations. You can create, read, update and delete any of them. 
There are any examples of CRUD operations with `User` entity:

#### Post request to create a new user:

Request:
```JSON
{
  "firstName" : "Ivan",
  "lastName" : "Sidorov",
  "email" : "vanyakaktus@gmail.com",
  "phoneNumber" : "+375294545677", 
  "password" : "password78"
}
```
#### Get request to get user:
`http://localhost:8080/api/user/{id}`

Response:
```JSON
{
  "id": 1,
  "firstName" : "Ivan",
  "lastName" : "Sidorov",
  "email" : "vanyakaktus@gmail.com",
  "phoneNumber" : "+375294545677"
}
```

#### Get request to get all users:
`http://localhost:8080/api/user/all`

Response:
```JSON
[
  {
    "id": 1,
    "firstName" : "Ivan", 
    "lastName" : "Sidorov", 
    "email" : "vanyakaktus@gmail.com", 
    "phoneNumber" : "+375294545677"
  }
]
```
#### Put request to update user:
`http://localhost:8080/api/user/{id}`

Request:
```JSON
{
  "firstName": "Vova",
  "lastName": "Ivanov",
  "email": "vaba@gmail.com",
  "phoneNumber": "+375294545677"
}
```

#### Delete request to delete user:
`http://localhost:8080/api/user/{id}`
