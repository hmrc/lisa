package models

import play.api.libs.json._

case class SendEmailRequest(to: List[String], templateId: String, parameters: Map[String, String], force: Boolean)

object SendEmailRequest {
  implicit val format = Json.format[SendEmailRequest]
}
