#!/bin/bash

set -euo pipefail

# update all installed packages
echo "update all installed packages"
sudo yum update -y

# remove Java 7
echo "remove Java 7"
sudo yum remove -y java-1.7.0
sudo yum clean all
sudo rm -rf /var/cache/yum

# update to the latest version of SAM CLI (and AWS CLI and PIP3)
echo "update to the latest version of SAM CLI (and AWS CLI and PIP3)"
sudo pip uninstall aws-sam-cli -y
sudo pip uninstall awscli -y
sudo python -m pip uninstall pip -y

sudo alternatives --set python /usr/bin/python3.6

curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
sudo python get-pip.py

sudo /usr/local/bin/pip install awscli
sudo /usr/local/bin/pip install aws-sam-cli
sudo /usr/local/bin/pip install cfn-lint

# Install GraalVM 19.3.0
echo "Install GraalVM 19.3.0"
#wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-19.3.0/graalvm-ce-java8-linux-amd64-19.3.0.tar.gz
wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-19.3.0/graalvm-ce-java11-linux-amd64-19.3.0.tar.gz
tar -xzvf graalvm-ce-java11-linux-amd64-19.3.0.tar.gz
rm -rf graalvm-ce-java11-linux-amd64-19.3.0.tar.gz
echo 'export GRAALVM_HOME=/home/ec2-user/environment/graalvm-ce-java11-19.3.0' >> ~/.bashrc
echo 'export JAVA_HOME=/home/ec2-user/environment/graalvm-ce-java11-19.3.0' >> ~/.bashrc
echo 'export PATH="$PATH:$GRAALVM_HOME/bin"' >> ~/.bashrc
/home/ec2-user/environment/graalvm-ce-java11-19.3.0/bin/gu install native-image

# Install Scala 2.13.1
echo "Install Scala 2.13.1"
wget http://downloads.typesafe.com/scala/2.13.1/scala-2.13.1.tgz
tar -xzvf scala-2.13.1.tgz
rm -rf scala-2.13.1.tgz
echo 'export SCALA_HOME=/home/ec2-user/environment/scala-2.13.1' >> ~/.bashrc
echo 'export PATH="$PATH:$SCALA_HOME/bin"' >> ~/.bashrc

# Install Gatling 3.3.1
echo "Install Gatling 3.3.1"
wget https://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts-bundle/3.3.1/gatling-charts-highcharts-bundle-3.3.1-bundle.zip
unzip gatling-charts-highcharts-bundle-3.3.1-bundle.zip
rm -rf gatling-charts-highcharts-bundle-3.3.1-bundle.zip
echo 'export GATLING_HOME=/home/ec2-user/environment/gatling-charts-highcharts-bundle-3.3.1' >> ~/.bashrc
echo 'export PATH="$PATH:$GATLING_HOME/bin"' >> ~/.bashrc

# Make the SAM artifact bucket available as ENV variable
echo "Make the SAM artifact bucket available as ENV variable"
echo "export SAM_ARTIFACT_BUCKET=$(aws cloudformation describe-stacks \
    --stack-name lambda-java-workshop \
    --query 'Stacks[].Outputs[?OutputKey==`SAMArtifactBucket`].OutputValue' \
    --output text)" >> ~/.bashrc

echo "Make the pet bucket available as ENV variable"
echo "export PETS_BUCKET=$(aws cloudformation describe-stacks \
    --stack-name lambda-java-workshop \
    --query 'Stacks[].Outputs[?OutputKey==`PetsBucket`].OutputValue' \
    --output text)" >> ~/.bashrc

echo "Make the pet table available as ENV variable"
echo "export PETS_TABLE=$(aws cloudformation describe-stacks \
    --stack-name lambda-java-workshop \
    --query 'Stacks[].Outputs[?OutputKey==`PetsTable`].OutputValue' \
    --output text)" >> ~/.bashrc
