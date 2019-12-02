FROM amazonlinux:2

# Install dependencies and clear cache
RUN yum update -y \
    && yum install -y java-1.8.0 \
    && yum clean all \
    && rm -rf /var/cache/yum

# Provision the Proxy
COPY target/app.jar /app/app.jar
WORKDIR /app

# Run the Proxy
ENTRYPOINT ["java"]
CMD ["-jar", "app.jar"]
