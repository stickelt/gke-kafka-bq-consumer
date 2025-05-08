FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . /app
RUN ./mvnw package
CMD ["java", "-cp", "target/kafka-bq-consumer-1.0-SNAPSHOT.jar", "com.example.KafkaToBQ"]
