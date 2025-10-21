package com.example.trx.support.util;

import lombok.Getter;

@Getter
public class SseEvent {
  SseEventType type;
  String message;

  private SseEvent() {
    this.type = SseEventType.NONE;
    this.message = "";
  }

  private SseEvent(SseEventType type, String message) {
    this.type = type;
    this.message = message;
  }

  public static SseEvent of(SseEventType type, Object obj) {
    return new SseEvent(type, obj.toString());
  }
}
