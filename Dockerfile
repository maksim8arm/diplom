FROM openjdk:11
ADD /target/test_diplom-0.0.1-SNAPSHOT.jar backend.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/backend.jar"]

