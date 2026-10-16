# Stage 1: Build da aplicação com Maven e Java 25
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app

# Copia o pom.xml e baixa dependências para aproveitar cache de camadas
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copia o código-fonte e gera o pacote JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Imagem final de execução com JRE 25
FROM eclipse-temurin:25-jre
WORKDIR /app

# Copia o artefato compilado
COPY --from=build /app/target/*.jar app.jar

# Variável de porta (Render/Railway injetam a variável PORT)
ENV PORT=8080
EXPOSE 8080

# Execução da aplicação
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
