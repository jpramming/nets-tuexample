FROM maven:3-jdk-11 as build

LABEL description="Maven JDK 11 build environment for Nets DanID TU-example"

WORKDIR /opt/tuexample

COPY ./lib/ /opt/tuexample/lib/
COPY ./src/ /opt/tuexample/src/
COPY ./pom.xml /opt/tuexample/pom.xml

RUN chmod +x /opt/tuexample/lib/install-jars.sh \
  && cd /opt/tuexample/lib \
  && ./install-jars.sh \
  && cd /opt/tuexample \
  && mvn clean package

FROM jetty:9-jre11 as runtime

LABEL description="Jetty 9 JRE 11 runtime environment for Nets DanID TU-example"

WORKDIR /var/lib/jetty

COPY --from=build /opt/tuexample/target/pp.war /var/lib/jetty/webapps

