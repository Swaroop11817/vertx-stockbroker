package com.swaroop.udemy.broker.watchlist;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class WatchListRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(WatchListRestApi.class);

  public static void attach(final Router parent){
    final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<>();
    final String path = "/account/watchlist/:accountId";
    parent.get(path).handler(new GetWatchListHandler(watchListPerAccount));
    parent.put(path).handler(new PutWatchListHandler(watchListPerAccount));
    parent.delete(path).handler(new DeleteWatchListHandler(watchListPerAccount));
  }

   static String getAccountId(RoutingContext context) {
    var accountId = context.pathParam("accountId");
    LOG.debug("{} for account {}", context.normalizedPath(),accountId);
    return accountId;
  }
}
