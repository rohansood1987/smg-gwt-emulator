package org.smg.gwt.emulator.backend;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.game_api.GameApi;
import org.game_api.GameApi.EndGame;
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
import org.smg.gwt.emulator.client.EnhancedConsole.ConsoleMessageType;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;

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
  public int defaultTurnTimeInSecs;
  public static final String FIRST_PLAYER_ID = "42";
  
  private boolean moveInProgress;
  private boolean singlePlayerMode;
  private int randomDelayMillis;
  
  //players who have verified the current MakeMove
  private Set<String> verifiers = new HashSet<String>();
  
  //players who have sent game ready
  private Set<String> gameReadyPlayers = new HashSet<String>();
  
  private int verifiersSize = 0;
  
  private String currentPlayerIdTurn = FIRST_PLAYER_ID;
  
  private boolean isViewerPresent = false;
  
  //private int countGameReady = 0;
  public ServerEmulator(int numberOfPlayers, GwtEmulatorGraphics graphics, 
      int defaultTurnTimeInSecs, int randomDelayMillis, boolean singlePlayerMode,
          boolean isViewerPresent) {
    gameState = new GameState();
    this.numberOfPlayers = numberOfPlayers;
    this.graphics = graphics;
    this.singlePlayerMode = singlePlayerMode;
    this.isViewerPresent = isViewerPresent;
    this.defaultTurnTimeInSecs = defaultTurnTimeInSecs;
    this.randomDelayMillis = randomDelayMillis;
    setupPlayers();
  }
  
  private void setupPlayers() {
    playerIds = Lists.newArrayList();
    playersInfo = Lists.newArrayList();
    playerIds.addAll(getPlayerIds(numberOfPlayers));
    for(String playerId : playerIds) {
      playersInfo.add(ImmutableMap.<String, Object>of(PLAYER_ID, playerId));
    }
    //playersInfo.add(ImmutableMap.<String, Object>of(PLAYER_ID, GameApi.VIEWER_ID));
    graphics.getConsole().addInfoMessage("Setup done for " + numberOfPlayers + " players.");
    verifiers.clear();
    gameReadyPlayers.clear();
  }
  
  private List<String> getPlayerIds(int numOfPlayers) {
    List<String> playerIdList = Lists.newArrayList();
    for(int i = 0; i < numOfPlayers; i++) {
      String playerId = (Integer.parseInt(FIRST_PLAYER_ID) + i) + "";
      playerIdList.add(playerId);
    }
    return playerIdList;
  }
  
  public List<String> getPlayerIds() {
    return playerIds;
  }
  
  private int getPlayerIndex(String playerId) {
    return playerIds.indexOf(playerId);
  }
  
  public void handleMessage(Message messageObj, int playerIndex) {
    String playerId = playerIds.get(playerIndex);
    if (singlePlayerMode) {
      playerId = currentPlayerIdTurn;
    }
    
    if (messageObj instanceof GameReady) {
      graphics.getConsole().addGameApiMessage(messageObj,
          playerId, ConsoleMessageType.INCOMING);
      handleGameReady((GameReady)messageObj, playerId);
    }
    else if (messageObj instanceof MakeMove) {
      graphics.getConsole().addGameApiMessage(messageObj,
          playerId, ConsoleMessageType.INCOMING);
      handleMakeMove((MakeMove)messageObj, playerId);
    }
    else if (messageObj instanceof VerifyMoveDone) {
      graphics.getConsole().addGameApiMessage(messageObj, "UNKNOWN", ConsoleMessageType.INCOMING);
      handleVerifyMoveDone((VerifyMoveDone)messageObj, playerId);
    }
    else {
      graphics.getConsole().addInfoMessage("<err> no instance found");
    }
  }
  
  public void eventListner(final String message, final int playerIndex) {
    Message messageObj = null;
    try {
      messageObj = GameApiJsonHelper.getMessageObject(message);
    }
    catch(Exception ex) {
      graphics.getConsole().addInfoMessage("<err> cant parse json. " + ex.getMessage());
    }
    if (messageObj != null) {
      final Message messageObject = messageObj;
      Timer delayTimer = new Timer() {
        @Override
        public void run() {
          handleMessage(messageObject, playerIndex);
          this.cancel();
          graphics.showDefaultCursor();
        }
      };
      int delay = Random.nextInt(randomDelayMillis);
      if (delay != 0) {
        graphics.getConsole().addInfoMessage("Message Received: " + messageObj.getMessageName());
        graphics.getConsole().addInfoMessage("Simulating network delay of " + delay + " ms.");
      }
      delayTimer.schedule(delay);
      graphics.showWaitCursor();
      delayTimer = null;
    }
  }
  
  private void handleGameReady(GameReady gameReady, String sendingPlayerId) {
    // Send initial UpdateUI message
    graphics.getConsole().addInfoMessage("Handling game ready from player id " + sendingPlayerId);
    //TODO: map PlayerId's here since some game can send GameReady twice
    //countGameReady++;
    gameReadyPlayers.add(sendingPlayerId);
    if (singlePlayerMode) {
      gameReadyPlayers.addAll(playerIds);
    }
    if (gameReadyPlayers.containsAll(playerIds)) {
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
    graphics.getConsole().addInfoMessage("Handling make move");
    if (moveInProgress) {
      //TODO:Handle the case where previous move was in progress and a new move was sent.
      throw new IllegalStateException("Previous move in progress!");
    }
    moveInProgress = true;
    
    lastGameState = gameState.copy();
    gameState.makeMove(makeMove.getOperations());
    lastMovePlayerId = playerId;
    lastMove = ImmutableList.copyOf(makeMove.getOperations());
    /*// Add all playerids to verifiers list before sending verifyMove message
    for (String verifyingPlayerId : playerIds) { 
      verifiers.add(verifyingPlayerId);
    }*/
    // Verify the move by all players
    for (String verifyingPlayerId : playerIds) {
      //TODO: Should this be sent to player making the move as well?
      //verifiers.add(verifyingPlayerId);
      int verifyingPlayerIndex = getPlayerIndex(verifyingPlayerId);
      if (singlePlayerMode) {
        verifyingPlayerIndex = 0;
      }
      graphics.sendMessageForPlayer(verifyingPlayerIndex, new VerifyMove(playersInfo,
          gameState.getStateForPlayerId(verifyingPlayerId),
          lastGameState.getStateForPlayerId(verifyingPlayerId),
          makeMove.getOperations(), playerId,
          gameState.getPlayerIdToNumberOfTokensInPot()), verifyingPlayerId);
    }
  }
  
  private void handleVerifyMoveDone(VerifyMoveDone verifyMoveDone, String verifyingPlayerId) {
    graphics.getConsole().addInfoMessage("Handling verify move done");
    if(verifyMoveDone.getHackerPlayerId() == null) {
      verifiers.add(verifyingPlayerId);
      verifiersSize++;
      //TODO: make this condition lenient?
      if(verifiers.containsAll(playerIds) || 
          (singlePlayerMode && verifiersSize == playerIds.size())) {
        // Verified by all
        sendUpdateStateToAllPlayers();
        // check for EndGame
        for (Operation operation : lastMove) {
          if(operation instanceof EndGame) {
            graphics.handleGameOver((EndGame)operation);
            break;
          }
        }
        while (currentSliderIndex < savedStates.size() - 1) {
             savedStates.remove(currentSliderIndex + 1);
        }
        savedStates.add(saveGameStateJSONAsString());
        currentSliderIndex = savedStates.size() - 1;
        graphics.incrementSliderMaxValue(currentSliderIndex);
        moveInProgress = false;
        verifiers.clear();
        verifiersSize = 0;
      }
    }
    else {
      //TODO: Handle hacker!
      graphics.getConsole().addInfoMessage("Hacker detected! -- " + lastMovePlayerId);
    }
  }
  
  private void sendUpdateStateToAllPlayers() {
    SetTurn playerTurn = getTurnPlayer(lastMove);
    if (playerTurn != null) {
      currentPlayerIdTurn = playerTurn.getPlayerId();
    }
    if (singlePlayerMode) {
      Map<String, Object> lastPlayerState = null;
      if (lastGameState != null) {
        lastPlayerState = lastGameState.getStateForPlayerId(currentPlayerIdTurn);
      }
      graphics.sendMessageForPlayer(0, new UpdateUI(currentPlayerIdTurn, playersInfo,
          gameState.getStateForPlayerId(currentPlayerIdTurn),
          lastPlayerState, lastMove, lastMovePlayerId,
          gameState.getPlayerIdToNumberOfTokensInPot()), currentPlayerIdTurn);
    } else {
      for(String playerId : playerIds) {
        int playerIndex = getPlayerIndex(playerId);
        Map<String, Object> lastPlayerState = null;
        if (lastGameState != null) {
          lastPlayerState = lastGameState.getStateForPlayerId(playerId);
        }
        graphics.sendMessageForPlayer(playerIndex, new UpdateUI(playerId, playersInfo,
            gameState.getStateForPlayerId(playerId),
            lastPlayerState, lastMove, lastMovePlayerId,
            gameState.getPlayerIdToNumberOfTokensInPot()), playerId);
      }
    }
    
    //Sending update to the viewer
    if (isViewerPresent) {
      Map<String, Object> lastPlayerState = null;
      if (lastGameState != null) {
        lastPlayerState = lastGameState.getStateForPlayerId(GameApi.VIEWER_ID);
      }
      graphics.sendMessageForViewer(new UpdateUI(GameApi.VIEWER_ID, playersInfo,
          gameState.getStateForPlayerId(GameApi.VIEWER_ID),
          lastPlayerState, lastMove, lastMovePlayerId,
          gameState.getPlayerIdToNumberOfTokensInPot()));
    }
    
    //Set timer and turn
    if (playerTurn != null) {
      int turnInSeconds = playerTurn.getNumberOfSecondsForTurn();
      if (turnInSeconds <= 0) {
        turnInSeconds = defaultTurnTimeInSecs;
      }
      if (turnInSeconds <= 0) {
        //infinite time
        graphics.setTurnAndTimer(playerTurn.getPlayerId(), "");
      } else {
        graphics.setTurnAndTimer(playerTurn.getPlayerId(), String.valueOf(
            defaultTurnTimeInSecs));
      }
    } else {
      graphics.resetTimer();
    }
  }

  public String getStateAsString() {
    return GameApiJsonHelper.getJsonStringFromMap(gameState.getMasterState());
  }
  
  public String getVisibilityMapAsString() {
    return GameApiJsonHelper.getJsonStringFromMap(gameState.getMasterVisibilityMap());
  }
  
  @SuppressWarnings("unchecked")
  public String getTokensMapAsString() {
    return GameApiJsonHelper.getJsonStringFromMap((Map<String, Object>)(
        Map<String, ? extends Object>)gameState.getMasterTokensMap());
  }
  
  public void updateStateManually(Map<String, Object> state, Map<String, Object> visibilityMap,
      Map<String, Integer> tokensMap) {
    graphics.getConsole().addInfoMessage("Updating state manually: " + state.toString());
    //lastGameState = gameState.copy();
    //lastMove = Lists.newArrayList((Operation)new SetTurn(getTurnPlayer(lastMove)));
    gameState.setManualState(state, visibilityMap, tokensMap);
    sendUpdateStateToAllPlayers();
  }

  @SuppressWarnings("unchecked")
  public String saveGameStateJSONAsString() {
    graphics.getConsole().addInfoMessage("Saving Game State");
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
    json.put("numberOfPlayers", new JSONNumber(numberOfPlayers));
    return json.toString();
  }
  
  @SuppressWarnings("unchecked")
  public void loadGameStateFromJSON(JSONObject json) {
    graphics.getConsole().addInfoMessage("Loading Game State");
    JSONValue jsonPlayerIdToNumberOfTokensInPot = json.get("playerIdToNumberOfTokensInPot");
    JSONValue jsonLastState = json.get("lastState");
    JSONValue jsonLastVisibilityInfo = json.get("lastVisibilityInfo");
    JSONValue jsonLastMove = json.get("lastMove");
    JSONValue jsonLastMovePlayerId = json.get("lastMovePlayerId");
    JSONValue jsonCurrentState = json.get("currentState");
    JSONValue jsonCurrentVisibilityInfo = json.get("currentVisibilityInfo");
    JSONNumber jsonNumberOfPlayers= (JSONNumber) json.get("numberOfPlayers");
    
    int oldTotalPlayers = numberOfPlayers;
    numberOfPlayers = (int) jsonNumberOfPlayers.doubleValue();
    if (numberOfPlayers != oldTotalPlayers) {
      setupPlayers();
    }
    if (jsonLastState instanceof JSONNull) {
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
          (Map<String, Integer>)(Map<String, ? extends Object>)
              GameApiJsonHelper.getMapFromJsonObject(jsonPlayerIdToNumberOfTokensInPot.isObject()));
    } 
    lastMovePlayerId = ((JSONString)jsonLastMovePlayerId).stringValue();
    //lastMove = Lists.newArrayList((Operation)new SetTurn(currentMovePlayerId));
    if (jsonLastMove instanceof JSONNull) {
      lastMove = null;
    }
    else {
      //TODO: Refactor GameApiJsonHelper to make this simpler
      lastMove = ((MakeMove)Message.messageToHasEquality(GameApiJsonHelper.getMapFromJsonObject(
          (JSONObject)jsonLastMove))).getOperations();
    }
    gameState.setManualState(
        GameApiJsonHelper.getMapFromJsonObject(jsonCurrentState.isObject()), 
        GameApiJsonHelper.getMapFromJsonObject(jsonCurrentVisibilityInfo.isObject()),
        (Map<String, Integer>)(Map<String, ? extends Object>)GameApiJsonHelper.getMapFromJsonObject(
            jsonPlayerIdToNumberOfTokensInPot.isObject()));
    if (numberOfPlayers < oldTotalPlayers) {
      gameReadyPlayers.addAll(playerIds);
      if (!singlePlayerMode) {
        graphics.removePlayerFrames(oldTotalPlayers - numberOfPlayers, oldTotalPlayers);
      }
    }
    if (numberOfPlayers <= oldTotalPlayers) {
      sendUpdateStateToAllPlayers();
    }
    else {
      /*numberOfPlayers > oldTotalPlayers: The new frames will send GameReady and make the 
      gameReadyPlayers.containsAll(playerIds) = true which will send the updated state to 
      all the frames.*/
      gameReadyPlayers.addAll(getPlayerIds(oldTotalPlayers));
      if (!singlePlayerMode) {
        graphics.addPlayerFrames(numberOfPlayers - oldTotalPlayers, oldTotalPlayers);
      } else {
        sendUpdateStateToAllPlayers();
      }
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
  private SetTurn getTurnPlayer(List<Operation> operations) {
    if (operations != null && !operations.isEmpty()) {
      for (Operation operation : operations) {
        if (operation instanceof SetTurn) {
          return ((SetTurn) operation);
        }
      }
    }
    return null; // TODO: If not found?
  }
  
  public void resetSliderState() {
    savedStates.clear();
    savedStates.add(saveGameStateJSONAsString());
    currentSliderIndex = savedStates.size() - 1;
    graphics.incrementSliderMaxValue(currentSliderIndex);
  }
}
