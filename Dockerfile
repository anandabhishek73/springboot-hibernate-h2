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

# Run Stage
FROM openjdk:8
COPY --from=maven /home/app/target/demo-*.jar /usr/local/lib/demo.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]
