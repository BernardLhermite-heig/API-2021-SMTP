FROM openjdk:11

ARG filename
ENV filename=${filename}

COPY $filename /usr/src/$filename
WORKDIR /usr/src/

# Permet d'obtenir un mapping automatique en lançant le container avec le flag -P
# Les ports peuvent s'obtenir avec docker ps ou docker port <container id>
EXPOSE 25
EXPOSE 8282

CMD java -jar $filename