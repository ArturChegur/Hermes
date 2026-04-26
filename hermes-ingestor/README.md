# hermes-ingestor

Сервис Telegram-бота: принимает апдейты, обрабатывает команды и отправляет события в Kafka.

## Нужные переменные

- `TELEGRAM_BOT_TOKEN`
- `TELEGRAM_BOT_USERNAME`
- `DB_URL`, `DB_USER`, `DB_PASSWORD`
- `UPDATES_KAFKA_TOPIC`
- `HOST_BASE_URL`

## Быстрый запуск

```bash
docker compose up -d --build hermes-ingestor
```
