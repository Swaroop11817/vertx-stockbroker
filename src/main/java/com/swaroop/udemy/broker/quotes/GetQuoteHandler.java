package com.swaroop.udemy.broker.quotes;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GetQuoteHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetQuoteHandler.class);
  private final Map<String, Quote> cachedQuotes;

  public GetQuoteHandler(Map<String, Quote> cachedQuotes) {
    this.cachedQuotes = cachedQuotes;
  }

  @Override
  public void handle(final RoutingContext routingContext) {

    final String assetParam = routingContext.pathParam("asset");
    LOG.debug("Asset parameter: {}", assetParam);

    var mayBequote = Optional.ofNullable(cachedQuotes.get(assetParam));
    if (mayBequote.isEmpty()) {
      routingContext.response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(new JsonObject()
          .put("message", "quote for asset " + assetParam + " not available!")
          .put("path", routingContext.normalizedPath())
          .toBuffer());
      return;
    }
    final JsonObject response = mayBequote.get().toJsonObject();
    LOG.info("Path {} responds with {}", routingContext.normalizedPath(), response.encode());
    routingContext.response().end(response.toBuffer());
  }
}
