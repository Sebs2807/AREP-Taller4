FROM openjdk:21

WORKDIR /usrapp/bin

ENV PORT 6000

COPY target/classes ./classes
COPY target/dependency ./dependency

CMD ["java","-cp","./classes:./dependency/*","eci.escuelaing.edu.co.RestServiceApplication"]
