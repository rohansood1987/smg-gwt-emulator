package org.smg.gwt.emulator.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.game_api.GameApi.EndGame;
import org.game_api.GameApi.GameApiJsonHelper;
import org.game_api.GameApi.Message;
import org.game_api.GameApi.Operation;
import org.smg.gwt.emulator.backend.ServerEmulator;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;

public class GwtEmulatorGraphics extends Composite {
  public interface GwtEmulatorGraphicsUiBinder extends UiBinder<Widget, GwtEmulatorGraphics> {
  }
  
  @UiField
  TextBox txtGameWidth;
  
  @UiField
  TextBox txtGameHeight;
  
  @UiField
  TextBox txtGameUrl;
  
  @UiField
  Button btnStart;
  
  @UiField
  Button btnReLoadEmulator;
  
  @UiField
  TextArea console;
  
  @UiField
  Button btnEditState;
  
  @UiField
  ListBox listNumPlayers;
  
  @UiField
  Button btnSaveState;
  
  @UiField
  Button btnLoadState;
  
  @UiField
  AbsolutePanel mainConfigPanel;

  @UiField
  HorizontalPanel gamePanel;

  @UiField
  AbsolutePanel gameTabsPanel;
  
  @UiField
  AbsolutePanel sliderBarPanel;
  
  
  private TabLayoutPanel gameTabs;
  private ServerEmulator serverEmulator;
  private SliderBar sliderBar;
  private boolean change = false;
  private int gameFrameWidth, gameFrameHeight, numberOfPlayers;
  private Storage stateStore;
  private FlexTable flexTable;
  private ClickHandler clearAllButtonHandler;
  private PopupLoadState displayLoadPopUp;
  private List<Frame> playerFrames = new ArrayList<Frame>();
  private String gameUrl;
  private static final int MIN_PLAYERS = 2;
  private static final int MAX_PLAYERS = 9;
  
  public GwtEmulatorGraphics() {
    GwtEmulatorGraphicsUiBinder uiBinder = GWT.create(GwtEmulatorGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    gamePanel.setVisible(false);
  }
  
  @UiHandler("btnStart")
  void onClickStartButton(ClickEvent e) {
    initEmulator();
  }
  
  @UiHandler("btnReLoadEmulator")
  void onClickReLoadButton(ClickEvent e) {
    new PopupReloadEmulator().center();
  }
  
  private void clearEmulator() {
    if (gameTabs != null) {
      gameTabsPanel.remove(gameTabs);
      gameTabs.clear();
      playerFrames.clear();
      sliderBarPanel.clear();
      removeEventListener();
    }
  }
  
  private void initEmulator() {
    if (!validatateAndInitConfigInput()) {
      return;
    }
    // initialize ServerEmulator
    serverEmulator = new ServerEmulator(numberOfPlayers, this);
    clearEmulator();
    gameTabs = new TabLayoutPanel(1.5, Unit.EM);
    gameTabs.setSize(gameFrameWidth + "px", (gameFrameHeight + 25) + "px");
    for (int i = 0; i < numberOfPlayers; i++) {
      Frame frame = new Frame(gameUrl);
      frame.getElement().setId("frame" + i);
      frame.setSize("100%", "100%");
      gameTabs.add(frame, "Player " + serverEmulator.getPlayerIds().get(i));
      playerFrames.add(frame);
    }
    gameTabsPanel.add(gameTabs);
    console.setSize("400px", "500px");
    console.setEnabled(false);
    injectEventListener(serverEmulator, numberOfPlayers);
    addSlider(0);
    mainConfigPanel.setVisible(false);
    gamePanel.setVisible(true);
    addSaveStateTable();
    btnReLoadEmulator.setVisible(true);
  }
  
  private boolean validatateAndInitConfigInput() {
    try {
      gameFrameWidth = Integer.parseInt(txtGameWidth.getText());
    }
    catch(NumberFormatException ex) {
      alert("Invalid width: " + txtGameWidth.getText());
      return false;
    }
    try {
      gameFrameHeight = Integer.parseInt(txtGameHeight.getText());
    }
    catch(NumberFormatException ex) {
      alert("Invalid height: " + txtGameHeight.getText());
      return false;
    }
    try {
      numberOfPlayers = Integer.parseInt(
          listNumPlayers.getValue(listNumPlayers.getSelectedIndex()));
    } catch (Exception ex) {
      alert("Invalid number of Players");
      return false;
    }
    try {
      gameUrl = txtGameUrl.getText();
      String parameterUrl = "";
      String urlWithoutParams = new String(gameUrl);
      gameUrl = gameUrl.trim();
      int paramStartIndex = gameUrl.indexOf('?');
      String newParam = "cache_buster";
      String newValue = String.valueOf(Random.nextInt(Integer.MAX_VALUE));
      if (paramStartIndex != -1) {
        parameterUrl = gameUrl.substring(paramStartIndex + 1);
        urlWithoutParams = gameUrl.substring(0, paramStartIndex);
      }
      gameUrl = urlWithoutParams + "?" + insertParam(parameterUrl, newParam, newValue);
    } catch (Exception ex) {
      alert("Invalid URL: " + gameUrl);
      return false;
    }
    return true;
  }

  //paramsInUrl is the URL after '?'
  private String insertParam(String paramsInUrl, String param, String value) {
    String[] paramKeyValues = paramsInUrl.split("&");
    List<String> newParamKeyValues = new ArrayList<String>();
    boolean found = false;
    for (int i = 0; i < paramKeyValues.length; ++i) {
      if (paramKeyValues[i].length() != 0) {
        String [] keyValue = paramKeyValues[i].split("=");
        if (keyValue.length == 2) {
          String key = keyValue[0];
          if (key.equals(param)) {
            paramKeyValues[i] = key + "=" + value;
            found = true;
          }
          newParamKeyValues.add(paramKeyValues[i]);
        }
      }
    }
    if (!found) {
      newParamKeyValues.add(param + "=" + value);
    }
    StringBuilder newParamsBuilder = new StringBuilder();
    for (int i = 0; i < newParamKeyValues.size() - 1; ++i) {
      newParamsBuilder.append(newParamKeyValues.get(i));
      newParamsBuilder.append("&");
    }
    newParamsBuilder.append(newParamKeyValues.get(newParamKeyValues.size() - 1));
    return newParamsBuilder.toString();
  }
  
  private void addSlider(int maxValue) {
    change = false;
    sliderBar = new SliderBar(maxValue, "100%");
    sliderBar.drawMarks("white", 10);
    sliderBar.addBarValueChangedHandler(new BarValueChangedHandler() {
      @Override
      public void onBarValueChanged(BarValueChangedEvent event) {
        if (getAndSetBooleanValue()) {
          serverEmulator.currentSliderIndex = event.getValue();
          String jsonState = serverEmulator.getSavedStateAtIndex(event.getValue());
          if (jsonState != null) {
            serverEmulator.loadGameStateFromJSON(JSONParser.parseStrict(jsonState).isObject());
          }
        }
      }
    });
    sliderBarPanel.add(sliderBar);
  }
  
  private boolean getAndSetBooleanValue() {
    boolean value = change;
    change = true;
    return value;
  }
  
  public void incrementSliderMaxValue(int value) {
    sliderBarPanel.remove(sliderBar);
    addSlider(value);
    change = false;
    sliderBar.setValue(value);
  }

  @UiHandler("btnEditState")
  void onClickEditStateButton(ClickEvent e) {
    new PopupEditState(serverEmulator.getStateAsString(), serverEmulator.getVisibilityMapAsString(),
        serverEmulator.getTokensMapAsString(),
        new PopupEditState.StateEntered() {      
          @Override
          public void setUpdatedStateInfo(Map<String,Object> updatedState,
              Map<String,Object> updatedVisibilityMap, Map<String,Integer> updatedTokensMap) {
            serverEmulator.updateStateManually(updatedState, updatedVisibilityMap, updatedTokensMap);
          }
    }).center();
  }
  
  private void addSaveStateTable() {
    stateStore = Storage.getLocalStorageIfSupported();
    if (stateStore == null) {
      btnSaveState.setEnabled(false);
      btnLoadState.setEnabled(false);
      return;
    }
    flexTable = new FlexTable();
    addHeaderToTable();
    flexTable.setBorderWidth(2);
    for (int i = 0; i < stateStore.getLength(); i++){
      flexTable.setText(i + 1, 0, stateStore.key(i));
      Button load = new Button("Load");
      Button clear = new Button("Clear");
      addLoadClearButtonHandlers(load, clear, i + 1);
      flexTable.setWidget(i + 1, 1, load);
      flexTable.setWidget(i + 1, 2, clear);
    }
    displayLoadPopUp = new PopupLoadState(flexTable);
    displayLoadPopUp.hide();
  }
  
  private void addHeaderToTable() {
    flexTable.setText(0, 0, "Saved State Name");
    flexTable.setText(0, 1, "Load Option");
    Button clearAll = new Button("Clear All");
    clearAll.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        stateStore.clear();
        flexTable.removeAllRows();
        addHeaderToTable();
        displayLoadPopUp.center();
      }
    });
    flexTable.setWidget(0, 2, clearAll);
  }
  
  private void addLoadClearButtonHandlers(Button load, Button clear, final int row) {
    load.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        String key = flexTable.getText(row, 0);
        String content = stateStore.getItem(key);
        serverEmulator.loadGameStateFromJSON(JSONParser.parseStrict(content).isObject());
        serverEmulator.resetSliderState();
        displayLoadPopUp.hide();
      }
    });
    
    clear.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        String key = flexTable.getText(row, 0);
        stateStore.removeItem(key);
        flexTable.removeRow(row);
        displayLoadPopUp.center();
      }
    });
  }

  @UiHandler("btnSaveState")
  void onClickSaveStateButton(ClickEvent e) {
    final String data = serverEmulator.saveGameStateJSONAsString();
    Set<String> keySet = new HashSet<String>(); 
    for (int i = 0; i < stateStore.getLength(); i++) {
      keySet.add(stateStore.key(i));
    }
    new PopupSaveState(new PopupSaveState.NameEntered() {
      @Override
      public void setName(String name) {
        stateStore.setItem(name, data);
        int row  = flexTable.getRowCount();
        flexTable.setText(row + 1, 0, name);
        Button load = new Button("Load");
        Button clear = new Button("Clear");
        addLoadClearButtonHandlers(load, clear, row + 1);
        flexTable.setWidget(row + 1, 1, load);
        flexTable.setWidget(row + 1, 2, clear);
        Window.alert("State saved successfully. Press Load button to load any saved states");
      }
    }, keySet).center();
  }
   
  @UiHandler("btnLoadState")
  void onClickLoadStateButton(ClickEvent e) {
    displayLoadPopUp.center();
  }
  
  private native void injectEventListener(ServerEmulator emulator, int numberOfPlayers) /*-{
    $wnd.postMessageListener = function(e) {
      //alert("Total players " + numberOfPlayers);
      for(var i = 0; i < numberOfPlayers; i++) {
        var frameName = "frame"+i;
        var frame = $doc.getElementById(frameName);
        //if(frame) { $wnd.alert(frameName + " exists");}
        //else {$wnd.alert(frameName + " doesnt exist");}
        if(e.source == frame.contentWindow || e.source.parent == frame.contentWindow) {
          //$wnd.alert(frameName + " attached!");
          //$wnd.alert(JSON.stringify(e.data));
          emulator.@org.smg.gwt.emulator.backend.ServerEmulator::eventListner(Ljava/lang/String;I)(JSON.stringify(e.data), i);
        }
      }
    }
    $wnd.addEventListener("message", $wnd.postMessageListener, false);
  }-*/;
  
  private native void removeEventListener() /*-{
    $wnd.removeEventListener("message", $wnd.postMessageListener, false);
  }-*/;
  
  public void sendMessage(int playerIndex, Message message) {
    String jsonStr = GameApiJsonHelper.getJsonString(message);
    logToConsole("Sending message: " + jsonStr);
    postMessageToFrame("frame"+playerIndex, jsonStr);
  }
  
  private static native void postMessageToFrame(String frameName, String message) /*-{
    $doc.getElementById(frameName).contentWindow.postMessage(JSON.parse(message), "*");
  }-*/;
  
  private native void alert(String message) /*-{
    alert(message);
  }-*/;
  
  public void logToConsole(String msg) {
    console.setText(console.getText() + "\n\n" + msg);
  }
  
  public void addPlayerFrames(int newFrames, int oldTotalPlayers) {
    removeEventListener();
    for (int i = oldTotalPlayers; i < oldTotalPlayers + newFrames; ++i) {
      Frame frame = new Frame(gameUrl);
      frame.getElement().setId("frame" + i);
      frame.setSize("100%", "100%");
      gameTabs.add(frame, "Player " + serverEmulator.getPlayerIds().get(i));
      playerFrames.add(frame);
    }
    injectEventListener(serverEmulator, oldTotalPlayers + newFrames);
  }
  
  public void removePlayerFrames(int framesToRemove, int oldTotalPlayers) {
    removeEventListener();
    for (int i = oldTotalPlayers - 1; i >= oldTotalPlayers - framesToRemove; --i) {
      Frame frame = playerFrames.remove(i);
      gameTabs.remove(frame);
    }
    injectEventListener(serverEmulator, oldTotalPlayers - framesToRemove);
  }
  
  private void resetConfigPanelFields() {
    txtGameWidth.setText(String.valueOf(gameFrameWidth));
    txtGameHeight.setText(String.valueOf(gameFrameHeight));
    listNumPlayers.setItemSelected(numberOfPlayers - MIN_PLAYERS, true);
    txtGameUrl.setText(gameUrl);
  }
  
  public void handleGameOver(EndGame endGameOpn) {
    new PopupGameOver(endGameOpn).center();
  }
  
  private class PopupReloadEmulator extends DialogBox {
    
    PopupReloadEmulator() {
      setText("Reload Emulator");
      Button btnCancel = new Button("Cancel");
      Button btnReset = new Button("Reset");
      Button btnReload = new Button("Reload");
      final Label lblStatus = new Label("Please select the reload parameters");
      resetConfigPanelFields();
      
      btnCancel.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          hide();
        }
      });
      
      btnReset.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          resetConfigPanelFields();
        }
      });
      
      btnReload.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          try {
            hide();
            initEmulator();
          }
          catch(Exception ex) {
            lblStatus.setText("Please enter valid information");
          }
        }
      });
      VerticalPanel mainVertPanel = new VerticalPanel();
      mainVertPanel.add(lblStatus);
      mainVertPanel.add(new Label("Number of Players:"));
      mainVertPanel.add(listNumPlayers);
      mainVertPanel.add(new Label("Frame Width:"));
      mainVertPanel.add(txtGameWidth);
      mainVertPanel.add(new Label("Frame Height: "));
      mainVertPanel.add(txtGameHeight);
      mainVertPanel.add(new Label("Game URL:"));
      mainVertPanel.add(txtGameUrl);
      HorizontalPanel btnsPanel = new HorizontalPanel();
      btnsPanel.add(btnCancel);
      btnsPanel.add(btnReset);
      btnsPanel.add(btnReload);
      mainVertPanel.add(btnsPanel);
      setWidget(mainVertPanel);
    }
  }
  
  private class PopupGameOver extends DialogBox {
    public PopupGameOver(EndGame endGame) {
      
      Map<String, Integer> scores = endGame.getPlayerIdToScore();
      List<String> playerIds = serverEmulator.getPlayerIds();
      
      setText("Game Over");
      
      Button btnRestartGame = new Button("Restart");
      btnRestartGame.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          hide();
          new PopupReloadEmulator().center();
        }
      });
      
      Button btnCancel = new Button("Cancel");
      btnCancel.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          hide();
        }
      });
      
      int maxScore = 0;
      for(String playerId : playerIds) {
        if(scores.get(playerId) > maxScore) {
          maxScore = scores.get(playerId);
        }
      }
      
      FlexTable scoreTable = new FlexTable();
      scoreTable.setStyleName("scoreTable");
      scoreTable.setBorderWidth(1);
      scoreTable.setText(0, 0, "Player");
      scoreTable.setText(0, 1, "Score");
      scoreTable.getFlexCellFormatter().setStyleName(0, 0, "headerRow");
      scoreTable.getFlexCellFormatter().setStyleName(0, 1, "headerRow");
      
      for (int i = 0; i < playerIds.size(); i++) {
        String playerId = playerIds.get(i);
        int score = scores.get(playerId);
        scoreTable.setText(i + 1, 0, "Player " + playerId);
        scoreTable.setText(i + 1, 1, "" + score);
        if (score == maxScore) {
            scoreTable.getFlexCellFormatter().setStyleName(i + 1, 0, "winnerRow");
            scoreTable.getFlexCellFormatter().setStyleName(i + 1, 1, "winnerRow");
        }
      }
      
      VerticalPanel mainPanel = new VerticalPanel();
      mainPanel.add(new Label("Final Scores:"));
      mainPanel.add(scoreTable);
      HorizontalPanel buttonsPanel = new HorizontalPanel();
      buttonsPanel.add(btnCancel);
      buttonsPanel.add(btnRestartGame);
      mainPanel.add(buttonsPanel);
      setWidget(mainPanel);
    }
  }
}