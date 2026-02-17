package com.swaroop.udemy.broker.quotes;

import com.swaroop.udemy.broker.assets.Asset;
import com.swaroop.udemy.broker.assets.AssetsRestApi;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(QuotesRestApi.class);

  public static void attach(Router parent) {
    final Map<String, Quote> cachedQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(symbol ->
      cachedQuotes.put(symbol, initRandomQuote(symbol))
  );

    parent.get("/quotes/:asset").handler(routingContext -> {
      final String assetParam = routingContext.pathParam("asset");
      LOG.debug("Asset parameter: {}",assetParam);

      var mayBequote = Optional.ofNullable(cachedQuotes.get(assetParam));
      if(mayBequote.isEmpty()){
        routingContext.response()
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject()
            .put("message", "quote for asset " + assetParam + " not available!")
            .put("path",routingContext.normalizedPath())
            .toBuffer());
        return;
      }
      final JsonObject response = mayBequote.get().toJsonObject();
      LOG.info("Path {} responds with {}", routingContext.normalizedPath(), response.encode());
      routingContext.response().end(response.toBuffer());
    });
  }

  private static Quote initRandomQuote(final String assetParam) {
    return Quote.builder()
      .asset(new Asset(assetParam))
      .volume(randomValue())
      .ask(randomValue())
      .bid(randomValue())
      .lastPrice(randomValue())
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1,100));
  }

}
