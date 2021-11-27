FROM openjdk:11
EXPOSE 8080
ADD ./build/libs/spring-gumball-v3-0.0.1-SNAPSHOT.jar /srv/spring-gumball-v3-0.0.1-SNAPSHOT.jar
CMD java -jar /srv/spring-gumball-v3-0.0.1-SNAPSHOT.jar