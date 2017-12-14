package io.vertx.reactivex.ext.web.handler.sockjs.processor;

import in.erail.common.FramworkConstants;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventContext;
import io.vertx.reactivex.ext.web.handler.sockjs.BridgeEventProcessor;

/**
 *
 * @author vinay
 */
public class LoadAddressProcessor implements BridgeEventProcessor {

  @Override
  public Single<BridgeEventContext> process(Single<BridgeEventContext> pContext) {

    return pContext
            .map((ctx) -> {
              JsonObject rawMessage = ctx.getBridgeEvent().getRawMessage();
              if (rawMessage != null) {
                ctx.setAddress(rawMessage.getString(FramworkConstants.SockJS.BRIDGE_EVENT_RAW_MESSAGE_ADDRESS));
              }
              return ctx;
            });

  }

}
