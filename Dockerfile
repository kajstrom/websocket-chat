FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/websocket-chat.jar /websocket-chat/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/websocket-chat/app.jar"]
