package com.example.trx.apis.sse;

import com.example.trx.service.sse.SseService;
import com.example.trx.support.util.SseEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sse")
public class SseController {

  private final SseService sseService;

  @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribe(HttpServletRequest request) {
    String sessionId = request.getSession().getId();
    log.info("sessionId: {}", sessionId);
    return sseService.subscribe(sessionId);
  }

  @GetMapping("/test")
  public void sseTest() {
    sseService.broadCast(SseEvent.of("test", "hello"));
  }
}
