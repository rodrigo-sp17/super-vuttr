# Super Vuttr
![Badge](https://img.shields.io/github/workflow/status/rodrigo-sp17/super-vuttr/Deploy%20to%20Heroku)
![Badge](https://img.shields.io/github/issues/rodrigo-sp17/super-vuttr)
![Badge](https://img.shields.io/github/last-commit/rodrigo-sp17/super-vuttr)

Have you ever caught yourself trying to remember that *Very Useful Tool To Remember?*
Well, sweat no more: Super VUTTR will remember them for you!

## Motivation
This REST API was built as part of a challenge for BossaBox website.

## Description
The API provides Create, Read and Delete operations for useful tools.

This project was built using Spring Boot and MongoDB. The data layer uses a repository pattern on top of Spring Data
MongoDB, which greatly cuts down on boilerplate.

All resources except for *login*, *signup* and *documentation* are protected using Spring Security and JWT.

The data transfer between client and server follows the DTO pattern.

Deployment to Heroku and testing are automated using GitHub Actions pipelines.

## Prerequisites
- [Java JDK 15](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Maven](https://maven.apache.org/download.cgi) - Tested with 3.6.3

## Instructions
Set the following environment variables before running:
```sh
JWT_SECRET=<yourJWTsecret>
URI=<yourMongoDBProductionURI>
TEST_URI=<yourMongoDBTestURI>
```
Then, clone this repository to your local machine and run the following command on the project's root folder:
```sh
mvn spring-boot:run
```
When the server is running, you can use cURL or Postman to make requests.

## Usage
The API is deployed on Heroku at https://super-vuttr.herokuapp.com/.

You can check the Swagger-UI for the API [here](https://super-vuttr.herokuapp.com/swagger-ui.html).

Additionally, you can check the OpenAPI documentation in JSON format [here](https://super-vuttr.herokuapp.com/api-docs).

## Tech Stack
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MongoDB](https://www.mongodb.com/)
- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
- [Spring HATEOAS](https://spring.io/projects/spring-hateoas) 
- [Spring Security](https://spring.io/projects/spring-security)
- [Lombok](https://projectlombok.org/)
- [springdoc-openapi](https://springdoc.org/)

## License
[MIT](https://opensource.org/licenses/MIT)




