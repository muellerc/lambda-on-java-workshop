+++
title = "Optimize"
weight = 20
pre = "Lab 2: "
+++

## Overview

## What are we doing here?

Take the no DI framework measurements as baseline and see what else we can improve in terms of latency, cold-start and memory consumption.

## AWS Lambda Power Tuning

Our goal is to run our AWS Lambda function with a performance optimized configuration, taking cost and scaling into account as well. We are using **[AWS Lambda Power Tuning](https://github.com/alexcasalboni/aws-lambda-power-tuning)** to find the optimal configuration.

The easiest way is to install it via the **[AWS Serverless Application Repository](https://aws.amazon.com/serverless/serverlessrepo/)** by clicking at this **[link](https://serverlessrepo.aws.amazon.com/applications/arn:aws:serverlessrepo:us-east-1:451282441545:applications~aws-lambda-power-tuning)**.

A sample visualization with recommendations looks like this:

![AWS Lambda Power Tuning](optimize/aws-lambda-power-tuning.png)