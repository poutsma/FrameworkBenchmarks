FROM eclipse-temurin:17 as jre-build

# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime

FROM maven:3.8.5-openjdk-17-slim as maven
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime $JAVA_HOME

RUN mvn -version
WORKDIR /spring
COPY src src
COPY pom.xml pom.xml
RUN mvn package -q

FROM debian:bullseye-slim
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime $JAVA_HOME

RUN java -version
WORKDIR /spring
COPY --from=maven /spring/target/spring-fn-benchmark-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-server", "-XX:+UseNUMA", "-XX:+UseG1GC", "-XX:+DisableExplicitGC", "-XX:+UseStringDeduplication", "-Dlogging.level.root=OFF", "-jar", "app.jar", "--spring.profiles.active=jdbc"]
