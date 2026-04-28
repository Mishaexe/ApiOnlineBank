# ApiOnlineBanking

REST API для онлайн-банкинга: управление счетами, балансами и переводами.

---

## 📌 Содержание

- [Описание](#описание)
- [Основные возможности](#основные-возможности)
- [Технологии](#технологии)
- [Требования](#требования)
- [Установка и запуск](#установка-и-запуск)
- [API](#api)
- [Примеры запросов](#примеры-запросов)
- [скриншот структуры базы данных](#скриншот-структуры-базы-данных).

   ---

## Описание

Микросервис на Spring Boot, предоставляющий REST API для управления банковскими счетами.  
Позволяет:
- просматривать баланс,
- манипуляция балансом пользователя.

---

## Основные возможности

- ✅ Получение баланса по ID счёта (`GET /accounts/{id}/getBalance`)
- ✅ Пополнение счёта (`PUT /accounts/{id}/putMoney`)
- ✅ Снятие со счёта (`POST /accounts/{id}/takeMoney`)
- ✅ Переводы между счетами (`POST /accounts/transfer`)
---

## Технологии

- Язык: Java 21
- Фреймворк: Spring Boot 4.0.0
- Веб: Spring Web MVC (REST API)
- ORM: Spring Data JPA + Hibernate (под капотом)
- БД: PostgreSQL
- Валидация: Jakarta Bean Validation
- Помощь в коде: Lombok
- Сборка: Maven
- Тестирование: Spring Boot Test (JPA + Web MVC)

---

## Требования

- JDK 21+
- PostgreSQL 17+
- Maven 3.9.11+

---

## Установка и запуск

1. **Склонируй репозиторий**

git clone https://github.com/ваш-ник/ApiOnlineBanking.git
  cd ApiOnlineBanking

2. **Восстановите базу данных**

Чтобы работать с тестовыми файлами выполните команду:

psql -U postgres -d ApiOnlineBankingdb -f sql/dump.sql

---

## 🔗 Live Demo

Сервис развернут и доступен по адресу:  
👉[ [https://apionlinebank.onrender.com](https://apionlinebank.onrender.com)](https://apionlinebank-v2.onrender.com/)

# Пример: получить баланс пользователя под Id 1.

https://apionlinebank.onrender.com/accounts/1/getBalance

---

## API

Все эндпоинты доступны по адресу: `http://localhost:8080`

### Счета

 Метод | Эндпоинт                          | Описание                     | Тело запроса |
|--------|-----------------------------------|------------------------------|--------------|
| `GET`  | `/accounts/{id}/getBalance`       | Получить текущий баланс счёта | — |
| `PUT`  | `/accounts/{id}/putMoney`         | Пополнить счёт на указанную сумму | `{"amount": 500.00}` |
| `POST` | `/accounts/{id}/takeMoney`       | Снять со счёта на указанную сумму | `{"amount": 500.00}` |
| 'POST' | `/accounts/transfer`             | Перевести пользователю на указанную сумму | {"fromUserId": 1,"toUserId": 2,"amount": 1000}|
| 'GET'  | '/accounts/{id}/getOperationList' | Получить лист операций, можно за определенный диапазон дат. | - |

> 💡 Поле `amount` должно быть положительным числом.
> Все суммы хранятся с точностью до копеек (`NUMERIC(38,2)` в PostgreSQL).

## Примеры запросов

**Получить баланс счёта с ID = 1**

GET http://localhost:8080/accounts/1/getBalance

Ответ 0

**Пополнить счёт на 250 рублей**

PUT http://localhost:8080/accounts/1/putMoney
Content-Type: application/json

{
  "amount": 250.00
}

ответ 250

** Получить лист операций за определенный диапазон.

GET http://localhost:8080/accounts/2/getOperationList?from=2025-12-14T10:15:53&to=2025-12-14T10:15:54

---

Имеются в проекте Unit-Тестирование классов Service.

---

## скриншот структуры базы данных

<img width="1337" height="464" alt="изображение" src="https://github.com/user-attachments/assets/0a48795e-7a18-4454-8e81-f5502411cc15" />

<img width="1231" height="479" alt="изображение" src="https://github.com/user-attachments/assets/8b3e5010-1a62-47b4-866b-5f000b48a6bb" />

<img width="719" height="448" alt="изображение" src="https://github.com/user-attachments/assets/3c30f3cf-cde8-44ec-8a77-3f20faf27c09" />

<img width="733" height="19" alt="изображение" src="https://github.com/user-attachments/assets/fec28a46-4fd0-4807-b18f-e83503dfca20" />

<img width="1363" height="1247" alt="изображение" src="https://github.com/user-attachments/assets/6af36826-ddb6-4f3a-bd0f-a594daa4cc44" />

