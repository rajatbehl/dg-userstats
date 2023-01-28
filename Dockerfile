FROM openjdk:11
RUN rm -f /etc/localtime && ln -sf /usr/share/zoneinfo/Asia/Kolkata /etc/localtime && echo "Asia/Kolkata" > /etc/timezone
RUN apt-get update && apt-get install vim -y
WORKDIR /tmp
COPY ./target/userstats-1.0.jar userstats-1.0.jar 
COPY ./src/main/resources/application.yaml application.yaml
COPY ./src/main/resources/logback-spring.xml logback-spring.xml
ENTRYPOINT [ "java", "-jar", "userstats-1.0.jar"]
