# lisa

[Build Status](https://build.tax.service.gov.uk/job/LISA/job/lisa/)

Backend microservice for [lisa-frontend]("http://github.com/hmrc/lisa-frontend")

## Requirements

This service is written in [Scala 2.13](http://www.scala-lang.org/) and [Play 2.8](http://playframework.com/), and needs at least Java 11 to run.

## Running Locally

1. **[Install Service-Manager](https://github.com/hmrc/service-manager/wiki/Install#install-service-manager)**
2. `git clone git@github.com:hmrc/lisa.git`
3. `sbt "run 8886"`
4. `sm2 --start LISA_FRONTEND_ALL`

The unit tests can be run by running
```
sbt test
```

To run a single unit test
```
sbt testOnly SPEC_NAME
```

## Resources

| Method | URL                                            | Description |
| :---: | ----------------------------------------------- | ---------------------------------------------------------------------- |
|  GET  |  /tax-enrolments/groups/:groupId/subscriptions  | TaxEnrolmentController.getSubscriptionsForGroupId(groupId: String) |
|  POST |  /:utr/register                                 | ROSMController.register(utr: String) |
|  POST |  /:utr/subscribe/:lisaManagerRef                | ROSMController.submitSubscription(utr: String, lisaManagerRef: String) |
|  GET  |  /rosm/callback                                 | ROSMController.subscriptionCallback() |
|  POST |  /rosm/callback                                 | ROSMController.subscriptionCallback() |
|  PUT  |  /rosm/callback                                 | ROSMController.subscriptionCallback() |

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
