package org.smg.gwt.emulator.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smg.gwt.emulator.data.GameApi.*;

public class GameApiJsonHelper {
  
  public static String getJsonString(Message messageObject) {
    return new JSONObject(messageObject.toMessage()).toString();
  }
  
  public static Message getMessageObject(String jsonString) throws JSONException {
    return (Message) getObject(new JSONObject(jsonString));
  }
  
  @SuppressWarnings({ "unchecked" })
  static Object getObject(Object object) {
    try {
      if (object == null) {
        return object;
      }
      if (object instanceof Integer || object instanceof Double
          || object instanceof String || object instanceof Boolean) {
        return object;
      }
      if (object instanceof JSONArray) {
        JSONArray array = (JSONArray) object;
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); ++i) {
          list.add(getObject(array.get(i)));
        }
        return list;
      }
      if (object instanceof JSONObject) {
        JSONObject message = (JSONObject) object;
        if (message.has("type")) {
          String type = (String) message.get("type");
          switch (type) {
            case "UpdateUI":
              return new UpdateUI(
                  (Integer) message.get("yourPlayerId"),
                  (List<Map<String, Object>>) getObject(message.get("playersInfo")),
                  (Map<String, Object>) getObject(message.get("state")),
                  (Map<String, Object>) getObject(message.get("lastState")),
                  (List<Operation>) getObject(message.get("lastMove")),
                  (Integer) message.get("lastMovePlayerId"),
                  toIntegerMap(getObject(message.get("playerIdToNumberOfTokensInPot"))));
    
            case "VerifyMove":
              return new VerifyMove(
                  (List<Map<String, Object>>) getObject(message.get("playersInfo")),
                  (Map<String, Object>) getObject(message.get("state")),
                  (Map<String, Object>) getObject(message.get("lastState")),
                  (List<Operation>) getObject(message.get("lastMove")),
                  (Integer) message.get("lastMovePlayerId"),
                  toIntegerMap(getObject(message.get("playerIdToNumberOfTokensInPot"))));
    
            case "EndGame":
              return new EndGame(toIntegerMap(getObject(message.get("playerIdToScore"))));
    
            case "Set":
             if (message.get("visibleToPlayerIds").equals("ALL")) {
               return new Set((String) message.get("key"),
                   getObject(message.get("value")));
             }
              return new Set((String) message.get("key"),
                  getObject(message.get("value")),
                  (List<Integer>) getObject(message.get("visibleToPlayerIds")));
    
            case "SetRandomInteger":
              return new SetRandomInteger(
                  (String) message.get("key"),
                  (Integer) message.get("from"),
                  (Integer) message.get("to"));
    
            case "SetVisibility":
              if (message.get("visibleToPlayerIds").equals("ALL")) {
                return new SetVisibility((String) message.get("key"));
              }
              return new SetVisibility(
                  (String) message.get("key"),
                  (List<Integer>) getObject(message.get("visibleToPlayerIds")));
    
            case "SetTurn":
              return new SetTurn((Integer) message.get("playerId"),
                  (Integer) message.get("numberOfSecondsForTurn"));
    
            case "Delete":
              return new Delete((String) message.get("key"));
    
            case "AttemptChangeTokens":
              return new AttemptChangeTokens(toIntegerMap(getObject(
                  message.get("playerIdToTokenChange"))),
                      toIntegerMap(getObject(message.get("playerIdToNumberOfTokensInPot"))));
    
            case "Shuffle":
              return new Shuffle((List<String>) getObject(message.get("keys")));
    
            case "GameReady":
              return new GameReady();
    
            case "MakeMove":
              return new MakeMove((List<Operation>) getObject(message.get("operations")));
    
            case "VerifyMoveDone":
              return new VerifyMoveDone(
                  (Integer) message.get("hackerPlayerId"),
                  (String) message.get("message"));
    
            case "RequestManipulator":
              return new RequestManipulator();
    
            case "ManipulateState":
              return new ManipulateState((Map<String, Object>) getObject(message.get("state")));
    
            case "ManipulationDone":
              return new ManipulationDone((List<Operation>) getObject(message.get("operations")));
            default:
              return null;
          }
        } else {
          JSONObject jsonObject = (JSONObject) object;
          Map<String, Object> innerMap = new HashMap<String, Object>();
          String [] names = JSONObject.getNames(jsonObject);
          if (names != null) {
            for (String name:names) {
              innerMap.put(name, getObject(jsonObject.get(name)));
            }
          }
          return innerMap;
        }
      }
    }
    catch(Exception e) {
      return null;
    }
    return null;
  }
  
  private static Map<Integer, Integer> toIntegerMap(Object objMap) {
    Map<?, ?> map = (Map<?, ?>) objMap;
    Map<Integer, Integer> result = new HashMap<>();
    for (Object key : map.keySet()) {
      Object value = map.get(key);
      result.put(key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString()),
          value instanceof Integer ? (Integer) value : Integer.parseInt(value.toString()));
    }
    return result;
  }

}
