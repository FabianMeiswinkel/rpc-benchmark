import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.ChannelPipelineConfigurer;
import reactor.netty.ConnectionObserver;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerResponse;

import javax.net.ssl.SSLException;
import java.net.SocketAddress;
import java.security.cert.CertificateException;

public class EchoHttp11Server extends EchoServerBase {
    private final static Logger logger = LoggerFactory.getLogger(EchoHttp11Server.class);

    @Override
    public void Start(int port) throws CertificateException, SSLException {
        SelfSignedCertificate ssc = new SelfSignedCertificate("localhost");
        SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
//                .startTls(true)
                .build();

        server = HttpServer.create()
                .port(port)
                .protocol(HttpProtocol.HTTP11)
                .secure(spec -> spec.sslContext(sslCtx))
                .route(routes -> {
                    routes.get("/hello",
                            (request, response) ->
                                    Utils.OKResponseWithStringBody(response, "Hello World")
                    );
                })
                .wiretap(true)
                .doOnConnection(connection ->
                {
                    if (logger.isInfoEnabled()) {
                        logger.info("OnConnection for: {}", connection.address());
                    }
                })
                .doOnChannelInit(new ChannelPipelineConfigurer() {
                    @Override
                    public void onChannelInit(ConnectionObserver connectionObserver, Channel channel, SocketAddress socketAddress) {
                        if (logger.isInfoEnabled()) {
                            logger.info("OnChannelInit for: {} -> ", channel.remoteAddress(), channel.localAddress());
                        }
                    }
                })
                .bindNow();

        logger.info("Http1 Server listening on port: {}", port);
    }
}
