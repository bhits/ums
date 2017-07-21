# User Management Service

The User Management Service (UMS) is a component of Consent2Share. It manages the user account creation process, user account activation, user disable function, user update function, and persisting the user demographics. The UMS has been designed to support various roles for given users such as Staff, Parent, Guardian, Patient, and so on. If it is configured to do so, it also registers user demographics (if the user is also a patient) to a Fast Healthcare Interoperability Resources (FHIR) server. The UMS also has a script to create Provider users, who can login to Consent2Share as Providers to create and manage their patients. Please review the [Creating a Provider User](#creating-a-provider-user) section for more information. 


## Build

### Prerequisites

+ [Oracle Java JDK 8 with Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
+ [Docker Engine](https://docs.docker.com/engine/installation/) (for building a Docker image from the project)

### Commands

This is a Maven project and requires [Apache Maven](https://maven.apache.org/) 3.3.3 or greater to build it. It is recommended to use the *Maven Wrapper* scripts provided with this project. *Maven Wrapper* requires an internet connection to download Maven and project dependencies for the very first build.

To build the project, navigate to the folder that contains the `pom.xml` using the terminal/command line.

+ To build a JAR:
    + For Windows, run `mvnw.cmd clean install`
    + For *nix systems, run `mvnw clean install`
+ To build a Docker Image (this will create an image with `bhitsdev/ums:latest` tag):
    + For Windows, run `mvnw.cmd clean install & cd web & ..\mvnw.cmd clean package docker:build & cd..`
    + For *nix systems, run `mvnw clean install; cd ./web; ../mvnw clean package docker:build; cd ..`

## Run

### Prerequisites

This project uses *[MySQL](https://www.mysql.com/)* for persistence and *[Flyway](https://flywaydb.org/)* for database migration. It requires having a database user account with Object and DDL Rights to a schema with the default name `ums`. Please see [Configure](#configure) section for details of configuring the data source. 

[SQL files](https://github.com/bhits-dev/ums/blob/master/ums-db-sample) are provided with this project to populate it with a small set of sample lookup data.

### Commands

This is a [Spring Boot](https://projects.spring.io/spring-boot/) project and serves the project via an embedded Tomcat instance. Therefore, there is no need for a separate application server to run this service.
+ Run as a JAR file: `java -jar ums-x.x.x-SNAPSHOT.jar <additional program arguments>`
+ Run as a Docker Container: `docker run -d bhitsdev/ums:latest <additional program arguments>`

*NOTE: In order for this Service to fully function as a microservice in the Consent2Share application, it is required to setup the dependency microservices and the support level infrastructure. Please refer to the Consent2Share Deployment Guide in the corresponding Consent2Share release (see [Consent2Share Releases Page](https://github.com/bhits-dev/consent2share/releases)) for instructions to setup the Consent2Share infrastructure.*


## Configure

This project utilizes [`Configuration Server`](https://github.com/bhits-dev/config-server) which is based on [Spring Cloud Config](https://github.com/spring-cloud/spring-cloud-config) to manage externalized configuration, which is stored in a `Configuration Data Git Repository`. We provide a [`Default Configuration Data Git Repository`]( https://github.com/bhits-dev/c2s-config-data).

This project can run with the default configuration, which is targeted for a local development environment. Default configuration data comes from three places: `bootstrap.yml`, `application.yml`, and the data which `Configuration Server` reads from `Configuration Data Git Repository`. Both `bootstrap.yml` and `application.yml` files are located in the `resources` folder of this source code.

We **recommend** overriding the configuration as needed in the `Configuration Data Git Repository`, which is used by the `Configuration Server`.

Also, please refer to [Spring Cloud Config Documentation](https://cloud.spring.io/spring-cloud-config/spring-cloud-config.html) to see how the config server works, [Spring Boot Externalized Configuration Documentation](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) to see how Spring Boot applies the order to load the properties, and [Spring Boot Common Properties](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html) documentation to see the common properties used by Spring Boot.

### Other Ways to Override Configuration

#### Override a Configuration Using Program Arguments While Running as a JAR:

+ `java -jar ums-x.x.x-SNAPSHOT.jar --server.port=80 --spring.datasource.password=strongpassword`

#### Override a Configuration Using Program Arguments While Running as a Docker Container:

+ `docker run -d bhitsdev/ums:latest --server.port=80 --spring.datasource.password=strongpassword`

+ In a `docker-compose.yml`, this can be provided as shown below:
```yml
version: '2'
services:
...
  ums.c2s.com:
    image: "bhitsdev/ums:latest"
    command: ["--server.port=80","--spring.datasource.password=strongpassword"]
...
```
*NOTE: Please note that these additional arguments will be appended to the default `ENTRYPOINT` specified in the `Dockerfile` unless the `ENTRYPOINT` is overridden.*

### Enable SSL

For simplicity in development and testing environments, SSL is **NOT** enabled by default configuration. SSL can easily be enabled following the examples below:

#### Enable SSL While Running as a JAR

+ `java -jar ums-x.x.x-SNAPSHOT.jar --spring.profiles.active=ssl --server.ssl.key-store=/path/to/ssl_keystore.keystore --server.ssl.key-store-password=strongkeystorepassword`

#### Enable SSL While Running as a Docker Container

+ `docker run -d -v "/path/on/dockerhost/ssl_keystore.keystore:/path/to/ssl_keystore.keystore" bhitsdev/ums:latest --spring.profiles.active=ssl --server.ssl.key-store=/path/to/ssl_keystore.keystore --server.ssl.key-store-password=strongkeystorepassword`
+ In a `docker-compose.yml`, this can be provided as follows:
```yml
version: '2'
services:
...
  ums.c2s.com:
    image: "bhitsdev/ums:latest"
    command: ["--spring.profiles.active=ssl","--server.ssl.key-store=/path/to/ssl_keystore.keystore", "--server.ssl.key-store-password=strongkeystorepassword"]
    volumes:
      - /path/on/dockerhost/ssl_keystore.keystore:/path/to/ssl_keystore.keystore
...
```

*NOTE: As seen in the examples above, `/path/to/ssl_keystore.keystore` is made available to the container via a volume mounted from the Docker host running this container.*

### Override Java CA Certificates Store In Docker Environment

Java has a default CA Certificates Store that allows it to trust well-known certificate authorities. For development and testing purposes, one might want to trust additional self-signed certificates. In order to override the default Java CA Certificates Store in a Docker container, one can mount a custom `cacerts` file over the default one in the Docker image as follows: `docker run -d -v "/path/on/dockerhost/to/custom/cacerts:/etc/ssl/certs/java/cacerts" bhitsdev/ums:latest`

*NOTE: The `cacerts` references given in the volume mapping above are files, not directories.*

[//]: # (## Project Documentation)
[//]: # (## Contribute)


### Creating a Provider User

This project comes with a [script](scripts/create_activate_provider_user.sh) to create users who can login to Consent2Share as Providers. Before running the script, make sure to have an instance of UMS, [Discovery Server](https://github.com/bhits/discovery-server), [Edge Server](https://github.com/bhits-dev/edge-server) and [UAA](https://github.com/bhits-dev/uaa) up and running. Run the script and enter the requested information, including First Name, Last Name, DOB, and so on. Wait for the message "Is the Provider User Account Activated?: True" to verify that the user has been successfully created.

## Contact

If you have any questions, comments, or concerns please see [Consent2Share](https://bhits-dev.github.io/consent2share/) project site.

## Report Issues

Please use [GitHub Issues](https://github.com/bhits-dev/ums/issues) page to report issues.

[//]: # (License)
