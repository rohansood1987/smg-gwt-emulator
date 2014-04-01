package org.smg.gwt.emulator.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.game_api.GameApi.GameApiJsonHelper;
import org.game_api.GameApi.GameReady;
import org.game_api.GameApi.MakeMove;
import org.game_api.GameApi.Message;
import org.game_api.GameApi.Operation;
import org.game_api.GameApi.SetTurn;
import org.game_api.GameApi.UpdateUI;
import org.game_api.GameApi.VerifyMove;
import org.game_api.GameApi.VerifyMoveDone;
import org.smg.gwt.emulator.client.GwtEmulatorGraphics;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * This class emulates all the functionalities of the server.
 * 
 */
public class ServerEmulator {
  
  private int numberOfPlayers;
  private List<String> playerIds;
  private List<Map<String, Object>> playersInfo;
  private GameState gameState;
  private GameState lastGameState;
  private List<Operation> lastMove = new ArrayList<Operation>();
  private String lastMovePlayerId;
  private GwtEmulatorGraphics graphics;
  private List<String> savedStates = new ArrayList<String>();
  public int currentSliderIndex = -1;
  
  
  private static final JSONNull JSON_NULL = JSONNull.getInstance();
  public static final String PLAYER_ID = "playerId";
  private static final String firstPlayerId = "42";
  
  private boolean moveInProgress;
  private List<String> verifiers = Lists.newArrayList();
  private int countGameReady = 0;
  
  public ServerEmulator(int numberOfPlayers, GwtEmulatorGraphics graphics) {
    gameState = new GameState();
    this.numberOfPlayers = numberOfPlayers;
    this.graphics = graphics;
    setupPlayers();
  }
  
  private void setupPlayers() {
    playerIds = Lists.newArrayList();
    playersInfo = Lists.newArrayList();
    for(int i = 0; i < numberOfPlayers; i++) {
      String playerId = (Integer.parseInt(firstPlayerId) + i) + "";
      playerIds.add(playerId);
      playersInfo.add(ImmutableMap.<String, Object>of(PLAYER_ID, playerId));
    }
    graphics.logToConsole("Setup done for " + numberOfPlayers + " players.");
    countGameReady = 0;
  }
  
  public List<String> getPlayerIds() {
    return playerIds;
  }
  
  private int getPlayerIndex(String playerId) {
    return playerIds.indexOf(playerId);
  }
  
  public void eventListner(String message, int playerIndex) {
    
    String playerId = playerIds.get(playerIndex);
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

  private void handleGameReady(GameReady gameReady, String sendingPlayerId) {
    // Send initial UpdateUI message
    graphics.logToConsole("handling game ready " + (countGameReady + 1));
    //TODO: map PlayerId's here since some game can send GameReady twice
    countGameReady++;
    if (countGameReady == numberOfPlayers) {
      /*for(String playerId : playerIds) {
        int playerIndex = getPlayerIndex(playerId);
        graphics.sendMessage(playerIndex, new UpdateUI(playerId, playersInfo,
              gameState.getStateForPlayerId(playerId),
              null, new ArrayList<Operation>(), playerId,
              gameState.getPlayerIdToNumberOfTokensInPot()));
      }*/
      sendUpdateStateToAllPlayers();
      //isGameReady = true;
    }
  }
  
  private void handleMakeMove(MakeMove makeMove, String playerId) {
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
    for (String verifyingPlayerId : playerIds) { 
      verifiers.add(verifyingPlayerId);
    }
    // Verify the move by all players
    for (String verifyingPlayerId : playerIds) {
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
  
  private void handleVerifyMoveDone(VerifyMoveDone verifyMoveDone, String verifyingPlayerId) {
    graphics.logToConsole("handling verify move done");
    if(verifyMoveDone.getHackerPlayerId() == null) {
      verifiers.remove(verifyingPlayerId);
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
    for(String playerId : playerIds) {
      int playerIndex = getPlayerIndex(playerId);
      Map<String, Object> lastPlayerState = null;
      if (lastGameState != null) {
        lastPlayerState = lastGameState.getStateForPlayerId(playerId);
      }
      graphics.sendMessage(playerIndex, new UpdateUI(playerId, playersInfo,
          gameState.getStateForPlayerId(playerId),
          lastPlayerState, lastMove, lastMovePlayerId,
          gameState.getPlayerIdToNumberOfTokensInPot()));
    }
  }

  public String getStateAsString() {
    return GameApiJsonHelper.getJsonStringFromMap(gameState.getMasterState());
  }
  
  public String getVisibilityMapAsString() {
    return GameApiJsonHelper.getJsonStringFromMap(gameState.getMasterVisibilityMap());
  }
  
  public String getTokensMapAsString() {
    return GameApiJsonHelper.getJsonStringFromMap((Map<String, Object>)(Map<String, ? extends Object>)gameState.getMasterTokensMap());
  }
  
  public void updateStateManually(Map<String, Object> state, Map<String, Object> visibilityMap,
      Map<String, Integer> tokensMap) {
    graphics.logToConsole("Updating state manually: " + state.toString());
    //lastGameState = gameState.copy();
    //lastMove = Lists.newArrayList((Operation)new SetTurn(getTurnPlayer(lastMove)));
    gameState.setManualState(state, visibilityMap, tokensMap);
    sendUpdateStateToAllPlayers();
  }

  public String saveGameStateJSONAsString() {
    graphics.logToConsole("Saving Game State");
    JSONObject json = new JSONObject();
    json.put("playerIdToNumberOfTokensInPot", GameApiJsonHelper.getJsonObject(
        (Map<String, Object>)(Map<String, ? extends Object>)gameState.getPlayerIdToNumberOfTokensInPot()));
    json.put("currentState", GameApiJsonHelper.getJsonObject(gameState.getMasterState()));
    json.put("currentVisibilityInfo", GameApiJsonHelper.getJsonObject(gameState.getMasterVisibilityMap()));
    if (lastGameState != null) {
      json.put("lastState", GameApiJsonHelper.getJsonObject(lastGameState.getMasterState()));
      json.put("lastVisibilityInfo", GameApiJsonHelper.getJsonObject(lastGameState.getMasterVisibilityMap()));
    }
    else {
      json.put("lastState", JSON_NULL);
      json.put("lastVisibilityInfo", JSON_NULL);
    }
    if (lastMove == null) {
      json.put("lastMove", JSONNull.getInstance());
    }
    else {
      json.put("lastMove", GameApiJsonHelper.getJsonObject(new MakeMove(lastMove).toMessage()));
    }
    json.put("lastMovePlayerId", new JSONString(lastMovePlayerId));
    json.put("currentMovePlayerId", new JSONString(getTurnPlayer(lastMove)));
    json.put("numberOfPlayers", new JSONNumber(numberOfPlayers));
    return json.toString();
  }
  
  public void loadGameStateFromJSON(JSONObject json) {
    graphics.logToConsole("Loading Game State");
    JSONValue jsonPlayerIdToNumberOfTokensInPot = json.get("playerIdToNumberOfTokensInPot");
    JSONValue jsonLastState = json.get("lastState");
    JSONValue jsonLastVisibilityInfo = json.get("lastVisibilityInfo");
    JSONValue jsonLastMove = json.get("lastMove");
    JSONValue jsonLastMovePlayerId = json.get("lastMovePlayerId");
    JSONValue jsonCurrentMovePlayerId = json.get("currentMovePlayerId");
    JSONValue jsonCurrentState = json.get("currentState");
    JSONValue jsonCurrentVisibilityInfo = json.get("currentVisibilityInfo");
    JSONNumber jsonNumberOfPlayers= (JSONNumber) json.get("numberOfPlayers");
    
    int oldTotalPlayers = numberOfPlayers;
    numberOfPlayers = (int) jsonNumberOfPlayers.doubleValue();
    if (numberOfPlayers != oldTotalPlayers) {
      setupPlayers();
    }
    if (!(jsonLastState instanceof JSONNull)) {
      lastGameState = null;
    }
    else {
      if (lastGameState == null) {
        lastGameState = new GameState();
      }
      // playerIdToNumberOfTokensInPot for last state doesn't matter
      lastGameState.setManualState(
          GameApiJsonHelper.getMapFromJsonObject(jsonLastState.isObject()), 
          GameApiJsonHelper.getMapFromJsonObject(jsonLastVisibilityInfo.isObject()),
          (Map<String, Integer>)(Map<String, ? extends Object>)GameApiJsonHelper.getMapFromJsonObject(jsonPlayerIdToNumberOfTokensInPot.isObject()));
    } 
    lastMovePlayerId = ((JSONString)jsonLastMovePlayerId).stringValue();
    String currentMovePlayerId = ((JSONString)jsonCurrentMovePlayerId).stringValue();
    //lastMove = Lists.newArrayList((Operation)new SetTurn(currentMovePlayerId));
    if (jsonLastMove instanceof JSONNull) {
      lastMove = null;
    }
    else {
      //TODO: Refactor GameApiJsonHelper to make this simpler
      lastMove = ((MakeMove)Message.messageToHasEquality(GameApiJsonHelper.getMapFromJsonObject((JSONObject)jsonLastMove))).getOperations();
    }
    gameState.setManualState(
        GameApiJsonHelper.getMapFromJsonObject(jsonCurrentState.isObject()), 
        GameApiJsonHelper.getMapFromJsonObject(jsonCurrentVisibilityInfo.isObject()),
        (Map<String, Integer>)(Map<String, ? extends Object>)GameApiJsonHelper.getMapFromJsonObject(jsonPlayerIdToNumberOfTokensInPot.isObject()));
    if (numberOfPlayers < oldTotalPlayers) {
      graphics.removePlayerFrames(oldTotalPlayers - numberOfPlayers, oldTotalPlayers);
    }
    if (numberOfPlayers <= oldTotalPlayers) {
      sendUpdateStateToAllPlayers();
    }
    else {
      /*numberOfPlayers > oldTotalPlayers: The new frames will send GameReady and make the 
      countGameReady = numberOfPlayers which will send the updated state to all the frames.*/
      countGameReady = oldTotalPlayers;
      graphics.addPlayerFrames(numberOfPlayers - oldTotalPlayers, oldTotalPlayers);
    } 
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
  private String getTurnPlayer(List<Operation> operations) {
    if (operations != null && !operations.isEmpty()) {
      for (Operation operation : operations) {
        if (operation instanceof SetTurn) {
          return ((SetTurn) operation).getPlayerId();
        }
      }
    }
    return lastMovePlayerId; // TODO: If not found?
  }
  
  public void resetSliderState() {
    savedStates.clear();
    savedStates.add(saveGameStateJSONAsString());
    currentSliderIndex = savedStates.size() - 1;
    graphics.incrementSliderMaxValue(currentSliderIndex);
  }
}
