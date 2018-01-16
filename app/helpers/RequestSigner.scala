package helpers

import java.net.URI
import javax.inject.{Inject, Singleton}

import com.gu.hmac.HMACHeaders
import play.api.Configuration

@Singleton
class RequestSigner @Inject() (config:Configuration) extends HMACHeaders {
  val secret=config.get[String]("AtomToolSharedSecret")


}
