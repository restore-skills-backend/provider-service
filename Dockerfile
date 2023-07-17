FROM openjdk:17
 ENV SPRING_PROFILES_ACTIVE=dev
 ADD target/provider-service.jar provider-service.jar
 EXPOSE 8081
ENTRYPOINT ["java","-jar","provider-service.jar"]