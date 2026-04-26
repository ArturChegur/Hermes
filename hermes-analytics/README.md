# hermes-analytics

Сервис аналитики: читает события из Kafka, сохраняет в Postgres, строит SQL-view статистики.

## Нужные переменные

- `DB_URL`, `DB_USER`, `DB_PASSWORD`
- `UPDATES_KAFKA_TOPIC`
- `CONSUMER_GROUP_ID`

## Быстрый запуск

```bash
docker compose up -d --build hermes-analytics
```
