## Overview
A foundational set of web services that implement industry standard guidelines, common best practices, and the experienced insights afforded to Lighthouse Software thru decades of enterprise business software development. 

Created and supported by Lighthouse Software @ https://LighthouseSoftware.com

## Topics
* [5 Minute Test](#5-minute-test)
* [Features](#features)
* [Security](readme_docs/SECURITY.md)
* [Development](readme_docs/DEVELOPMENT.md)
* [Development Recipes](readme_docs/DEVELOPMENT-RECIPES.md)
* [Testing](readme_docs/STANDARDS-TESTING.md)
* [Deploy](readme_docs/DEPLOY.md)

## 5 Minute Test
Run the Voyage API and execute a JSON API request within 5 minutes

1. Download the source locally
   * Ensure you have Git installed locally or within your IDE
   * This example will assume you are at a terminal console command prompt.
   * NOTE: If you choose to use your own IDE, then follow the git clone instructions for your particular IDE
   
   `git clone https://github.com/lssinc/voyage-api-java.git`
   
2. Start the app
   * Gradle build system is used to build, test, and run the application
   * Using the "gradew" commands in the root of the project will automatically download and install a local version of Gradle

   Linux/OSX: `./gradlew bootRun`
   Windows: `gradlew.bat` 

3. Verify the app is running
   * Once the app is done starting up and loading the in-memory database with seed data, the following log statement will display in the console:
     
     `INFO 23204 --- [           main] voyage.App                               : Started App in 27.519 seconds (JVM running for 28.708)`   
     
   * Open a browser and access the general "status" web service at [http://localhost:8080/api/status](http://localhost:8080/api/status). A JSON response should appear like the following:
     
     `{"status":"alive","datetime":"2018-05-15T13:52:12-05:00"}` 

4. Get an OAuth2 Authorization token to execute requests on the API
   * On the terminal, run the following cURL command (OSX, Linux already has cURL. [Download for Windows](https://curl.haxx.se/download.html))
     
     ```curl -u client-super:secret -X POST -d "client_id=client-super&client_secret=secret&grant_type=client_credentials" http://localhost:8080/oauth/token``` 
     
   * The return should be a JSON result like the following:
     ```{"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjcmVhdGVkIjoxNTI3NDczNDQ3ODQzLCJzY29wZSI6WyJSZWFkIERhdGEiLCJXcml0ZSBEYXRhIl0sImV4cCI6MTUyNzQ4MDY0NywiYXV0aG9yaXRpZXMiOlsiYXBpLnBlcm1pc3Npb25zLmRlbGV0ZSIsImFwaS5yb2xlcy5kZWxldGUiLCJhcGkucm9sZXMudXBkYXRlIiwiYXBpLnBlcm1pc3Npb25zLmxpc3QiLCJhcGkucGVybWlzc2lvbnMudXBkYXRlIiwiYXBpLnVzZXJzLmNyZWF0ZSIsImFwaS51c2Vycy5nZXQiLCJhcGkudXNlcnMubGlzdCIsImFwaS5wZXJtaXNzaW9ucy5nZXQiLCJhcGkucm9sZXMuZ2V0IiwiYXBpLnVzZXJzLnVwZGF0ZSIsImFwaS5wcm9maWxlcy5tZSIsImFwaS5yb2xlcy5jcmVhdGUiLCJhcGkudXNlcnMuZGVsZXRlIiwiYXBpLnBlcm1pc3Npb25zLmNyZWF0ZSIsImFwaS5yb2xlcy5saXN0Il0sImp0aSI6Ijc3YTAwOGY0LTA4MjAtNDYxOS1hNTBhLWFjYjExMjI0ODUzYSIsImNsaWVudF9pZCI6ImNsaWVudC1zdXBlciJ9.QyIgq2HmFDXUT-nKLXJLudA3yQP0bz_wi6Ru9lPizs4UbPOawxlLHQauapsdpN2yXd611pYILRUwGP9F8C_H-fJ28uBs7bRpCUN8dslwHovk-1K8Gs5wjyuibQ-YZQqfi_LxsE7-cNS2el7j86Pqfw_RAFnsr4A9wRi9oAu8tSJ611PGBSUimMssZRULzcbh7zHUbPhDHOPY2NR2YapETclyPQap5qmFcqJE1BpGt0ZtdpUb8qX_Rjw7Y8lHxjg5ux583sdZFlENZF1hf1SvJw0XaNgs2f9R-n0F8Y4zBJwqEA-8-OdrLFBdmYUpwR5xA83ohoZNi1QEhr-UedViBg",
       "token_type":"bearer",
       "expires_in":7199,
       "scope":"Read Data Write Data",
       "created":1527473447843,
       "jti":"77a008f4-0820-4619-a50a-acb11224853a"}```
       
   * Copy the "access_token" value from the JSON response
     ```eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjcmVhdGVkIjoxNTI3NDczNDQ3ODQzLCJzY29wZSI6WyJSZWFkIERhdGEiLCJXcml0ZSBEYXRhIl0sImV4cCI6MTUyNzQ4MDY0NywiYXV0aG9yaXRpZXMiOlsiYXBpLnBlcm1pc3Npb25zLmRlbGV0ZSIsImFwaS5yb2xlcy5kZWxldGUiLCJhcGkucm9sZXMudXBkYXRlIiwiYXBpLnBlcm1pc3Npb25zLmxpc3QiLCJhcGkucGVybWlzc2lvbnMudXBkYXRlIiwiYXBpLnVzZXJzLmNyZWF0ZSIsImFwaS51c2Vycy5nZXQiLCJhcGkudXNlcnMubGlzdCIsImFwaS5wZXJtaXNzaW9ucy5nZXQiLCJhcGkucm9sZXMuZ2V0IiwiYXBpLnVzZXJzLnVwZGF0ZSIsImFwaS5wcm9maWxlcy5tZSIsImFwaS5yb2xlcy5jcmVhdGUiLCJhcGkudXNlcnMuZGVsZXRlIiwiYXBpLnBlcm1pc3Npb25zLmNyZWF0ZSIsImFwaS5yb2xlcy5saXN0Il0sImp0aSI6Ijc3YTAwOGY0LTA4MjAtNDYxOS1hNTBhLWFjYjExMjI0ODUzYSIsImNsaWVudF9pZCI6ImNsaWVudC1zdXBlciJ9.QyIgq2HmFDXUT-nKLXJLudA3yQP0bz_wi6Ru9lPizs4UbPOawxlLHQauapsdpN2yXd611pYILRUwGP9F8C_H-fJ28uBs7bRpCUN8dslwHovk-1K8Gs5wjyuibQ-YZQqfi_LxsE7-cNS2el7j86Pqfw_RAFnsr4A9wRi9oAu8tSJ611PGBSUimMssZRULzcbh7zHUbPhDHOPY2NR2YapETclyPQap5qmFcqJE1BpGt0ZtdpUb8qX_Rjw7Y8lHxjg5ux583sdZFlENZF1hf1SvJw0XaNgs2f9R-n0F8Y4zBJwqEA-8-OdrLFBdmYUpwR5xA83ohoZNi1QEhr-UedViBg```
     
5. Execute a GET request for a list of users
   * cURL command: 
   
   ```curl --header "Authorization: Bearer PUT_ACCESS_TOKEN_HERE" http://localhost:8080/api/v1/users```
   
   * cURL example: 
   
   ```curl --header "Authorization: Bearer eyJhbGciOiJSUzI1NiIsIImV4cCI6MTUyNzQ4MDY0NywiYXV0aG9yaXRpZXMiOlsiYXBpLnBlcm1pc3Npb25zLmRlbGV0ZSIsImFwaS5yb2xlcy5kZWxldGUiLCJhcGkucm9sZXMudXBkYXRlIiwiYXBpLnBlcm1pc3Npb25zLmxpc3QiLCJhcGkucGVybWlzc2lvbnMudXBkYXRlIiwiYXBpLnVzZXJzLmNyZWF0ZSIsImFwaS51c2Vycy5nZXQiLCJhcGkudXNlcnMubGlzdCIsImFwaS5wZXJtaXNzaW9ucy5nZXQiLCJhcGkucm9sZXMuZ2V0IiwiYXBpLnVzZXJzLnVwZGF0ZSIsImFwaS5wcm9maWxlcy5tZSIsImFwaS5yb2xlcy5jcmVhdGUiLCJhcGkudXNlcnMuZGVsZXRlIiwiYXBpLnBlcm1pc3Npb25zLmNyZWF0ZSIsImFwaS5yb2xlcy5saXN0Il0sImp0aSI6Ijc3YTAwOGY0LTA4MjAtNDYxOS1hNTBhLWFjYjExMjI0ODUzYSIsImNsaWVudF9pZCI6ImNsaWVudC1zdXBlciJ9.QyIgq2HmFDXUT-nKLXJLudA3yQP0bz_wi6Ru9lPizs4UbPOawxlLHQauapsdpN2yXd611pYILRUwGP9F8C_H-fJ28uBs7bRpCUN8dslwHovk-1K8Gs5wjyuibQ-YZQqfi_LxsE7-cNS2el7j86Pqfw_RAFnsr4A9wRi9oAu8tSJ611PGBSUimMssZRULzcbh7zHUbPhDHOPY2NR2YapETclyPQap5qmFcqJE1BpGt0ZtdpUb8qX_Rjw7Y8lHxjg5ux583sdZFlENZF1hf1SvJw0XaNgs2f9R-n0F8Y4zBJwqEA-8-OdrLFBdmYUpwR5xA83ohoZNi1QEhr-UedViBg" http://localhost:8080/api/v1/users``` 

6. View API documentation
   * http://localhost:8080/api/docs  

7. Read [Developer Documentation](readme_docs/DEVELOPMENT.md) for more detailed instructions

## Features

### Web Services
* __HTTP Compliant RESTful API__
  - Follows HTTP protocols for RESTful web services
  - Lightweight JSON requests and responses
  - See our [Web Service Standards](readme_docs/STANDARDS-WEB-SERVICES.md)
* __Public API Status Service__
  - Web service that provides general status of the API to the public
  - Helpful endpiont for automated monitoring
* __User Administration Services__
  - Full suite of user administration web services (list, get, create, update, delete)
  - Secured access through role based security
* __Account Management Services__
  - Users can update their account information themselves
  - Manage account settings
  - Password reset
* __API Documentation__
  - Complete documentation for web services consumers
  - Includes detailed descriptions and example to quickly interact with the API

### Security
* __[OWASP](https://www.owasp.org/index.php/Category:OWASP_Top_Ten_Project) Hacker Proof__
  - Tested nightly against OWASP common hacks (particularly the top 10)
  - Tested nightly using 3rd party penetration testing services to ensure entperprise grade security!
* __[OAuth2](https://oauth.net/2/) Authentication__
  - Bearer Token authentication configuraiton
  - SHA2 hash encrypted user password (when authenticating using the database)
  - Supports other authentication methods
* __Active Directory / LDAP Authentication__
  - Extends OAuth2 to support authentication with an AD/LDAP system
  - Supports Enterprise SSO environments using AD/LDAP
* __Role Based Authorization__
  - Custom role definitions to suit any situation
  - Supports granular security permissions 
  - Full suite of role administration web services (list, get, create, update, delete)
* __Forgot Username / Password Support__
  - Web services that allow users to reset their username and/or password
  - Validates a user via their email address
* __Auditing__
  - Complete enterprise access and data auditing to meet compliance requirements
  - HTTP Request / Response logging to track user activity (anonymous and authenticated users)
  - Database change logging to track manipulation of data over time (anonymous and authenticated users)

### Tech Stack
* __JSON RESTful Web Services__
  - JSON request/response interaction
  - Strict [REST implementation](readme_docs/STANDARDS-WEB-SERVICES.md)
  - [apiDoc](http://apidocjs.com) documentation generated from source code comments
* __Spring Boot__
  - Spring MVC / REST
  - Groovy
  - Spring Security
  - Hibernate
  - (auditing, logging, ...)
* __Database Neutral__
  - Capable of integrating with any major database vendor (SQL Server, Oracle, DB2, MySQL, etc)
  - Database interactions follow [SQL99](https://en.wikipedia.org/wiki/SQL:1999) standards with no vendor specific database features relied upon
  - Liquibase database migrations produce on-demand SQL specific to the integrated database
* __Integrated Test Suite__
  - Automated test coverage using Spock testing framework
  - Tests executed during every build to ensure high quality code coverage
* __Continuous Integration (CI)__
  - Jenkins CI jobs able to invoke Gradle and apiDoc commands to build, test, and package
  - Jenkins jobs included with API source
  - Supports other CI environments like Team Foundation Server (TFS)

### Developers
* __Team Protocols__
  - Fast learning curve through clear documentation
  - Easy values, standards, best practices that most developers will agree to follow
* __Core Values__
  - Documented core values that we believe will resonate with most development teams
  - Unifies teams and promotes healthy communication
  - See our [Core Values](readme_docs/DEVELOPMENT.md#core-values) documentation
* __Coding Standards__ 
  - Industry accepted language coding standards
  - Best practices when developing within the code base
  - Standard enforced using static code analysis at build time (CodeNarc)
  - See our [Development Team Standards](#development-team-standards)
  
### System Administrators
* __Deploy Instructions__
  - Full instructions on how to properly build, test, and package the API app for deploy
  - Continuous Integration job templates for QA, UAT, and PROD
* __Docker Support__
  - Preconfigured Dockerfile for deployment within Amazon Web Services environment
  - Generate a Docker bundle for distribution using built-in tasks
  - Customize to fit any environment
* __Amazon Web Services (AWS) - Elastic Beanstalk__
  - Supports AWS Elastic Beanstalk using a Docker image
  - Run a build task to generate an AWS EB compatible .zip file
* __API Monitoring__
  - Configure automated web uptime monitoring to use the Status Web Service
* __DevOps Ready__
  - [Ansible](https://www.ansible.com) scripts for deploying the API Docker image to the Amazon Web Service (AWS) environment
  - Customize scripts to support any environment
