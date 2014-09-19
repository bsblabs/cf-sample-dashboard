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
to return *false*, you will always get an access denied exception when accessing the dashboard. If you have the
a running up Cloud Foundry instance, you can skip this section.

By default the controller uses the port 8888. The port can be customized by overriding the Maven property:

```
-Dcf.controller.port=[CUSTOM PORT]
```

The controller can be started by doing:

```
mvn clean spring-boot:run
```

or by doing:

```
mvn clean install
java -jar target/com.bsb.showcase.cf.dashboard.controller-1.0.0-SNAPSHOT.jar
```



Managing the UAA
---------------------

### Demo UAA

The dashboard is a client application that must be registered in the UAA file *[oauth-clients.xml](https://github.com/cloudfoundry/uaa/blob/master/uaa/src/main/webapp/WEB-INF/spring/oauth-clients.xml)*:

```
<entry key="sampleDashboard">
    <map>
        <entry key="id" value="sampleDashboard" />
        <entry key="secret" value="clientsecret" />
        <entry key="authorized-grant-types" value="password,implicit,authorization_code,client_credentials" />
        <entry key="scope"
            value="openid,cloud_controller.read,cloud_controller_service_permissions.read" />
        <entry key="authorities" value="uaa.resource" />
        <entry key="autoapprove">
            <list>
                <value>openid</value>
            </list>
        </entry>
        <entry key="signup_redirect_url" value="http://localhost:8989/" />
    </map>
</entry>
```

By default, there is a user called "marissa" with the password "koala". You can add extra one by modifying
*[scim-endpoints.xml](https://github.com/cloudfoundry/uaa/blob/master/uaa/src/main/webapp/WEB-INF/spring/scim-endpoints.xml)*.

### Real implementation

The dashboard as client application is registered like this to the UAA available at *[http://uaa.10.244.0.34.xip.io](http://uaa.10.244.0.34.xip.io)*:

```
uaac target http://uaa.10.244.0.34.xip.io/
uaac token get
uaac token client get admin -s admin-secret
uaac client add sampleDashboard --redirect_uri [YOUR IP ADDRESS] -s clientsecret
    --scope "cloud_controller.read,openid,cloud_controller_service_permissions.read"
    --authorized_grant_types "authorization_code,client_credentials,password,refresh_token"
    --authorities="uaa.resource" --autoapprove="openid"
```

The second command will require an email and password. By default, `admin:admin`.

New users can be added by doing:

```
uaac target http://uaa.10.244.0.34.xip.io/
uaac token get
uaac user add [USER] --family_name="[FULL NAME]" --emails="[EMAIL]"
```

For instance:

```
uaac user add john --family_name="John Smith" --emails="john.smith@foo.bar"
```



Managing the Cloud Controller
---------------------

### Stub Controller

See the previous section, there is nothing to configure. All you have to do is starting the controller.

### Real implementation

Make sure that the UAA user you're about to use can manage the current instance.

```
cf set-space-role [USER] [ORG] [SPACE] SpaceDeveloper
```

For instance:

```
cf set-space-role john com.bsb development SpaceDeveloper
```



com.bsb.showcase.cf.dashboard.impl
---------------------

This module contains the dashboard implementation. It will interact with the Cloud Controller and the UAA.

The default Cloud Controller URL is `http://localhost:8888`. This value can be customized by overriding
the Maven property:

```
-Dcf.controller.port=http://api.10.244.0.34.xip.io
```

The default UAA URL is `http://localhost:8080/uaa`. This value can be customized by overriding
the Maven property:

```
-Dcf.uaa.url=http://uaa.10.244.0.34.xip.io
```

By default the dashboard uses the port 8989. The port can be customized by overriding the Maven property:

```
-Ddashboard.controller.port=[CUSTOM PORT]
```

The dashboard can be started by doing:

```
mvn clean spring-boot:run
```
or by doing:

```
mvn clean install
java -jar target/com.bsb.showcase.cf.dashboard.impl-1.0.0-SNAPSHOT.war
```

The dashboard can be accessed at the URL `http://localhost:8989/`. You can log on the application with
the credentials contained in your UAA. There is also a technical endpoint that can be accessed at
`http://localhost:8989/services/v1/ping` with the credentials `admin:admin`.