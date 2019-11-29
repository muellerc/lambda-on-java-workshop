+++
title = "GraalVM"
weight = 33
pre = ""
+++

## Overview

## What are we doing here?

{{< highlight bash >}}
cd lab-3-rearchitect/graalvm/
sam package --template-file template.yaml  --output-template-file packaged.yaml --s3-bucket ${SAM_ARTIFACT_BUCKET}
sam deploy --template-file packaged.yaml --stack-name lambda-on-java-workshop-lab-3-graalvm --capabilities CAPABILITY_IAM
{{< /highlight >}}
