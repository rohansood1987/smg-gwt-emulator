package org.smg.gwt.emulator.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smg.gwt.emulator.data.GameApi.Message;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class GameApiJsonHelper {
  
  public static String getJsonString(Message messageObject) {
    Map<String, Object> messageMap = messageObject.toMessage();
    return getJsonObject(messageMap).toString();
  }
  
  private static JSONObject getJsonObject(Map<String, Object> messageMap) {
    JSONObject jsonObj = new JSONObject(); 
    for (Map.Entry<String, Object> entry: messageMap.entrySet()) {
      JSONValue jsonVal = null;
      if (entry.getValue() == null) {
        jsonVal = JSONNull.getInstance();
      }
      else if (entry.getValue() instanceof Boolean) {
        jsonVal = JSONBoolean.getInstance((Boolean)entry.getValue());
      }
      else if (entry.getValue() instanceof Integer) {
        jsonVal = new JSONNumber((Integer)entry.getValue());
      }
      else if (entry.getValue() instanceof String) {
        jsonVal = new JSONString((String)entry.getValue());
      }
      else if (entry.getValue() instanceof List) {
        jsonVal = getJsonArray((List<Object>)entry.getValue());
      }
      else if (entry.getValue() instanceof Map) {
        jsonVal = getJsonObject((Map<String, Object>)entry.getValue());
      }
      else {
        throw new IllegalStateException("Invalid object encountered");
      }
      jsonObj.put(entry.getKey(), jsonVal);
    }
    return jsonObj;
  }
  
  private static JSONArray getJsonArray(List<Object> messageList) {
    JSONArray jsonArr = new JSONArray();
    int index = 0;
    for (Object object: messageList) {
      if (object == null) {
        jsonArr.set(index++, JSONNull.getInstance());
      }
      else if (object instanceof Boolean) {
        jsonArr.set(index++, JSONBoolean.getInstance((Boolean)object));
      }
      else if (object instanceof Integer) {
        jsonArr.set(index++, new JSONNumber((Integer)object));
      }
      else if (object instanceof String) {
        jsonArr.set(index++, new JSONString((String)object));
      }
      else if (object instanceof List) {
        jsonArr.set(index++, getJsonArray((List<Object>)object));
      }
      else if (object instanceof Map) {
        jsonArr.set(index++, getJsonObject((Map<String, Object>)object));
      }
      else {
        throw new IllegalStateException("Invalid object encountered");
      }
    }
    return jsonArr;
  }
  
  public static Message getMessageObject(String jsonString) {
    JSONValue jsonVal = JSONParser.parseStrict(jsonString);
    JSONObject jsonObj = jsonVal.isObject();
    if (jsonObj == null) {
      throw new IllegalStateException("JSONObject expected");
    }
    return Message.messageToHasEquality(getMapFromJsonObject(jsonObj));
  }
  
  private static Map<String, Object> getMapFromJsonObject(JSONObject jsonObj) {
    Map<String, Object> map = new HashMap<String, Object>();
    for (String key : jsonObj.keySet()) {
      JSONValue jsonVal = jsonObj.get(key); 
      if (jsonVal instanceof JSONNull) {
        map.put(key, null);
      }
      else if (jsonVal instanceof JSONBoolean) {
        map.put(key, (Boolean)((JSONBoolean)jsonVal).booleanValue());
      }
      else if (jsonVal instanceof JSONNumber) {
        map.put(key, new Integer((int)((JSONNumber)jsonVal).doubleValue()));
      }
      else if (jsonVal instanceof JSONString) {
        map.put(key, ((JSONString)jsonVal).toString());
      }
      else if (jsonVal instanceof JSONArray) {
        map.put(key, getListFromJsonArray((JSONArray)jsonVal));
      }
      else if (jsonVal instanceof JSONObject) {
        map.put(key, getMapFromJsonObject((JSONObject)jsonVal));
      }
      else {
        throw new IllegalStateException("Invalid JSONValue encountered");
      }
    }
    return map;
  }
  
  private static Object getListFromJsonArray(JSONArray jsonArr) {
    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < jsonArr.size(); i++) {
      JSONValue jsonVal = jsonArr.get(i);
      if (jsonVal instanceof JSONNull) {
        list.add(null);
      }
      else if (jsonVal instanceof JSONBoolean) {
        list.add((Boolean)((JSONBoolean)jsonVal).booleanValue());
      }
      else if (jsonVal instanceof JSONNumber) {
        list.add(new Integer((int)((JSONNumber)jsonVal).doubleValue()));
      }
      else if (jsonVal instanceof JSONString) {
        list.add(((JSONString)jsonVal).toString());
      }
      else if (jsonVal instanceof JSONArray) {
        list.add(getListFromJsonArray((JSONArray)jsonVal));
      }
      else if (jsonVal instanceof JSONObject) {
        list.add(getMapFromJsonObject((JSONObject)jsonVal));
      }
      else {
        throw new IllegalStateException("Invalid JSONValue encountered");
      }
    }
    return list;
  }
}
