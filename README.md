# 🏨 Hotel Management API

RESTful API для управления отелями. Проект разработан на **Java 21** с использованием **Spring Boot**, **Spring Data JPA**, **Liquibase** и **H2 database**.

---

## 📋 Содержание
- [Технологии](#технологии)
- [Требования](#требования)
- [Быстрый старт](#быстрый-старт)
- [Конфигурация](#конфигурация)
- [API Endpoints](#api-endpoints)
- [Примеры запросов](#примеры-запросов)
- [Переключение между БД](#переключение-между-бд)
- [Liquibase](#liquibase)
- [Тестирование](#тестирование)
- [Swagger UI](#swagger-ui)
- [Структура проекта](#структура-проекта)
- [Возможные проблемы](#возможные-проблемы)

---

## 🛠 Технологии
Java 21, Spring Boot 3.2.0, Spring MVC, Spring Data JPA, Liquibase, H2 Database, MapStruct, Lombok, Swagger/OpenAPI 3, JUnit 5, Mockito, Maven

## 📦 Требования
- Java 21 или выше
- Maven 3.8+
- Docker (опционально, для других БД)

## 🚀 Быстрый старт
```bash
git clone https://github.com/dub200586/HotelApi
cd hotel-api
mvn clean install
mvn spring-boot:run
После запуска приложение доступно по адресу: http://localhost:8092/property-view/api/v1/hotels

## ⚙️ Конфигурация
application.yml
yaml
server:
  port: 8092
  servlet:
    context-path: /property-view

spring:
  profiles:
    active: h2
  jackson:
    property-naming-strategy: SNAKE_CASE
    serialization:
      write-dates-as-timestamps: false

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method

logging:
  level:
    liquibase: INFO
    com.hotelapi: DEBUG
application-h2.yml
yaml
spring:
  datasource:
    url: jdbc:h2:mem:hoteldb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: validate
    show-sql: true
    defer-datasource-initialization: true
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
  h2:
    console:
      enabled: true
      path: /h2-console

## 📌 API Endpoints
Метод	Endpoint	Описание
GET	/api/v1/hotels	Список всех отелей
GET	/api/v1/hotels/{id}	Детальная информация об отели
GET	/api/v1/search	Поиск отелей по параметрам
POST	/api/v1/hotels	Создать новый отель
POST	/api/v1/hotels/{id}/amenities	Добавить удобства к отелю
GET	/api/v1/histogram/{param}	Гистограмма по параметру
Параметры поиска (/search)
name - название отеля

brand - бренд

city - город

country - страна

amenity - удобство

Параметры гистограммы (/histogram/{param})
city - группировка по городам

country - группировка по странам

brand - группировка по брендам

amenities - группировка по удобствам

## 💻 Примеры запросов
Создание отеля
bash
curl -X POST http://localhost:8092/property-view/api/v1/hotels \
  -H "Content-Type: application/json" \
  -d '{
    "name": "DoubleTree by Hilton Minsk",
    "description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms...",
    "brand": "Hilton",
    "address": {
      "house_number": 9,
      "street": "Pobediteley Avenue",
      "city": "Minsk",
      "country": "Belarus",
      "post_code": "220004"
    },
    "contacts": {
      "phone": "+375 17 309-80-00",
      "email": "minsk@hilton.com"
    },
    "arrival_time": {
      "check_in": "14:00",
      "check_out": "12:00"
    }
  }'

Добавление удобств
bash
curl -X POST http://localhost:8092/property-view/api/v1/hotels/1/amenities \
  -H "Content-Type: application/json" \
  -d '["Free parking", "Free WiFi", "Non-smoking rooms", "Concierge", "On-site restaurant", "Fitness center", "Pet-friendly rooms", "Room service", "Business center", "Meeting rooms"]'
Поиск отелей
bash
curl "http://localhost:8092/property-view/api/v1/search?city=Minsk&brand=Hilton"
Получение гистограммы
bash
curl http://localhost:8092/property-view/api/v1/histogram/city
bash
curl http://localhost:8092/property-view/api/v1/histogram/amenities

## 🔄 Переключение между БД
MySQL
Зависимость в pom.xml:

xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
application-mysql.yml:

yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hoteldb?useSSL=false&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: yourpassword
  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect
    hibernate:
      ddl-auto: validate
    show-sql: true
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
Запуск:

bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
PostgreSQL
Зависимость в pom.xml:

xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
application-postgresql.yml:

yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hoteldb
    driver-class-name: org.postgresql.Driver
    username: postgres
    password: yourpassword
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
    show-sql: true
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
Запуск:

bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgresql

## 📝 Liquibase
Changelog файлы расположены в src/main/resources/db/changelog/:

db.changelog-master.yaml

yaml
databaseChangeLog:
  - include:
      file: db/changelog/v1/v1-create-tables.yaml
v1/v1-create-tables.yaml

yaml
databaseChangeLog:
  - changeSet:
      id: 1
      author: yourname
      changes:
        - createTable:
            tableName: hotels
            columns:
              - column:
                  name: id
                  type: BIGINT
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: name
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
              - column:
                  name: description
                  type: VARCHAR(2000)
              - column:
                  name: brand
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
              - column:
                  name: house_number
                  type: INT
                  constraints:
                    nullable: false
              - column:
                  name: street
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
              - column:
                  name: city
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
              - column:
                  name: country
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
              - column:
                  name: post_code
                  type: VARCHAR(20)
                  constraints:
                    nullable: false
              - column:
                  name: phone
                  type: VARCHAR(50)
                  constraints:
                    nullable: false
              - column:
                  name: email
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
              - column:
                  name: check_in
                  type: VARCHAR(5)
                  constraints:
                    nullable: false
              - column:
                  name: check_out
                  type: VARCHAR(5)
              - column:
                  name: created_at
                  type: TIMESTAMP
              - column:
                  name: updated_at
                  type: TIMESTAMP

  - changeSet:
      id: 2
      author: yourname
      changes:
        - createTable:
            tableName: amenities
            columns:
              - column:
                  name: id
                  type: BIGINT
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: name
                  type: VARCHAR(255)
                  constraints:
                    unique: true
                    nullable: false

  - changeSet:
      id: 3
      author: yourname
      changes:
        - createTable:
            tableName: hotel_amenities
            columns:
              - column:
                  name: hotel_id
                  type: BIGINT
                  constraints:
                    nullable: false
              - column:
                  name: amenity_id
                  type: BIGINT
                  constraints:
                    nullable: false
        - addPrimaryKey:
            tableName: hotel_amenities
            columnNames: hotel_id, amenity_id
            constraintName: pk_hotel_amenities
        - addForeignKeyConstraint:
            baseTableName: hotel_amenities
            baseColumnNames: hotel_id
            referencedTableName: hotels
            referencedColumnNames: id
            constraintName: fk_hotel_amenities_hotel
        - addForeignKeyConstraint:
            baseTableName: hotel_amenities
            baseColumnNames: amenity_id
            referencedTableName: amenities
            referencedColumnNames: id
            constraintName: fk_hotel_amenities_amenity
Liquibase автоматически создаёт все таблицы при первом запуске. Для отключения установите spring.liquibase.enabled: false

## 🧪 Тестирование
bash
# Запуск всех тестов
mvn test

# Запуск конкретного теста
mvn test -Dtest=HotelControllerTest
mvn test -Dtest=HotelServiceTest

# Запуск с покрытием
mvn test jacoco:report

## 📚 Swagger UI
Документация API доступна по адресу:
http://localhost:8092/property-view/swagger-ui.html

## 📁 Структура проекта
text
src/main/java/com/hotelapi/
├── config/
│   ├── DatabaseConfig.java
│   └── OpenAPIConfig.java
├── controller/
│   └── HotelController.java
├── dto/
│   ├── HotelCreateDTO.java
│   ├── HotelSummaryDTO.java
│   ├── HotelDetailDTO.java
│   ├── AddressDTO.java
│   ├── ContactsDTO.java
│   ├── ArrivalTimeDTO.java
│   └── HistogramResponse.java
├── exception/
│   ├── ErrorResponse.java
│   ├── ResourceNotFoundException.java
│   └── ValidationException.java
├── handler/
│   └── GlobalExceptionHandler.java
├── mapper/
│   └── HotelMapper.java
├── model/
│   ├── Hotel.java
│   ├── Address.java
│   ├── Amenity.java
│   ├── Contacts.java
│   └── ArrivalTime.java
├── repository/
│   ├── HotelRepository.java
│   └── AmenityRepository.java
└── service/
    ├── HotelService.java
    └── HotelServiceImpl.java

src/main/resources/
├── db/
│   └── changelog/
│       └── db.changelog-master.yaml
├── application.yml
├── application-h2.yml
├── application-mysql.yml
└── application-postgresql.yml

src/test/java/com/hotelapi/
├── controller/
│   └── HotelControllerTest.java
├── service/
│   └── HotelServiceTest.java
├── repository/
│   └── HotelRepositoryTest.java
└── HotelApiApplicationTests.java

## ⚠️ Возможные проблемы
1. Ошибка циклической зависимости Liquibase и Hibernate
Решение:

yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
2. Ошибки валидации при создании отеля
Решение: Проверьте формат времени:

check_out должно быть после check_in

Формат: HH:MM (например, "14:00")
3. Приложение запускается и сразу останавливается
Решение: Убедитесь, что вы запускаете main-класс HotelAPIApplication, а не тесты. Используйте mvn spring-boot:run