package com.swaroop.udemy.broker.quotes;

import com.swaroop.udemy.broker.MainVerticle;
import com.swaroop.udemy.broker.assets.TestAssetsRestApi;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuotesRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(TestQuotesRestApi.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> testContext.completeNow()));
  }

  @AfterEach
  void tearDown(Vertx vertx, VertxTestContext ctx) {
    vertx.close().onComplete(ctx.succeedingThenComplete());
  }

  @Test
  void returns_quote_for_asset(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var client = WebClient.create(vertx,new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    client.get("/quotes/AMZN")
      .send()
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("Response {}", json);
        assertEquals("{\"name\":\"AMZN\"}",json.getJsonObject("asset").encode());
        assertEquals(200,response.statusCode());
        testContext.completeNow();
      }));
  }

  @Test
  void returns_not_found_for_unknown_asset(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var client = WebClient.create(vertx,new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    client.get("/quotes/UNKNOWN")
      .send()
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("Response {}", json);
        assertEquals(404,response.statusCode());
        assertEquals("{\"message\":\"quote for asset UNKNOWN not available!\",\"path\":\"/quotes/UNKNOWN\"}",json.encode());
        testContext.completeNow();
      }));
  }
}
