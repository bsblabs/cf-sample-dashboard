Sample Cloud Foundry Service Dashboard
=================

This demo illustrates how we can develop a dashboard for a Cloud Foundry service. The dashboard interacts with 2 main Cloud Foundry components: the UAA and the Cloud Controller. The UAA is used to authenticate the user. The authentication is based on OAuth2 (SSO mechanism). The Cloud Controller (in the scope of a dashboard) is used to to check that the current user can access the current dashboard instance.

More information:
* [Cloud Foundry UAA, doc](http://docs.cloudfoundry.org/concepts/architecture/uaa.html)
* [Cloud Foundry Dashboard, SSO doc](http://docs.cloudfoundry.org/services/dashboard-sso.html)
* [Cloud Foundry Controller, doc](http://docs.cloudfoundry.org/concepts/architecture/cloud-controller.html)
* [Cloud Foundry UAA, security](https://github.com/cloudfoundry/uaa/blob/master/docs/UAA-Security.md)
* [Cloud Foundry UAA, High level features](http://blog.cloudfoundry.org/2012/07/24/high-level-features-of-the-uaa/)
* [OAuth Grant Types](http://aaronparecki.com/articles/2012/07/29/1/oauth2-simplified#other-app-types)

In order to test the sample dashboard these 2 components must be running up. There are 2 options. You have a running up Cloud Foundry instance. Or, you download the UAA at the following address: [Cloud Foundry UAA](https://github.com/cloudfoundry/uaa) and you use the stub Cloud Controller in the sub-project *com.bsb.showcase.cf.dashboard.controller*.



Dashboard Features
---------------------

The dashboard can be accessed in 2 different ways: from a web page, or from a REST endpoint.

When a human user accesses the web page, he is redirected to the UAA login page. Once authenticated on the UAA, the browser comes back on the dashboard application. The dashboard displays the user's full name and a link to log out of the dashboard and the more globally out of the UAA. Every logged on client is matched to an internal user. The whole authentication mechanism is performed externally. As a consequence, passwords are not stored in this application.

The REST endpoint, a technical endpoint is secured via basic authentication. The idea was also to illustrate a different way to perform authentication on the service. The authentication mechanism is based on the JPA entity *TechUser*.

The REST technical endpoint simply returns an "Hello World":

```
{
message: "Hello World!"
}
```



Managing the UAA
---------------------

The dashboard needs to authenticate on the UAA. It interacts with the UAA as an OAuth client. The dashboard uses an id and a secret to perform authentication on the UAA. We chose the client id "sampleDashboard" and the secret "clientsecret".

The grant-types allows the dashboard to use refresh tokens (see *refresh_token*). The client is authenticated on the UAA login page and redirected on the dashboard (see *authorization_code*). The dashboard is authorized to request "client_credentials" grant, the associated scope is "uaa.resource" (see [endpoint security](https://github.com/cloudfoundry/uaa/blob/master/uaa/src/main/webapp/WEB-INF/spring/resource-endpoints.xml#L22)).

The dashboard requires the minimal scopes, see [dashboard SSO](https://github.com/cloudfoundry/docs-services/blob/master/dashboard-sso.html.md):
* *openid*: allows access to basic data about the user, such as email addresses.
* *cloud_controller_service_permissions.read*: allows access to the Cloud Controller endpoint that specifies whether the user can manage a given service instance.

Those scopes are automatically approved, the user won't be prompted to check whether he authorizes those scopes (they are mandatory).

The signup redirect URL is the dashboard URL, or part of this URL. In this case, we chose to specify the dashboard URL up to its domain/IP address (no port, no context).



### Standalone UAA

The [UAA](https://github.com/cloudfoundry/uaa) can be executed in standalone by launching a single command. Before starting the UAA, you have to modify the file  *[oauth-clients.xml](https://github.com/cloudfoundry/uaa/blob/master/uaa/src/main/webapp/WEB-INF/spring/oauth-clients.xml)*:

```
<entry key="sampleDashboard">
    <map>
        <entry key="id" value="sampleDashboard" />
        <entry key="secret" value="clientsecret" />
        <entry key="authorized-grant-types" value="authorization_code,client_credentials,refresh_token" />
        <entry key="scope"
               value="openid,cloud_controller_service_permissions.read" />
        <entry key="authorities" value="uaa.resource" />
        <entry key="autoapprove">
            <list>
                <value>openid</value>
                <value>cloud_controller_service_permissions.read</value>
            </list>
        </entry>
        <entry key="signup_redirect_url" value="[DASHBOARD URL]" />
    </map>
</entry>
```

Don't forget to change `[DASHBOARD URL]` by the dashboard domain (could be an IP, "localhost", or a domain). If you only want to make a local test, just use `http://localhost`.

By default, there is a user called "marissa" with the password "koala". You can add extra ones by modifying *[scim-endpoints.xml](https://github.com/cloudfoundry/uaa/blob/master/uaa/src/main/webapp/WEB-INF/spring/scim-endpoints.xml)*.

The UAA is started by:

```
./gradlew run
```

### UAA in a Cloud Foundry Instance

Let's consider that the UAA is available at the address *[http://uaa.10.244.0.34.xip.io](http://uaa.10.244.0.34.xip.io)*. The UAA can be managed with the command `uaac` (see [UAAC doc](https://github.com/cloudfoundry/cf-uaac)).

The dashboard as client application is registered like this to the UAA:

```
uaac target http://uaa.10.244.0.34.xip.io/
uaac token get
uaac token client get admin -s admin-secret
uaac client add sampleDashboard --redirect_uri [DASHBOARD URL] -s clientsecret
    --scope "openid,cloud_controller_service_permissions.read"
    --authorized_grant_types "authorization_code,client_credentials,refresh_token"
    --authorities="uaa.resource" --autoapprove="openid,cloud_controller_service_permissions.read"
```

The second command will require an email and a password. By default, `admin:admin`.  In the fourth command, don't forget to change `[DASHBOARD URL]` by the dashboard domain (could be an IP, "localhost", or a domain).

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

The module *com.bsb.showcase.cf.dashboard.controller* provides a stub implementation of the controller that always returns *true* to the question "Is the current user allowed to manage the current service instance (i.e., dashboard instance)?". If you change the code to return *false*, you will always get an access denied exception when accessing the dashboard. If you have the a running up Cloud Foundry instance, you can skip this section.

By default the controller uses the port 8888. The port can be customized by overriding the Maven property `cf.controller.port`. For instance:

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

### Cloud Controller in a Cloud Foundry Instance

Make sure that the UAA user you're about to use can manage the current instance.

```
cf set-space-role [USER] [ORG] [SPACE] SpaceDeveloper
```

For instance:

```
cf set-space-role john com.bsb development SpaceDeveloper
```



Dashboard
---------------------

The module *com.bsb.showcase.cf.dashboard.impl* contains the dashboard implementation. It will interact with the Cloud Controller and the UAA.

The default Cloud Controller URL is `http://localhost:8888`. This value can be customized by overriding the Maven property `cf.controller.port`. For instance:

```
-Dcf.controller.port=http://api.10.244.0.34.xip.io
```

The default UAA URL is `http://localhost:8080/uaa`. This value can be customized by overriding the Maven property `cf.uaa.url`. For instance:

```
-Dcf.uaa.url=http://uaa.10.244.0.34.xip.io
```

By default the dashboard uses the port 8989. The port can be customized by overriding the Maven property `dashboard.controller.port`. For instance:

```
-Ddashboard.controller.port=10100
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

As described in the initial section there are two ways to access the dashboard:
- The dashboard can be accessed by default at the URL `http://localhost:8989/dashboard/`; please use the credentials contained in your UAA.
- The technical endpoint can be accessed by default at `http://localhost:8989/services/v1/ping`; please use the credentials `admin:admin` (basic authentication).