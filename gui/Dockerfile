#FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1
#
#RUN apt-get update
#
#EXPOSE 8080
#
#WORKDIR /core
#
#ADD . /core
#
#CMD sbt run
FROM uno-gui:1.0
RUN apt-get update && apt-get install --no-install-recommends -y libxext6
