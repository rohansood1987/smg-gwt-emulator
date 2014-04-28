package org.smg.gwt.emulator.i18n;

import com.google.gwt.i18n.client.Messages;

public interface StatusMessages extends Messages {

  @DefaultMessage("<b>Turn : </b> Player {0}")
  String playerTurnHTML(String playerTurnId);
  
  @DefaultMessage("Player {0}")
  String player(String playerId);
  
  @DefaultMessage("{0} Players")
  String numPlayers(int number);
}
