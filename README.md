# Hermes

Коротко: 3 сервиса (`hermes-ingestor`, `hermes-analytics`, `hermes-frontend`) + Postgres + Kafka.

- `hermes-ingestor/README.md`
- `hermes-analytics/README.md`
- `hermes-frontend/README.md`

## 1) `.env` (в корне)

Заполни эти поля своими значениями. Ниже — **пример-шаблон**:

```env
TELEGRAM_BOT_TOKEN=1234567890:AAExampleTokenReplaceMe
TELEGRAM_BOT_USERNAME=MyHermesBot
FRONTEND_URL=http://your-server-or-domain:8080
DB_USER=hermes
DB_PASSWORD=hermes
DB_URL=jdbc:postgresql://postgres:5432/hermes
HOST_BASE_URL=http://your-server-or-domain:8080
UPDATES_KAFKA_TOPIC=telegram-updates
CONSUMER_GROUP_ID=hermes-analytics
DLQ_KAFKA_TOPIC=telegram-updates-dlt
LINK_TTL_MINUTES=60
JAVA_TOOL_OPTIONS=-XX:+UseParallelGC -Xms256m -Xmx512m
```

> Не коммить реальный `TELEGRAM_BOT_TOKEN` в репозиторий.

## 2) Запуск всего проекта

```bash
docker compose up -d --build
```

## 3) Полезно

- Frontend: `http://localhost:8080`
- Ingestor: `http://localhost:8081`
- Analytics: `http://localhost:8082`

```bash
docker compose up -d --build hermes-ingestor hermes-analytics hermes-frontend
```
