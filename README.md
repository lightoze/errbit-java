Errbit Java notifier
===========

Error catcher for Java applications. Log4j logger included.

To use with Maven:
```xml
<dependency>
    <groupId>net.lightoze.errbit</groupId>
    <artifactId>errbit-logger</artifactId>
    <version>2.3.1</version>
</dependency>
```

Then you can add this to your log4 configuration file:

```xml
<appender name="errbit" class="net.lightoze.errbit.Log4jErrbitAppender">
    <param name="url" value="http://errbit.example.com/notifier_api/v2/notices" />
    <param name="apiKey" value="<your-errbit-app-api-key>" />
    <param name="environment" value="<your-app-environment>" />
    <param name="threshold" value="error" />
</appender>

<root>
    <priority value ="warn" />
    <appender-ref ref="errbit" />
</root>
```
