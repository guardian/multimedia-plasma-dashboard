#!/bin/bash -e

cd /tmp
wget https://sbt-downloads.cdnedge.bluemix.net/releases/v1.0.2/sbt-1.0.2.tgz
cd /usr/share
tar xvzf /tmp/sbt-1.0.2.tgz
rm -rf /tmp/sbt-1.0.2.tgz