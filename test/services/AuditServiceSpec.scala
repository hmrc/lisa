package services

import config.AppConfig
import org.mockito.{ArgumentCaptor, ArgumentMatchers}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.DataEvent

import scala.concurrent.ExecutionContext

class AuditServiceSpec extends PlaySpec
  with MockitoSugar
  with GuiceOneAppPerSuite
  with BeforeAndAfter {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  val mockAuditConnector: AuditConnector = mock[AuditConnector]
  val mockAppConfig: AppConfig = mock[AppConfig]
  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  object SUT extends AuditService(mockAuditConnector, mockAppConfig)

  "AuditService" must {

    before {
      reset(mockAuditConnector)
      when(mockAppConfig.appName).thenReturn("lisa")
    }

    "build an audit event with the correct details" in {
      SUT.audit("submitSubscriptionSuccess", "submitSubscription", Map("safeId" -> "safeId", "lisaManagerRef" -> "lisaManagerRef", "subscriptionId" -> "subscriptionId"))

      val captor: ArgumentCaptor[DataEvent] = ArgumentCaptor.forClass(classOf[DataEvent])

      verify(mockAuditConnector).sendEvent(captor.capture())(ArgumentMatchers.any(), ArgumentMatchers.any())

      val event = captor.getValue

      event.auditSource mustBe "lisa"
      event.auditType mustBe "submitSubscriptionSuccess"

      event.tags must contain("path" -> "submitSubscription")
      event.tags must contain("transactionName" -> "submitSubscriptionSuccess")

      event.detail must contain("safeId" -> "safeId")
      event.detail must contain("lisaManagerRef" -> "lisaManagerRef")
      event.detail must contain("subscriptionId" -> "subscriptionId")
    }

  }
}
