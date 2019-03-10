FROM openjdk:8-jre-stretch
WORKDIR /usr/local/bin/
ADD ./dist/ChineseCharacterPracticeSheets.jar ChineseCharacterPracticeSheets.jar
ADD ./dist/SimpleWebServer.jar SimpleWebServer.jar
ADD ./font.otf /usr/share/fonts/
RUN apt install -y fontconfig
RUN fc-cache -f -v
EXPOSE 4001
CMD [ "java" , "-jar" , "/usr/local/bin/ChineseCharacterPracticeSheets.jar" ]

