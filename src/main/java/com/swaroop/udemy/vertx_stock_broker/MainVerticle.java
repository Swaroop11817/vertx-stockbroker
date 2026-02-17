package com.swaroop.udemy.vertx_stock_broker;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
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
    return vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    }).listen(8888).onSuccess(http -> {
      LOG.info("HTTP server started on port 8888");
    });
  }
}
