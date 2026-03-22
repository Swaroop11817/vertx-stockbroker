package com.swaroop.udemy.broker.watchlist;

import com.swaroop.udemy.broker.DbResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PutWatchListDatabaseHandler implements Handler<RoutingContext> {
  private static final Logger logger = LoggerFactory.getLogger(PutWatchListDatabaseHandler.class);
  private final Pool db;

  public PutWatchListDatabaseHandler(final Pool db) {
    this.db = db;
  }

 @Override
  public void handle(final RoutingContext ctx) {

   var accountId = WatchListRestApi.getAccountId(ctx);

   var json = ctx.body().asJsonObject();
   var watchList = json.mapTo(WatchList.class);

  var parameterBatch = watchList.getAssets().stream().map(asset -> {
       final Map<String, Object> parameters = new HashMap<>();
       parameters.put("account_id", accountId);
       parameters.put("asset", asset.getName());
       return parameters;
     }).collect(Collectors.toList());

     SqlTemplate.forUpdate(db, "INSERT INTO broker.watchlist VALUES (#{account_id},#{asset})" +
         "ON CONFLICT (account_id, asset) DO NOTHING")
       .executeBatch(parameterBatch)
       .onFailure(DbResponse.errorHandler(ctx, "Failed to insert into watchlist"))
       .onSuccess(result -> {
           ctx.response()
             .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
             .end();
       });

 }
}
