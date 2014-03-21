package org.smg.gwt.emulator.backend;

import java.util.List;
import java.util.Map;

import org.smg.gwt.emulator.client.GwtEmulatorGraphics;
import org.smg.gwt.emulator.data.GameApi.GameReady;
import org.smg.gwt.emulator.data.GameApi.MakeMove;
import org.smg.gwt.emulator.data.GameApi.Message;
import org.smg.gwt.emulator.data.GameApi.Operation;
import org.smg.gwt.emulator.data.GameApi.UpdateUI;
import org.smg.gwt.emulator.data.GameApi.VerifyMove;
import org.smg.gwt.emulator.data.GameApi.VerifyMoveDone;
import org.smg.gwt.emulator.data.GameApiJsonHelper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * This class emulates all the functionalities of the server.
 * 
 */
public class ServerEmulator {
  
  private int numberOfPlayers;
  private List<Integer> playerIds;
  private final List<Map<String, Object>> playersInfo = Lists.newArrayList();
  private GameState gameState;
  private GameState lastGameState;
  private List<Operation> lastMove;
  private int lastMovePlayerId;
  private GwtEmulatorGraphics graphics;
  
  public static final String PLAYER_ID = "playerId";
  private static final int firstPlayerId = 42;
  
  private boolean moveInProgress;
  private List<Integer> verifiers = Lists.newArrayList();
  
  public ServerEmulator(int numberOfPlayers, GwtEmulatorGraphics graphics) {
    gameState = new GameState();
    this.numberOfPlayers = numberOfPlayers;
    this.graphics = graphics;
    setupPlayers();
  }
  
  private void setupPlayers() {
    playerIds = Lists.newArrayList();
    for(int i = 0; i < numberOfPlayers; i++) {
      int playerId = firstPlayerId + i;
      playerIds.add(playerId);
      playersInfo.add(ImmutableMap.<String, Object>of(PLAYER_ID, playerId));
    }
  }
  
  public List<Integer> getPlayerIds() {
    return playerIds;
  }
  
  private int getPlayerIndex(int playerId) {
    return playerIds.indexOf(playerId);
  }
  
  public void eventListner(String message, Integer playerIndex) {
    
    int playerId = playerIds.get(playerIndex);
    graphics.logToConsole("Message from [" + playerId + "]: " + message);
    
    Message messageObj = GameApiJsonHelper.getMessageObject(message);
    
    if (messageObj instanceof GameReady) {
      handleGameReady((GameReady)messageObj, playerId);
    }
    else if (messageObj instanceof MakeMove) {
      handleMakeMove((MakeMove)messageObj, playerId);
    }
    else if (messageObj instanceof VerifyMoveDone) {
      handleVerifyMoveDone((VerifyMoveDone)messageObj, playerId);
    }
  }

  private void handleGameReady(GameReady gameReady, int playerId) {
    // Send initial UpdateUI message
    int playerIndex = getPlayerIndex(playerId);
    graphics.sendMessage(playerIndex, new UpdateUI(playerId, playersInfo,
          gameState.getStateForPlayerId(playerId),
          null, null, playerId, gameState.getPlayerIdToNumberOfTokensInPot()));
  }
  
  private void handleMakeMove(MakeMove makeMove, int playerId) {
    
    if (moveInProgress) {
      //TODO:Handle the case where previous move was in progress and a new move was sent.
      throw new IllegalStateException("Previous move in progress!");
    }
    moveInProgress = true;
    
    lastGameState = gameState.copy();
    gameState.makeMove(makeMove.getOperations());
    lastMovePlayerId = playerId;
    lastMove = ImmutableList.copyOf(makeMove.getOperations());
    // Verify the move by all players
    for (int verifyingPlayerId : playerIds) {
      //TODO: Should this be sent to player making the move as well?
      verifiers.add(verifyingPlayerId);
      int verifyingPlayerIndex = getPlayerIndex(verifyingPlayerId);
      graphics.sendMessage(verifyingPlayerIndex, new VerifyMove(playersInfo,
          gameState.getStateForPlayerId(playerId),
          lastGameState.getStateForPlayerId(playerId),
          makeMove.getOperations(), playerId,
          gameState.getPlayerIdToNumberOfTokensInPot()));
    }
  }
  
  private void handleVerifyMoveDone(VerifyMoveDone verifyMoveDone, int verifyingPlayerId) {
    if(verifyMoveDone.getHackerPlayerId() == 0) {
      verifiers.remove(new Integer(verifyingPlayerId));
      //TODO: make this condition lenient?
      if(verifiers.size() == 0) {
        // Verified by all
        for(int playerId : playerIds) {
          int playerIndex = getPlayerIndex(playerId);
          graphics.sendMessage(playerIndex, new UpdateUI(playerId, playersInfo,
              gameState.getStateForPlayerId(playerId),
              lastGameState.getStateForPlayerId(playerId),
              lastMove, lastMovePlayerId, gameState.getPlayerIdToNumberOfTokensInPot()));
        }
        moveInProgress = false;
      }
    }
    else {
      //TODO: Handle hacker!
      graphics.logToConsole("Hacker detected! -- " + lastMovePlayerId);
    }
  }

}
