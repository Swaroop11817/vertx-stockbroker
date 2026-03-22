package com.swaroop.udemy.broker.watchlist;

import com.swaroop.udemy.broker.DbResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.sqlclient.Pool;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class GetWatchListFromDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(GetWatchListFromDatabaseHandler.class);

  private final Pool db;

  public GetWatchListFromDatabaseHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext ctx) {

    var accountId = WatchListRestApi.getAccountId(ctx);

    SqlTemplate.forQuery(db,
      "SELECT w.asset FROM broker.watchlist w where w.account_id = #{account_id}")
      .mapTo(Row::toJson)
      .execute(Collections.singletonMap("account_id", accountId))
      .onFailure(DbResponse.errorHandler(ctx, "Failed to fetch the watchlist for accountId: " + accountId))
      .onSuccess(assets -> {
        if(!assets.iterator().hasNext()){
          DbResponse.notFoundResponse(ctx, "watchlist for accountId: " + accountId + " is not available!");
        }
        var response = new JsonArray();
        assets.forEach(response::add);
        logger.info("Path {} responds with {}", ctx.normalizedPath(), response.encode());
        ctx.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });

  }
}
