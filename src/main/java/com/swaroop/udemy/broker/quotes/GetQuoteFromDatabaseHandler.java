package com.swaroop.udemy.broker.quotes;

import com.swaroop.udemy.broker.DbResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class GetQuoteFromDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger logger = LoggerFactory.getLogger(GetQuoteFromDatabaseHandler.class);
  private final Pool db;

  public GetQuoteFromDatabaseHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(final RoutingContext context) {
    final String assetParam = context.pathParam("asset");
    logger.debug("Asset parameter: {}", assetParam);
    SqlTemplate.forQuery(db, "SELECT q.asset,q.bid,q.ask,q.last_price,q.volume from broker.quotes q where asset= #{asset}")
      .mapTo(QuoteEntity.class)
      .execute(Collections.singletonMap("asset", assetParam))
      .onFailure(DbResponse.errorHandler(context,"Failed to get quote for asset "+assetParam+" from db!"))
      .onSuccess(quotes-> {
        if(!quotes.iterator().hasNext()){
          DbResponse.notFoundResponse(context,"quote for asset "+assetParam+" not available!");
          return;
        }
        var response = quotes.iterator().next().toJsonObject();
        logger.info("Path {} responds with {}", context.normalizedPath(), response.encode());
        context.response()
          .end(response.toBuffer());
      });

  }


}
