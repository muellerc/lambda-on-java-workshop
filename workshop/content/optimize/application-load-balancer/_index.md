+++
title = "Application Load Balancer"
weight = 31
pre = ""
+++

```bash
aws elbv2 create-target-group \
  --name PetsApiTargetGroup \
  --protocol https \
  --port 443 \
  --target-type lambda

aws elbv2 register-targets \
  --target-group-arn arn:aws:elasticloadbalancing:us-west-2:123456789012:targetgroup/my-tcp-ip-targets/8518e899d173178f \
  --targets Id=arn:aws:lambda:us-west-2:123456789012:function:my-function

aws lambda add-permission \
--function-name lambda-function-arn-with-alias-name \ 
--statement-id elb1 \
--principal elasticloadbalancing.amazonaws.com \
--action lambda:InvokeFunction \
--source-arn target-group-arn
```

## Overview

