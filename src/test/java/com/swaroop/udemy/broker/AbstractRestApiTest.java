package com.swaroop.udemy.broker;

import com.swaroop.udemy.broker.config.ConfigLoader;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(VertxExtension.class)
public abstract class AbstractRestApiTest {
  protected static final int TEST_SERVER_PORT = 9000;
  private static final Logger LOG = LoggerFactory.getLogger(AbstractRestApiTest.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    System.setProperty(ConfigLoader.SERVER_PORT, String.valueOf(TEST_SERVER_PORT));
    System.setProperty(ConfigLoader.DB_HOST, "localhost");
    System.setProperty(ConfigLoader.DB_PORT, "5432");
    System.setProperty(ConfigLoader.DB_DATABASE, "vertx-stock-broker");
    System.setProperty(ConfigLoader.DB_USER, "postgres");
    System.setProperty(ConfigLoader.DB_PASSWORD, "secret");
    LOG.warn("Tests are using local database");
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> testContext.completeNow()));
  }
}
