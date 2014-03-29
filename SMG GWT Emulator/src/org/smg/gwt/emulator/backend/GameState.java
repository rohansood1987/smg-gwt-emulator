package org.smg.gwt.emulator.backend;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.game_api.GameApi.AttemptChangeTokens;
import org.game_api.GameApi.Delete;
import org.game_api.GameApi.Operation;
import org.game_api.GameApi.Set;
import org.game_api.GameApi.SetRandomInteger;
import org.game_api.GameApi.SetVisibility;
import org.game_api.GameApi.Shuffle;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GameState {

  private final Map<String, Object> state = Maps.newHashMap();
  private final Map<String, Object> visibleTo = Maps.newHashMap();
  private final Map<String, Integer> playerIdToNumberOfTokensInPot = Maps.newHashMap();
  
  private static final String ALL = "ALL"; 

  public GameState copy() {
    GameState result = new GameState();
    result.state.putAll(state);
    result.visibleTo.putAll(visibleTo);
    result.playerIdToNumberOfTokensInPot.putAll(playerIdToNumberOfTokensInPot);
    return result;
  }
  
  public Map<String, Integer> getPlayerIdToNumberOfTokensInPot() {
    return playerIdToNumberOfTokensInPot;
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> getStateForPlayerId(String playerId) {
    Map<String, Object> result = Maps.newHashMap();
    for (String key : state.keySet()) {
      Object visibleToPlayers = visibleTo.get(key);
      Object value = null;
      if (visibleToPlayers.equals(ALL)
          || ((List<String>) visibleToPlayers).contains(playerId)) {
        value = state.get(key);
      }
      result.put(key, value);
    }
    return result;
  }
  
  public Map<String, Object> getMasterState() {
    return state;
  }
  
  public Map<String, Object> getMasterVisibilityMap() {
    return visibleTo;
  }
  
  public Map<String, Integer> getMasterTokensMap() {
    return playerIdToNumberOfTokensInPot;
  }

  public void makeMove(List<Operation> operations) {
    for (Operation operation : operations) {
      makeMove(operation);
    }
  }

  public void makeMove(Operation operation) {
    if (operation instanceof Set) {
      Set set = (Set) operation;
      String key = set.getKey();
      state.put(key, set.getValue());
      visibleTo.put(key, set.getVisibleToPlayerIds());
    } else if (operation instanceof SetRandomInteger) {
      SetRandomInteger setRandomInteger = (SetRandomInteger) operation;
      String key = setRandomInteger.getKey();
      int from = setRandomInteger.getFrom();
      int to = setRandomInteger.getTo();
      int value = new Random().nextInt(to - from) + from;
      state.put(key, value);
      visibleTo.put(key, ALL);
    } else if (operation instanceof SetVisibility) {
      SetVisibility setVisibility = (SetVisibility) operation;
      String key = setVisibility.getKey();
      visibleTo.put(key, setVisibility.getVisibleToPlayerIds());
    } else if (operation instanceof Delete) {
      Delete delete = (Delete) operation;
      String key = delete.getKey();
      state.remove(key);
      visibleTo.remove(key);
    } else if (operation instanceof Shuffle) {
      Shuffle shuffle = (Shuffle) operation;
      List<String> keys = shuffle.getKeys();
      List<String> shuffledKeys = shuffle(Lists.newArrayList(keys));
      Map<String, Object> oldState = ImmutableMap.copyOf(state);
      Map<String, Object> oldVisibleTo = ImmutableMap.copyOf(visibleTo);
      for (int i = 0; i < keys.size(); i++) {
        String fromKey = keys.get(i);
        String toKey = shuffledKeys.get(i);
        state.put(toKey, oldState.get(fromKey));
        visibleTo.put(toKey, oldVisibleTo.get(fromKey));
      }
    } else if (operation instanceof AttemptChangeTokens) {
      playerIdToNumberOfTokensInPot.clear();
      playerIdToNumberOfTokensInPot.putAll(
          ((AttemptChangeTokens) operation).getPlayerIdToNumberOfTokensInPot());
    }
  }

  private List<String> shuffle(List<String> list) {
    List<String> listCopy = Lists.newArrayList(list);
    Random rnd = new Random();
    List<String> res = Lists.newArrayList();
    while (!listCopy.isEmpty()) {
      int index = rnd.nextInt(listCopy.size());
      res.add(listCopy.remove(index));
    }
    return res;
  }

  public void setManualState(
      Map<String, Object> manualState,
      Map<String, Object> manualVisibilityMap,
      Map<String, Integer> manualTokensMap) {
    state.clear();
    state.putAll(manualState);
    visibleTo.clear();
    visibleTo.putAll(manualVisibilityMap);
    playerIdToNumberOfTokensInPot.clear();
    playerIdToNumberOfTokensInPot.putAll(manualTokensMap);
  }

}
