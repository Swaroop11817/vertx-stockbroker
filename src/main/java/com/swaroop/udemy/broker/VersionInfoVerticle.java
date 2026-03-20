package com.swaroop.udemy.broker;

import com.swaroop.udemy.broker.config.ConfigLoader;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionInfoVerticle extends VerticleBase {

  private static final Logger LOG = LoggerFactory.getLogger(VersionInfoVerticle.class);

  @Override
  public Future<?> start() {
    return ConfigLoader.load(vertx).onFailure(error -> {
      LOG.error("Failed to load config", error);
    }).compose(config -> {
      LOG.info("Current Application Version is: {}", config.getVersion());
      return Future.succeededFuture();
    });
  }
}
