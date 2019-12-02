+++
title = "Replatform"
weight = 10
pre = "Lab 1: "
+++

## Overview

In this section, we first determine our performance baseline for this service, so that we have the necessary data to know, whether and how we improved the performance of this service in a particular implementation.

As baseline, we will measure the performance for the service implementation which is running behind an **[Application Load Balancer](https://aws.amazon.com/elasticloadbalancing/)** in **[AWS Fargate](https://aws.amazon.com/fargate/)**.

As the session description is "_...make cold-start times even faster than on Node.js- and Python-based functions. .._", we will also measure the performance for a service implementation based on pure Python 3.6, Boto3 and AWS Lambda.

As next steps, we will migrate this Springboot 2 based service to Amazon API Gateway and AWS Lambda and iterate over various improvements.
