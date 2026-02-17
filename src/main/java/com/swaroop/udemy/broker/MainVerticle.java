package com.swaroop.udemy.broker;

import com.swaroop.udemy.broker.assets.AssetsRestApi;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainVerticle extends VerticleBase {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args){
    var vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> {
      LOG.error("Unhandled:", error);
    });
    vertx.deployVerticle(new MainVerticle()).onComplete(ar->{
      if(ar.failed()){
        LOG.error("Failed to deploy:", ar.cause());
        return;
      }
      LOG.info("Deployed {}!",MainVerticle.class.getName());
    });
  }


  @Override
  public Future<?> start() {
    final Router restApi = Router.router(vertx);
    restApi.route().failureHandler(errorContext -> {
      if(errorContext.response().ended()){
        return;
      }
      LOG.error("Route Error:",errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong:(").toBuffer());
    });
    AssetsRestApi.attach(restApi);

    return vertx.createHttpServer().requestHandler(restApi)
      .exceptionHandler(error -> LOG.error("HTTP Server error: ", error))
      .listen(8888).onSuccess(http->{

      LOG.info("HTTP server started on port 8888");
    });
  }
}
