package com.example.trx.domain.event.round.match;

public enum MatchType {
  NORMAL, //일반. 두 참가자 중 한 명만을 승리 처리합니다.
  BYE     //부전승. 참가한 모든 참가자를 승리 처리합니다.
}
