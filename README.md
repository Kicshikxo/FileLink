<p align="center">
  <a href="https://github.com/Kicshikxo/FileLink">
    <img src="https://github.com/Kicshikxo/FileLink/blob/main/client/src/public/favicon.svg" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">FileLink</h3>

  <p align="center">
    Простое облачное хранилище файлов
  </p>
</p>

### <p align="center">[![Stargazers](https://img.shields.io/github/stars/Kicshikxo/FileLink?style=social)](https://github.com/Kicshikxo/FileLink) ![License](https://img.shields.io/github/license/Kicshikxo/FileLink)</p>

## О проекте

FileLink - это сервис для загрузки, хранения и обмена файлами.  
Пользователи могут загружать файлы, скачивать их, копировать ссылки для обмена и просматривать статистику скачиваний.

Основные функции:

- Загрузка файлов до 100 МБ
- Копирование прямых ссылок на файлы
- Просмотр списка загруженных файлов
- Статистика скачиваний файлов по дням
- Безопасная авторизация и аутентификация
- Автоматическая очистка файлов, которые не скачивались более суток

## Попробовать прямо сейчас

Используйте FileLink прямо в браузере:

- [filelink.kicshikxo.ru](https://filelink.kicshikxo.ru)

## Используемые технологии

Проект построен с использованием следующих технологий:

- **Backend:** Java 11, Javalin, PostgreSQL
- **Frontend:** Vite, Vanilla JS, Chart.js
- **Авторизация:** JWT
- **Управление зависимостями:** Gradle

## Установка и запуск

### Клонирование репозитория

```bash
git clone https://github.com/Kicshikxo/FileLink.git
cd FileLink
```

### Frontend (клиент)

Файлы клиента находятся в папке `client`.

#### Запуск в режиме разработки:

```bash
cd client
npm install
npm run dev
```

### Сборка релизной версии:

```bash
npm run build
npm run preview
```

Настройка порта и сервера API:

```bash
export PORT=3000
export API_URL=http://localhost:7070
npm run preview
```

### Backend (сервер)

Сервер находится в папке `server`.

#### Переменные окружения

Создайте файл .env рядом с сервером или используйте export:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/postgres
export DB_USER=postgres
export DB_PASSWORD=password

export AUTH_COOKIE_NAME="filelink-token"
export JWT_SECRET_KEY="yLZf4GOP+xk+eZ60VmpVDPhxRjcpuuWh8FWM22ZIjV0="

export PORT=7070
export UPLOADS_DIRECTORY="uploads"
```

[Пример .env файла](https://github.com/Kicshikxo/FileLink/blob/main/server/app/.env.example)

#### Запуск сервера:

```bash
cd server
gradle run
```

#### Сборка портативного `.jar` файла:

```bash
gradle build
```

После сборки файл `filelink.jar` появится в `server/app/build/libs/`. Запуск:

```bash
java -jar filelink.jar
```

## Лицензия

Распространяется по лицензии WTFPL. Смотрите [LICENSE](https://github.com/Kicshikxo/FileLink/blob/main/LICENSE) для большей информации.
