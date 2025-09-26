# Etapa 1: build da aplicação
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom primeiro para cachear dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte
COPY src ./src

# Gera o JAR da aplicação
RUN mvn clean package -DskipTests

# Etapa 2: runtime
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copia o JAR da etapa anterior
COPY --from=build /app/target/igaming-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]
