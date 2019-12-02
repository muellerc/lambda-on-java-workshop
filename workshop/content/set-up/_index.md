+++
title = "Set-Up"
weight = 2
pre = ""
+++

{{% notice info %}}
The following CloudFormation template should be executed by an IAM user with Administrator privileges,
not the root account user. Please ensure you are logged in as an IAM user, not the root
account user.
{{% /notice %}}

## Set-up the environment

We will leverage **[AWS CloudFormation](https://aws.amazon.com/cloudformation/)** which allows us to codify our infrastructure. In addition, we use **[AWS SAM](https://aws.amazon.com/serverless/sam/)** to build serverless applications in simple and clean syntax.  

### 1. Execute the AWS CloudFormation stack in your closest region

{{< tabs name="Region" >}}
{{< tab name="Frankfurt" include="eu-central-1" />}}
{{< tab name="Ireland" include="eu-west-1" />}}
{{< tab name="Oregon" include="us-west-2" />}}
{{< /tabs >}}

### 2. Launch the AWS CloudFormation stack

Acknowledge that AWS CloudFormation might create IAM resources and click the **Create Stack** button to launch the template.

### 3. Wait until the AWS CloudFormation stack launched

It takes usually less than 2 minutes until the stack launched. When the stack is launched, the status will change from **CREATE_IN_PROGRESS** to **CREATE_COMPLETE**.

### 4. Browse to your AWS Cloud9 development environment

{{% notice note %}}
Ad blockers, javascript disabler, and tracking blockers should be disabled for
the cloud9 domain, or connecting to the workspace might be impacted.
Cloud9 requires third-party-cookies. You can whitelist the [specific domains]( https://docs.aws.amazon.com/cloud9/latest/user-guide/troubleshooting.html#troubleshooting-env-loading).
{{% /notice %}}

The **Outputs** tab in your CloudFormation console exposes the **Cloud9DevEnvUrl** parameter. Click at the corresponding URL in the value column and open your AWS Cloud9 development environment in a new tab.

### 5. Configure your AWS Cloud9 development environment

In your AWS Cloud9 IDE, you can close the welcome tab. In the left environment navigation window, you can see the project **lambda-on-java-workshop** we have already checked out for you from Github. This project also contains a shell script to update the OS and to install additional tools like GraalVM, Scala and Gatling. Run the following command in the bash window at the bottom of the AWS Cloud9 IDE:

{{< highlight bash >}}
chmod +x ~/environment/lambda-on-java-workshop/labs/set-up/configureCloud9.sh 
~/environment/lambda-on-java-workshop/labs/set-up/configureCloud9.sh
source ~/.bashrc
{{< /highlight >}}

It takes usually ~ 3 minutes, until the OS is updated and all tools are installed.

As we want to compare our results with an equivalent implementation in Python, let's build the Python 3.6 based service first by running the following command in in the bash window in AWS Cloud9:

```bash
cd ~/environment/lambda-on-java-workshop/labs/lab-1-replatform/python-lambda
sam build
```

Now we continue with building all Java based service implementations, by running the following command in the AWS Cloud9 IDE bash window:

{{< highlight bash >}}
cd ~/environment/lambda-on-java-workshop/labs
chmod +x ./mvnw
./mvnw clean package
{{< /highlight >}}

It takes usually ~ 12 minutes, until all projects are build and packaged.
