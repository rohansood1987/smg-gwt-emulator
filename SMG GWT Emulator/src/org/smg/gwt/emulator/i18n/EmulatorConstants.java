package org.smg.gwt.emulator.i18n;

import com.google.gwt.i18n.client.Constants;

public interface EmulatorConstants extends Constants {
  
  @DefaultStringValue("Number of Players")
  String numberOfPlayers();
  
  @DefaultStringValue("Player Tokens")
  String playerTokens();
  
  @DefaultStringValue("Time-limit Per Turn")
  String timeLimit();
  
  @DefaultStringValue("Network Delay")
  String networkDelay();
  
  @DefaultStringValue("URL")
  String url();
  
  @DefaultStringValue("Add Viewer")
  String addViewer();
  
  @DefaultStringValue("Single Player")
  String singlePlayer();
  
  @DefaultStringValue("AI Present")
  String aiPresent();
  
}
