# Web-server-deployment-project


### create one maven project to deploy web server
- find a maven project for web server deployment
    - run maven commands
    - create a gitlab account
- create a docker container out of it
- Create a jenkins pipeline for CI
- Deploy the artifact to the nexus repository
- Deploy it as container on ec2 instance and test it
- Create EKS cluster
- deploy the docker image to kubernetes cluster
- create ingress and access the webserver from outside the kubernetes cluster

So here stage 1 is completed

I have the maven project 

- clone the project with command
    - git clone https://gitlab.com/devops5113843/web-server-deployment-project.git

- Run the following commands
    - mvn clean
    - mvn package

```
$ mvn package
[INFO] Scanning for projects...
[INFO] 
[INFO] -------------------< com.example:web-server-example >-------------------
[INFO] Building web-server-example 1.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- resources:3.3.1:resources (default-resources) @ web-server-example ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory C:\Users\Dell\gitlab\web-server-deployment-project\src\main\resources
[INFO]
[INFO] --- compiler:3.11.0:compile (default-compile) @ web-server-example ---
[INFO] Changes detected - recompiling the module! :source
[WARNING] File encoding has not been set, using platform encoding UTF-8, i.e. build is platform dependent!
[INFO] Compiling 1 source file with javac [debug target 1.8] to target\classes
[WARNING] bootstrap class path not set in conjunction with -source 8
[WARNING] source value 8 is obsolete and will be removed in a future release
[WARNING] target value 8 is obsolete and will be removed in a future release
[WARNING] To suppress warnings about obsolete options, use -Xlint:-options.
[INFO]
[INFO] --- resources:3.3.1:testResources (default-testResources) @ web-server-example ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory C:\Users\Dell\gitlab\web-server-deployment-project\src\test\resources
[INFO]
[INFO] --- compiler:3.11.0:testCompile (default-testCompile) @ web-server-example ---
[INFO] No sources to compile
[INFO]
[INFO] --- surefire:3.1.2:test (default-test) @ web-server-example ---
[INFO] No tests to run.
[INFO]
[INFO] --- jar:3.3.0:jar (default-jar) @ web-server-example ---
[INFO] Building jar: C:\Users\Dell\gitlab\web-server-deployment-project\target\web-server-example-1.0-SNAPSHOT.jar
[INFO] 
[INFO] --- assembly:3.3.0:single (make-assembly) @ web-server-example ---
[INFO] Building jar: C:\Users\Dell\gitlab\web-server-deployment-project\target\web-server-example-1.0-SNAPSHOT-jar-with-dependencies.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  7.050 s
[INFO] Finished at: 2024-01-19T11:10:22+05:30
[INFO] ------------------------------------------------------------------------
```    
```
$ mvn exec:java
[INFO] Scanning for projects...
[INFO]
[INFO] -------------------< com.example:web-server-example >-------------------
[INFO] Building web-server-example 1.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- exec:3.0.0:java (default-cli) @ web-server-example ---
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

We will be able to see this on local machine on following url

http://localhost:8080/hello


How to deploy it via docker in EC2 instance

```
# Use an official OpenJDK runtime as a base image
FROM openjdk:8-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY target/web-server-example-1.0-SNAPSHOT-jar-with-dependencies.jar /app/

# Expose the port the app runs on
EXPOSE 8080

# Command to run your application
CMD ["java", "-jar", "web-server-example-1.0-SNAPSHOT-jar-with-dependencies.jar"]

```

- Build the Docker Image:
    - docker build -t web-server-example .

- Run the Docker Container:
    - docker run -p 8080:8080 web-server-example
