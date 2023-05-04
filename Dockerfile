FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1

RUN apt-get update && apt-get install -y sbt libxrender1 libxtst6 libxi6

EXPOSE 8082

WORKDIR /SA-UNO

ADD . /uno

CMD sbt run
