package org.smg.gwt.emulator.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.smg.gwt.emulator.client.GwtEmulatorGraphics;
import org.smg.gwt.emulator.data.GameApi.GameReady;
import org.smg.gwt.emulator.data.GameApi.MakeMove;
import org.smg.gwt.emulator.data.GameApi.Message;
import org.smg.gwt.emulator.data.GameApi.Operation;
import org.smg.gwt.emulator.data.GameApi.SetTurn;
import org.smg.gwt.emulator.data.GameApi.UpdateUI;
import org.smg.gwt.emulator.data.GameApi.VerifyMove;
import org.smg.gwt.emulator.data.GameApi.VerifyMoveDone;
import org.smg.gwt.emulator.data.GameApiJsonHelper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

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
  private List<String> savedStates = new ArrayList<String>();
  public int currentSliderIndex = -1;
  
  public static final String PLAYER_ID = "playerId";
  private static final int firstPlayerId = 1;
  
  private boolean moveInProgress;
  private List<Integer> verifiers = Lists.newArrayList();
  private int countGameReady = 0;
  
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
    graphics.logToConsole("Setup done for " + numberOfPlayers + " players.");
    countGameReady = 0;
  }
  
  public List<Integer> getPlayerIds() {
    return playerIds;
  }
  
  private int getPlayerIndex(int playerId) {
    return playerIds.indexOf(playerId);
  }
  
  public void eventListner(String message, int playerIndex) {
    
    int playerId = playerIds.get(playerIndex);
    graphics.logToConsole("Message from [" + playerId + "]: " + message);
    
    Message messageObj = null;
    try {
      messageObj = GameApiJsonHelper.getMessageObject(message);
    }
    catch(Exception ex) {
      graphics.logToConsole("<err> cant parse json. " + ex.getMessage());
    }
    
    if (messageObj instanceof GameReady) {
      handleGameReady((GameReady)messageObj, playerId);
    }
    else if (messageObj instanceof MakeMove) {
      handleMakeMove((MakeMove)messageObj, playerId);
    }
    else if (messageObj instanceof VerifyMoveDone) {
      handleVerifyMoveDone((VerifyMoveDone)messageObj, playerId);
    }
    else {
      graphics.logToConsole("<err> no instance found");
    }
  }

  private void handleGameReady(GameReady gameReady, int playerId1) {
    // Send initial UpdateUI message
    graphics.logToConsole("handling game ready");
    countGameReady++;
    if (countGameReady == numberOfPlayers) {
      for(int playerId : playerIds) {
      int playerIndex = getPlayerIndex(playerId);
      graphics.sendMessage(playerIndex, new UpdateUI(playerId, playersInfo,
            gameState.getStateForPlayerId(playerId),
            null, new ArrayList<Operation>(), playerId,
            gameState.getPlayerIdToNumberOfTokensInPot()));
      }
     }
  }
  
  private void handleMakeMove(MakeMove makeMove, int playerId) {
    graphics.logToConsole("handling make move");
    if (moveInProgress) {
      //TODO:Handle the case where previous move was in progress and a new move was sent.
      throw new IllegalStateException("Previous move in progress!");
    }
    moveInProgress = true;
    
    lastGameState = gameState.copy();
    gameState.makeMove(makeMove.getOperations());
    lastMovePlayerId = playerId;
    lastMove = ImmutableList.copyOf(makeMove.getOperations());
    // Add all playerids to verifiers list before sending verifyMove message
    for (int verifyingPlayerId : playerIds) { 
      verifiers.add(verifyingPlayerId);
    }
    // Verify the move by all players
    for (int verifyingPlayerId : playerIds) {
      //TODO: Should this be sent to player making the move as well?
      //verifiers.add(verifyingPlayerId);
      int verifyingPlayerIndex = getPlayerIndex(verifyingPlayerId);
      graphics.sendMessage(verifyingPlayerIndex, new VerifyMove(playersInfo,
          gameState.getStateForPlayerId(playerId),
          lastGameState.getStateForPlayerId(playerId),
          makeMove.getOperations(), playerId,
          gameState.getPlayerIdToNumberOfTokensInPot()));
    }
  }
  
  private void handleVerifyMoveDone(VerifyMoveDone verifyMoveDone, int verifyingPlayerId) {
    graphics.logToConsole("handling verify move done");
    if(verifyMoveDone.getHackerPlayerId() == 0) {
      verifiers.remove(new Integer(verifyingPlayerId));
      //TODO: make this condition lenient?
      if(verifiers.size() == 0) {
        // Verified by all
        sendUpdateStateToAllPlayers();
        while (currentSliderIndex < savedStates.size() - 1) {
             savedStates.remove(currentSliderIndex + 1);
        }
        savedStates.add(saveGameStateJSONAsString());
        currentSliderIndex = savedStates.size() - 1;
        graphics.incrementSliderMaxValue(currentSliderIndex);
        moveInProgress = false;
      }
    }
    else {
      //TODO: Handle hacker!
      graphics.logToConsole("Hacker detected! -- " + lastMovePlayerId);
    }
  }
  
  private void sendUpdateStateToAllPlayers() {
    for(int playerId : playerIds) {
      int playerIndex = getPlayerIndex(playerId);
      graphics.sendMessage(playerIndex, new UpdateUI(playerId, playersInfo,
          gameState.getStateForPlayerId(playerId),
          lastGameState.getStateForPlayerId(playerId),
          lastMove, lastMovePlayerId, gameState.getPlayerIdToNumberOfTokensInPot()));
    }
  }

  public String getStateAsString() {
    return GameApiJsonHelper.getJsonStringFromMap(gameState.getMasterState());
  }
  
  public String getVisibilityMapAsString() {
    return GameApiJsonHelper.getJsonStringFromMap(gameState.getMasterVisibilityMap());
  }
  
  

  public void updateStateManually(Map<String, Object> state, Map<String, Object> visibilityMap) {
    graphics.logToConsole("Updating state manually: " + state.toString());
    lastGameState = gameState.copy();
    lastMove = Lists.newArrayList((Operation)new SetTurn(getTurnPlayer(lastMove)));
    gameState.setManualState(state, visibilityMap);
    sendUpdateStateToAllPlayers();
  }

  public String saveGameStateJSONAsString() {
    graphics.logToConsole("Saving Game State");
    JSONObject json = new JSONObject();
    json.put("currentState", GameApiJsonHelper.getJsonObject(gameState.getMasterState()));
    json.put("currentVisibilityInfo", GameApiJsonHelper.getJsonObject(gameState.getMasterVisibilityMap()));
    json.put("lastState",  GameApiJsonHelper.getJsonObject(lastGameState.getMasterState()));
    json.put("lastVisibilityInfo", GameApiJsonHelper.getJsonObject(lastGameState.getMasterVisibilityMap()));
    json.put("lastMovePlayerId", new JSONNumber(lastMovePlayerId));
    json.put("currentMovePlayerId", new JSONNumber(getTurnPlayer(lastMove)));
    return json.toString();
  }
  
  public void loadGameStateFromJSON(JSONObject json) {
    graphics.logToConsole("Loading Game State");
    lastGameState.setManualState(
        GameApiJsonHelper.getMapFromJsonObject(json.get("lastState").isObject()), 
        GameApiJsonHelper.getMapFromJsonObject(json.get("lastVisibilityInfo").isObject()));
    lastMovePlayerId = (int)((JSONNumber)json.get("lastMovePlayerId")).doubleValue();
    int currentMovePlayerId = (int)((JSONNumber)json.get("currentMovePlayerId")).doubleValue();
    lastMove = Lists.newArrayList((Operation)new SetTurn(currentMovePlayerId)); 
    gameState.setManualState(
        GameApiJsonHelper.getMapFromJsonObject(json.get("currentState").isObject()), 
        GameApiJsonHelper.getMapFromJsonObject(json.get("currentVisibilityInfo").isObject()));
    sendUpdateStateToAllPlayers();
  }

  public String getSavedStateAtIndex(int index) {
    if (index >= 0 && index < savedStates.size()) {
      currentSliderIndex = index;
      return savedStates.get(index);
    }
    return null;
  }
  
  /**
   * Get the playerId who has the turn.
   * @param operations
   * @return id of player who has the turn
   */
  private int getTurnPlayer(List<Operation> operations) {
    for (Operation operation : operations) {
      if (operation instanceof SetTurn) {
        return ((SetTurn) operation).getPlayerId();
      }
    }
    return lastMovePlayerId; // If not found
  }
}
