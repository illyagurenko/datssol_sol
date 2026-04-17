# DatsSol Bot

AI бот для игры DatsSol, реализованный на Java + Spring Boot.

## Структура проекта

```
datsol-bot/
├── src/main/java/com/datssol/bot/
│   ├── DatsolBotApplication.java    # Точка входа Spring Boot
│   ├── client/
│   │   └── DatsSolClient.java       # HTTP клиент для API игры
│   ├── config/
│   │   └── SchedulerConfig.java     # Конфигурация планировщика
│   ├── model/
│   │   ├── ArenaState.java          # Модель состояния арены
│   │   ├── Player.java              # Модель игрока
│   │   ├── Plantation.java          # Модель плантации
│   │   └── CommandRequest.java      # Модель команды
│   ├── service/
│   │   └── GameService.java         # Игровой сервис с game loop
│   └── strategy/
│       └── GameStrategy.java        # Стратегия принятия решений
├── src/main/resources/
│   └── application.yml              # Конфигурация приложения
└── pom.xml                          # Maven зависимости
```

## Запуск

### Требования
- Java 17+
- Maven 3.8+

### Сборка
```bash
cd datsol-bot
mvn clean package -DskipTests
```

### Запуск
```bash
java -jar target/datsol-bot-1.0-SNAPSHOT.jar --datssol.player-id=YOUR_PLAYER_ID
```

Или через Maven:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--datssol.player-id=YOUR_PLAYER_ID"
```

## Конфигурация

| Параметр | Описание | По умолчанию |
|----------|----------|--------------|
| `server.port` | Порт HTTP сервера | 8080 |
| `datssol.api.base-url` | Базовый URL API | https://games-test.datsteam.dev |
| `datssol.player-id` | ID игрока | (требуется указать) |

## Стратегия

Бот использует трехфазную стратегию:

1. **Экспансия (0-150 тиков)**: Быстрое захватывание территории вокруг ЦУ
2. **Консолидация (150-450 тики)**: Укрепление позиций, защита ЦУ
3. **Эндшпиль (450-600 тики)**: Защита, сохранение очков

### Приоритеты
1. Защита ЦУ (потеря = сброс прогресса)
2. Поддержание связности сети
3. Эффективное использование команд

## API Интеграция

- `GET /static/datssol/openapi/api/arena` - получение состояния арены
- `POST /static/datssol/openapi/api/command` - отправка команды
- `GET /static/datssol/openapi/api/logs` - получение логов

## Команды

- `create x y` - создать плантацию в координатах (x, y)
- `wait` - пропустить ход
