# Два приложения

## 1. SensorView

### Описание
Приложение **SensorVIew** предназначено для просмотра данных с сенсоров. Оно предоставляет интерфейс для работы с:
- Перечнем сенсоров.
- Отображением текущих данных с сенсоров.
- Управлением топиками MQTT.

### Основные функции
- Список сенсоров.
- Просмотр данных на графике

### Технологии
- **Jmix** для интерфейса и работы с данными.
- **Spring Framework** для управления зависимостями.
- **HSQLDB** (или другая база данных) для хранения информации о сенсорах.
- **Dotenv**
- **Yandex Cloud** 

---

## 2. SensorDataPublisher

### Описание
Приложение **SensorDataPublisher** отвечает за публикацию данных сенсоров в брокер MQTT. Оно имитирует сенсоры, генерирующие данные, и отправляет их по заданным топикам.

### Основные функции
- Генерация случайных данных (например, температуры).
- Публикация сообщений в топики MQTT.

### Технологии
- **Spring Boot** для упрощения разработки.
- **Eclipse Paho MQTT Client** для работы с MQTT-брокером.
- **Gson** для сериализации данных.
- **Dotenv-Java** для парсинга .env файла.

---

## Взаимодействие приложений

1. **SensorDataPublisher** публикует данные в брокер MQTT.
2. **SensorVIew** подписывается на топики MQTT и обрабатывает входящие данные, сохраняя их в базу данных.

---

### Пример использования
1. **SensorDataPublisher** публикует данные в выбранный топик.
2. **SensorView** обрабатывает данные из этого топика и отображает их пользователю.


# Установка
Для работы программы необходима Java 17.

1. Вы можете [скачать Java 17 с официального сайта](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).

2. Далее клонируйте репозиторий командой:
```bash
git clone https://github.com/e-gorobets/sensors-demo.git
```
3. В корневой папке репозитория вы найдете файл *.env.sample*, открывайте его с помощью любого текстового редактора и заполняйте своими данными. На примере MQTT брокера с **Yandex IoT Core** используются следующие данные:

```bash
CERT_FILE=rootCA.crt
```
- Файл [сертификата удостоверяющего центра Yandex IoT Core](https://storage.yandexcloud.net/mqtt/rootCA.crt), сам сертификат необходимо скачать и перенести в папки **SensorDataPublisher** и **SensorsVIew**.
```bash
MQTT_BROKER=ssl://mqtt.cloud.yandex.net:8883
```
- Адрес и порт MQTT сервера.
```bash
USER_NAME= *вставить свой идентификатор*
```
- Идентификатор MQTT брокера с Yandex IoT Core.
```bash
PASSWORD= *вставить свой пароль*
```
- Пароль MQTT брокера с Yandex IoT Core.
```bash
TOPIC=sensors/temperature
```
- Имя MQTT топика для отправки показаний температуры.
```bash
MIN_TEMPERATURE=-10
MAX_TEMPERATURE=200
FIXED_RATE=600000
```
- Нижний и верхний диапазон температуры, а так же частота отправки в милисекундах.

4. После заполнения *.env.sample* переименовывайте его в *.env* и сохраняйте в папки
**SensorDataPublisher** и **SensorsVIew**.

5. В папке  **SensorDataPublisher**  запускайте *gradlew.bat* для сборки.

6. После окончания сборки в папке **SensorDataPublisher** запускайте  *start.bat*

7. В папке  **SensorsVIew**  запускайте *gradlew.bat* для сборки.

8. После окончания сборки в папке  **SensorsVIew**  запускайте  *start.bat*

9. Для перехода к веб-интерфейсу заходите на [localhost:8080](localhost:8080).
