package com.swaroop.udemy.broker.assets;

import com.swaroop.udemy.broker.DbResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAssetsFromDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetAssetsFromDatabaseHandler.class);
  private final Pool db;

  public GetAssetsFromDatabaseHandler(final Pool db){
   this.db = db;
  }

  @Override
  public void handle(final RoutingContext context){

    db.query("SELECT a.value from broker.assets a")
      .execute()
      .onFailure(DbResponse.errorHandler(context, "Failed to get the assets from db!"))
      .onSuccess(result -> {
        var response = new JsonArray();
        result.forEach(row -> {
          response.add(row.getValue("value"));
        });
        LOG.info("Path {} responds with {}",context.normalizedPath(), response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });


  }
}
