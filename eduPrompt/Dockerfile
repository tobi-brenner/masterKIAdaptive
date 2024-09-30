FROM quay.io/quarkus/quarkus-micro-image:2.7.6.Final
WORKDIR /work/
COPY target/*-runner.jar /work/application.jar
CMD ["java", "-jar", "application.jar"]
