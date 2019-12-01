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


aws lambda add-permission \
--function-name lambda-function-arn-with-alias-name \ 
--statement-id elb1 \
--principal elasticloadbalancing.amazonaws.com \
--action lambda:InvokeFunction \
--source-arn target-group-arn
```

## Overview

