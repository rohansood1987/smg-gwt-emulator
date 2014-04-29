package org.smg.gwt.emulator.i18n;

import com.google.gwt.i18n.client.Messages;

public interface StatusMessages extends Messages {

  @DefaultMessage("<b>Turn : </b> Player {0}")
  String playerTurnHTML(String playerTurnId);
  
  @DefaultMessage("Player {0}")
  String player(String playerId);
  
  @DefaultMessage("{0} Players")
  String numPlayers(int number);
  
  /*
   * Specifies the order in which the language is written. For English this would be first string
   * followed by second but in a language like Hebrew it would be opposite.
   */
  @DefaultMessage("{0} {1}")
  String timeLeftOrder(String string1, String string2);
  
  @DefaultMessage("<b>Time Left : </b>")
  String timeLeftBold();
}
