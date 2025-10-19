package com.example.trx.service.sse;

import com.example.trx.support.util.SseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EventHandler {

  private final SseService sseService;

  @TransactionalEventListener
  public void handle(SseEvent event) {
    sseService.broadCast(event);
  }
}
