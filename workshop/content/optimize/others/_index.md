+++
title = "Other Optimizations"
weight = 29
pre = ""
+++

## Overview

There are some other optimizations we considered, but couldn't measure an improvement at all with our load test scenario. These optimizations includes:

+ **Using the [Amazon Corretto Crypto Provider](https://aws.amazon.com/blogs/opensource/introducing-amazon-corretto-crypto-provider-accp/) as the Java Cryptography Architecture (JCA) of choice**
+ **Upgrading you AWS Lambda runtime to [Amazon Corretto 11](https://aws.amazon.com/about-aws/whats-new/2019/11/aws-lambda-supports-java-11/)**
