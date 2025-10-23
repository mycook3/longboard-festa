package com.example.trx.support.util;

import com.example.trx.service.sse.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EventHandler {

  private final SseService sseService;

  @EventListener
  public void handle(SseEvent event) {
    sseService.broadCast(event);
  }
}
