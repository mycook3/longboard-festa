package com.example.trx.support.util;

import lombok.Getter;

@Getter
public class SseEvent {
  String type;
  String message;

  private SseEvent() {
    this.type = "";
    this.message = "";
  }

  private SseEvent(String type, String message) {
    this.type = type;
    this.message = message;
  }

  public static SseEvent of(String type, String message) {
    return new SseEvent(type, message);
  }
}
