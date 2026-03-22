package com.swaroop.udemy.broker;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbResponse {
  private static final Logger logger = LoggerFactory.getLogger(DbResponse.class);

  public static Handler<? super Throwable> errorHandler(RoutingContext context, String message) {
    return error -> {
      logger.error("Failure: ", error);
      context.response()
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(new JsonObject()
          .put("message", message)
          .put("path", context.normalizedPath())
          .toBuffer());
    };
  }

  public static void notFoundResponse(RoutingContext context,String message) {
    context.response()
      .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
      .end(new JsonObject()
        .put("message", message)
        .put("path", context.normalizedPath())
        .toBuffer());
  }

}
