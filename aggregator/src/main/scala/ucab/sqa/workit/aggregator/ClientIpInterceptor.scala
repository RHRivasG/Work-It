package ucab.sqa.workit.aggregator

import io.grpc.ServerInterceptor
import io.grpc.{Metadata, ServerCall, ServerCallHandler}
import io.grpc.ServerCall.Listener
import io.grpc.Grpc
import java.net.InetSocketAddress

case class ClientIpInterceptor(key: Metadata.Key[String]) extends ServerInterceptor {
  override def interceptCall[ReqT <: Object, RespT <: Object](x$1: ServerCall[ReqT,RespT], x$2: Metadata, x$3: ServerCallHandler[ReqT,RespT]): Listener[ReqT] = {
    val socketAddr = x$1.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR).asInstanceOf[InetSocketAddress]
    x$2.put(key, socketAddr.getAddress.getHostAddress + ":8080")
    x$3.startCall(x$1, x$2)
  }

}