package com.example.trx.service.sse;

import com.example.trx.support.util.SseEvent;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
public class SseService {

  private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  public SseEmitter subscribe(String sessionId) {
    SseEmitter emitter = createSseEmitter(sessionId);
    String at = Long.toHexString(System.currentTimeMillis());
    sendMessage(sessionId, at, SseEvent.of("created", "sse emitter created"));
    return emitter;
  }

  private SseEmitter createSseEmitter(String sessionId) {
    SseEmitter emitter = new SseEmitter();

    emitters.put(sessionId, emitter);
    return emitter;
  }

  public void broadCast(SseEvent event) {
    String at = Long.toHexString(System.currentTimeMillis());
    for (String sessionId : emitters.keySet()) {
        sendMessage(sessionId, at, event);
    }
  }

  private void sendMessage(String sessionId, String sentTime, SseEvent event) {
    SseEmitter emitter = emitters.get(sessionId);
    String messageId = sessionId + "_" + sentTime;
    String name = event.getType();
    String message = event.getMessage();

    if (emitter != null) {
      try {
        emitter.send(
            SseEmitter.event()
                .id(messageId)
                .name(name)
                .data(message)
                .build()
        );
      } catch (IOException e) {
        emitters.remove(sessionId);
      }
    }
  }
}
