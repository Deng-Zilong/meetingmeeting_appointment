FROM openjdk:21

LABEL version=<version>

ADD ./meeting_appointment/meeting-appointment-admin/target/<JAR_NAME> /opt/

COPY ./SourceHanSerifSC-Bold.otf /usr/share/fonts/dejavu

RUN fc-cache -f -v 

WORKDIR /opt/

EXPOSE <JAR_PORT>

CMD ["java","<MEMORY_SIZE>","-jar","<JAR_NAME>"]
