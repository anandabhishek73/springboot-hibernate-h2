## Build stage
FROM maven:3.6.0-jdk-8-slim AS maven
# Mount a reusable .m2 as a volume
VOLUME ["maven-repo:/root/.m2"]
# Add project maven build requirements
COPY ./pom.xml /home/app/pom.xml
# build all dependencies
WORKDIR /home/app
RUN mvn dependency:go-offline -B

## Package stage
# copy your other source files
COPY ./src /home/app/src
RUN mvn install

## Run Stage
# Thin JRE base image for reduced final image size
FROM openjdk:8-jre-alpine
# Copy sping boot deployable jar from build stage
COPY --from=maven /home/app/target/demo-*.jar /usr/local/lib/demo.jar
# Set Runtime
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar","/usr/local/lib/demo.jar"]
