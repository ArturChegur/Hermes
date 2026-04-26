# hermes-frontend

Веб-сервис статистики: читает агрегаты из Postgres и показывает страницы чата/пользователя.

## Нужные переменные

- `DB_URL`, `DB_USER`, `DB_PASSWORD`
- `TELEGRAM_BOT_USERNAME` (для отображения на 404)

## Быстрый запуск

```bash
docker compose up -d --build hermes-frontend
```
