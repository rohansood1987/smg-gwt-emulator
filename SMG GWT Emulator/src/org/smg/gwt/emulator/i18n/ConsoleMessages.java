package org.smg.gwt.emulator.i18n;

import com.google.gwt.i18n.client.Messages;

public interface ConsoleMessages extends Messages {
  
  @DefaultMessage("Setup done for {0} players.")
  String setupDone(int numberOfPlayers);
  
  @DefaultMessage("Added AI Player with ID: {0}")
  String aiPlayerAdded(String aiPlayerID);
  
  @DefaultMessage("To: {0}, Type: {1}")
  String outgoingGameApiMessage(String playerId, String messageName);
  
  @DefaultMessage("From: {0}, Type: {1}")
  String incomingGameApiMessage(String playerId, String messageName);
  
  @DefaultMessage("UNKNOWN")
  String unknown();
  
  @DefaultMessage("<err> no instance found")
  String errorInstance();
  
  @DefaultMessage("<err> cant parse json. {0}")
  String errorParsingJson(String exception);
  
  @DefaultMessage("Message Received: {0}")
  String messageReceived(String messageName);
  
  @DefaultMessage("Simulating network delay of ''{0}'' ms.")
  String simulatingNetworkDelay(int delay);
  
  @DefaultMessage("Handling GameReady from player id {0}")
  String handlingGameReady(String sendingPlayerId);
  
  @DefaultMessage("Handling MakeMove")
  String handlingMakeMove();
  
  @DefaultMessage("Handling VerifyMoveDone")
  String handlingVerifyMoveDone();
  
  @DefaultMessage("Hacker Detected! -- {0}")
  String hackerDetected(String hackerId);
  
  @DefaultMessage("Updating state manually: {0}")
  String updatingStateManually(String state);
  
  @DefaultMessage("Saving Game State")
  String savingGameState();
  
  @DefaultMessage("Loading Game State")
  String loadingGameState();
  
  @DefaultMessage("To: {0}")
  String outgoingMessageDetails(String playerId);
  
  @DefaultMessage("From: {0}")
  String incomingMessageDetails(String playerId);
  
  @DefaultMessage("{0} ({1})")
  String messageNameAndDetails(String messageName, String messageDetails);
  
  @DefaultMessage("Info Message")
  String infoMessageConstant();
  
  @DefaultMessage("Info Message: {0}")
  String infoMessage(String message);
}

