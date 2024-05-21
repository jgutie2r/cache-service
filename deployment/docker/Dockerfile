FROM eclipse-temurin:17.0.11_9-jre-alpine

RUN mkdir /app &&  mkdir /app/libs

COPY cache-*.jar /app/app.jar

COPY /libs/*.jar /app/libs/

WORKDIR /app

CMD ["java","-XX:+UseContainerSupport","-classpath","/app/libs/*:/app/app.jar:.", "edu.uv.cs.cache.main.WebAppCache"]

