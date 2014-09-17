Sample Cloud Foundry Service Dashboard
=================

This demo illustrates how we can develop a dashboard for a Cloud Foundry service. The dashboard
interacts with 2 main Cloud Foundry components: the UAA and the Cloud Controller. The UAA is used to
authenticate the user. The authentication is based on OAUTH (SSO mechanism). The Cloud Controller
(in the scope of a dashboard) is used to to check that the current user can access the current dashboard
instance.

More information:
* [Cloud Foundry UAA doc](http://docs.cloudfoundry.org/concepts/architecture/uaa.html)
* [Cloud Foundry Dashboard SSO doc](http://docs.cloudfoundry.org/services/dashboard-sso.html)
* [Cloud Foundry Controller doc](http://docs.cloudfoundry.org/concepts/architecture/cloud-controller.html)

In order to test the sample dashboard these 2 components must be running up. There are 2 options. You
have a running up Cloud Foundry instance. Or, you download the UAA at the following address:
[Cloud Foundry UAA](https://github.com/cloudfoundry/uaa) and you use the
stub Cloud Controller in the sub-project *com.bsb.showcase.cf.dashboard.controller*.


com.bsb.showcase.cf.dashboard.controller
---------------------

This module provides a stub implementation of the controller that always returns *true*. If you change the code
to return *false*, you will always get an access denied exception when accessing the dashboard.

The controller can be started by doing:

```
mvn clean spring-boot:run
```

or by doing:

```
mvn clean install
java -jar target/com.bsb.showcase.cf.dashboard.controller-1.0.0-SNAPSHOT.jar
```