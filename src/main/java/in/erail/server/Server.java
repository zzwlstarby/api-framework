package in.erail.server;

import in.erail.route.RouterBuilder;
import org.apache.logging.log4j.Logger;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.LoggerHandler;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler;
import in.erail.glue.Glue;
import in.erail.glue.annotation.StartService;

/**
 *
 * @author vinay
 */
public class Server {

  private int mPort;
  private String mHost;
  private Vertx mVertx;
  private String[] mRouterBuilder;
  private String[] mMountPath;
  private Router[] mRouter;
  private Logger mLog;
  private SockJSHandler mSockJSHandler;

  @StartService
  public void start() {

    HttpServer server = getVertx().createHttpServer(new HttpServerOptions().setPort(getPort()).setHost(getHost()));

    Router router = Router.router(getVertx());

    // Logging
    if (getLog().isDebugEnabled()) {
      router.route("/*").handler(LoggerHandler.create());
    }

    if (getSockJSHandler() != null) {
      router.route("/eventbus/*").handler(getSockJSHandler());
    }

    for (int i = 0; i < mMountPath.length; i++) {
      router.mountSubRouter(mMountPath[i], mRouter[i]);
    }

    server
            .requestHandler(router::accept)
            .rxListen()
            .blockingGet();

    getLog().debug(() -> String.format("---------------Server[%s:%s] is ready-----------------", getHost(), getPort()));
  }

  public Vertx getVertx() {
    return mVertx;
  }

  public void setVertx(Vertx pVertx) {
    this.mVertx = pVertx;
  }

  public int getPort() {
    return mPort;
  }

  public void setPort(int pPort) {
    this.mPort = pPort;
  }

  public String getHost() {
    return mHost;
  }

  public void setHost(String pHost) {
    this.mHost = pHost;
  }

  public String[] getRouterBuilder() {
    return mRouterBuilder;
  }

  public void setRouterBuilder(String[] pRouterBuilder) {

    this.mRouterBuilder = pRouterBuilder;

    mMountPath = new String[pRouterBuilder.length];
    mRouter = new Router[pRouterBuilder.length];

    for (int i = 0; i < pRouterBuilder.length; i++) {
      String[] kv = pRouterBuilder[i].split("=");
      Object component = Glue.instance().resolve(kv[1]);
      String route = kv[0];
      mMountPath[i] = route;
      mRouter[i] = ((RouterBuilder) component).getRouter();
    }
  }

  public Logger getLog() {
    return mLog;
  }

  public void setLog(Logger pLog) {
    this.mLog = pLog;
  }

  public SockJSHandler getSockJSHandler() {
    return mSockJSHandler;
  }

  public void setSockJSHandler(SockJSHandler pSockJSHandler) {
    this.mSockJSHandler = pSockJSHandler;
  }

}
