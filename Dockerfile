FROM ingensi/oracle-jdk:centos6-latest
MAINTAINER Spoonito <brunomonteiroalmeida@gmail.com>

RUN yum update -y && yum install -y unzip
RUN curl -O http://downloads.typesafe.com/play/2.2.6/play-2.2.6.zip
RUN unzip play-2.2.6.zip -d / && rm play-2.2.6.zip && chmod a+x /play-2.2.6/play
ENV PATH $PATH:/play-2.2.6

EXPOSE 9001
WORKDIR /app
COPY build.sbt ./
RUN play help
COPY ./ ./

CMD ["play", "start -Dshmup.password=xedy4bsa -Dhttp.port=9001"]