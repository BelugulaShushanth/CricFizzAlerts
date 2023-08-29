FROM amazoncorretto:11-alpine3.17-jdk
COPY /secure-connect-cricfizz-alerts-db.zip /opt/apps/secure-connect-cricfizz-alerts-db.zip
COPY build/libs/CricFizzAlerts-0.0.1-SNAPSHOT.jar /opt/apps/CricFizzAlerts-0.0.1-SNAPSHOT.jar
WORKDIR /opt/apps
ENTRYPOINT ["java", "-jar", "CricFizzAlerts-0.0.1-SNAPSHOT.jar"]