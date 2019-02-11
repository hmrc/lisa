# lisa

[Build Status](https://build.tax.service.gov.uk/job/LISA/job/lisa/)

Backend microservice for [lisa-frontend]("http://github.com/hmrc/lisa-frontend")

## Requirements

This service is written in [Scala 2.11](http://www.scala-lang.org/) and [Play 2.5](http://playframework.com/), so needs at least a [JRE 1.8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) to run.

## Running Locally

1. **[Install Service-Manager](https://github.com/hmrc/service-manager/wiki/Install#install-service-manager)**
2. `git clone git@github.com:hmrc/lisa.git`
3. `sbt "run 8886"`
4. `sm --start LISA_FRONTEND_ALL -f`

The unit tests can be run by running
```
sbt test
```

To run a single unit test
```
sbt testOnly SPEC_NAME
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
