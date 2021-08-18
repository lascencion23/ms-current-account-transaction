FROM openjdk:11.0.7
ARG JAR_FILE=target/ms-current-account-transaction-*.jar

ENV JAVA_OPTS="-Xms64m -Xmx256m"

COPY ${JAR_FILE} ms-current-account-transaction.jar

ENTRYPOINT java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -server -jar ms-current-account-transaction.jar