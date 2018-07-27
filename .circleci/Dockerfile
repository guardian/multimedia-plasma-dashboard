FROM alpine:latest

RUN apk update && apk add --no-cache libstdc++ openjdk8-jre git dpkg wget bash && rm -rf /var/cache/apk/
COPY setup-sbt.sh /tmp/setup-sbt.sh
RUN bash /tmp/setup-sbt.sh && rm -f /tmp/setup-sbt.sh
RUN adduser -D ci -s "/bin/bash --login"

USER ci
WORKDIR /home/ci
RUN echo 'declare -x PATH=$PATH:/usr/share/sbt/bin' >> /home/ci/.bash_profile
RUN echo 'export PS1="\u@\h:\W \$ "' >> /home/ci/.bashrc

CMD /bin/bash --login

##bootstrap sbt jars
RUN cd ~; bash --login -c 'echo exit | /usr/share/sbt/bin/sbt'

