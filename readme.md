# Для запуска необходимо:
1. JDK >= 17 (Должно быть указано в %JAVA_HOME%)
2. JRE >= 17 (Должно быть указано в %JRE_HOME% либо java.exe в PATH)
3. PostgreSQL база данных. В application.properties данные подключения (по умолчанию используется база данных postgres, хост localhost, порт 5432, пользователь postgres с паролем postgres)
# Инструкция запуска
1. запустить терминал в корневой папке проекта (simbir)
2. Использовать следующие команды:

1. /gradlew build
2. cd build/libs/
3. java -jar simbir-0.0.1-SNAPSHOT.jar
## URL: http://localhost:8080/swagger-ui/index.html
