# Descrption
Java Maven REST API backend with 3 microservices(location, trip, user) and API Gateway to act as a simulation of Uber API

# Setup


### Prerequisites

Latest version of Java  
- JDK Version 16.0.1  
- JRE Version 16.0.1   

Maven version 3.6.3  
IntelliJ IDEA Community Edition(recommended IDE)  
Postman(API testing)  
Lastest version of Docker  

### Datebase visualizing
- Lastest version of MongoCompass  
- Lastest version of pgAdmin4  
- Latest version of Neo4j Desktop 

### Setup
1. Clone the repo if needed
   ```sh
   git clone https://github.com/Tony-beeper/zoomer.git
   ```
2. dockerize all three microservices with the docker-compose.yml provided with
    ```sh
    docker-compose up --build -d
    ```
3. Make sure project SDK on IDE is JDK Version 16.0.1  



# DATABASE REQUIREMENTS

MongoDB
pgAdmin4
Neo4j Desktop


# JUnit Testing

Run with CLI in docker

```sh
mvn test
```
