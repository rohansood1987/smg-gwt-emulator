package org.smg.gwt.emulator.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.game_api.GameApi;
import org.game_api.GameApi.EndGame;
import org.game_api.GameApi.GameApiJsonHelper;
import org.game_api.GameApi.Message;
import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.CheckBoxButton;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.NavbarNav;
import org.gwtbootstrap3.client.ui.RadioButton;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.smg.gwt.emulator.backend.ServerEmulator;
import org.smg.gwt.emulator.client.EnhancedConsole.ConsoleMessageType;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;

public class GwtEmulatorGraphics extends Composite {
  public interface GwtEmulatorGraphicsUiBinder extends UiBinder<Widget, GwtEmulatorGraphics> {
  }
  
  @UiField
  FlowPanel mainPanel;
  
  @UiField
  HorizontalPanel mainEmulatorPanel;
  
  @UiField
  TextBox txtGameWidth, txtGameHeight, txtDefaultTimePerTurn, txtGameUrl, txtRandomDelayMillis;
  
  @UiField
  CheckBoxButton viewerCheck, singlePlayerCheck, computerPlayerCheck;
  
  @UiField
  org.gwtbootstrap3.client.ui.Button btnStart;
  
  @UiField
  Alert alertBar;
  
  @UiField
  ListItem btnLoadState, btnReloadEmulator, btnSaveState, btnEditState;
  
  @UiField
  ButtonGroup btnsPanel;
  
  @UiField
  org.gwtbootstrap3.client.ui.Button btnCancel, btnReset, btnReload;
  
  @UiField
  RadioButton n2, n3, n4, n5, n6, n7, n8, n9;
  
  @UiField
  FormGroup numOfPlayers, width, height, timeLimit, networkDelay, url;

  @UiField
  AbsolutePanel mainConfigPanel, sliderBarPanel;

  @UiField
  VerticalPanel consolePanel;
  
  @UiField
  TabPanel gameTabsPanel;
  
  @UiField
  Label turnTimerLabel;
  
  @UiField
  NavbarNav headerPanel;
  
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
  private Frame viewerFrame;
  private String gameUrl;
  private EnhancedConsole enhancedConsole;
  private final PopupReloadEmulator popupReloadEmulator = new PopupReloadEmulator();
  private static final int MIN_PLAYERS = 2;
  private static final int MAX_PLAYERS = 9;
  private static final String PLAYER_FRAME = "playerFrame";
  private static final String VIEWER_FRAME = "viewerFrame";
  private static final String AI = "AI";
  private final VerticalPanel emptyVerticalPanel = new VerticalPanel();
  private boolean singleFrame = false;
  private int totalPlayerFrames;
  private boolean isViewerPresent = true;
  private boolean isComputerPlayerPresent = false;
  private int randomDelayMillis;
  private int timePerTurn;
  private ScrollPanel scrollPanel;
  private final static String TIME_LEFT = "Time Left : ";
  public EnhancedConsole getConsole() {
    return enhancedConsole;
  }
  
  public void showWaitCursor() {
    mainPanel.getElement().getStyle().setProperty("cursor", "wait");
  }
 
  public void showDefaultCursor() {
    mainPanel.getElement().getStyle().setProperty("cursor", "default");
  }
  
  private Timer turnTimer = new Timer() {
    @Override
    public void run() {
      try {
        String timerLabel = turnTimerLabel.getText();
        int index = timerLabel.indexOf(TIME_LEFT);
        if (index != -1 ) {
          String timeText = turnTimerLabel.getText().substring(index + TIME_LEFT.length());
          if (timeText != null && timeText.length() != 0) {
            int currentTime = Integer.parseInt(timeText);
            turnTimerLabel.setText(timerLabel.replaceFirst(TIME_LEFT + ".*$", TIME_LEFT 
                + String.valueOf(currentTime - 1)));
            if (currentTime - 1 <= 0) {
              alertBar.setType(AlertType.DANGER);
              turnTimerLabel.setText("Game Ended. Please Restart !");
              removeEventListener();
              this.cancel();
              popupReloadEmulator.showConfigPanel();
            }
          }
        }
      } catch(Exception ex) {
        this.cancel();
      }
    }
  };
  
  public GwtEmulatorGraphics() {
    GwtEmulatorGraphicsUiBinder uiBinder = GWT.create(GwtEmulatorGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    turnTimerLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
    txtGameUrl.getElement().setAttribute("size", "40");
    enhancedConsole = new EnhancedConsole();
    popupReloadEmulator.hide();
    btnReloadEmulator.setVisible(false);
    btnSaveState.setVisible(false);
    btnEditState.setVisible(false);
    alertBar.setVisible(false);
    addSaveStateTable();
  }
  
  private void setupEmulatorGraphics() {
    mainPanel.remove(mainConfigPanel);
    mainEmulatorPanel.setVisible(true);
    scrollPanel = new ScrollPanel();
    scrollPanel.add(enhancedConsole);
    scrollPanel.setSize("300px", gameFrameHeight - 75 + "px");
    scrollPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
    scrollPanel.getElement().getStyle().setBorderWidth(1, Unit.MM);
    scrollPanel.getElement().getStyle().setBorderColor("lightgrey");
    consolePanel.add(scrollPanel);
    btnStart.setVisible(false);
    btnsPanel.setVisible(true);
    btnReloadEmulator.setVisible(true);
    btnSaveState.setVisible(true);
    btnEditState.setVisible(true);
    alertBar.setVisible(true);
  }
  
  @UiHandler("btnStart")
  void onClickStartButton(ClickEvent e) {
    if (!initEmulator())
      return;
    setupEmulatorGraphics();
  }
  
  @UiHandler("btnReloadEmulator")
  void onClickReloadEmulatorButton(ClickEvent e) {
    resetConfigPanelFields();
    popupReloadEmulator.showConfigPanel();
  }
  
  @UiHandler("btnCancel")
  void onClickCancelButton(ClickEvent e) {
    popupReloadEmulator.hideConfigPanel();
  }
  
  @UiHandler("btnReset")
  void onClickResetButton(ClickEvent e) {
    resetConfigPanelFields();
  }
  
  @UiHandler("btnReload")
  void onClickReloadButton(ClickEvent e) {
    try {
      popupReloadEmulator.hideConfigPanel();
      initEmulator();
    }
    catch(Exception ex) {
    }
  }
  
  private void clearEmulator() {
    enhancedConsole.reset();
    if (gameTabs != null) {
      gameTabsPanel.remove(gameTabs);
      gameTabs.clear();
      playerFrames.clear();
      sliderBarPanel.clear();
      removeEventListener();
      resetTimer();
      alertBar.setType(AlertType.WARNING);
      numOfPlayers.setValidationState(ValidationState.NONE);
      width.setValidationState(ValidationState.NONE);
      height.setValidationState(ValidationState.NONE);
      timeLimit.setValidationState(ValidationState.NONE);
      networkDelay.setValidationState(ValidationState.NONE);
      url.setValidationState(ValidationState.NONE);
    }
  }
  
  private boolean initEmulator(JSONObject gameStateJSON) {
    if (!validatateAndInitConfigInput()) {
      return false;
    }
    clearEmulator();
    //initialize ServerEmulator
    serverEmulator = new ServerEmulator(numberOfPlayers, this, timePerTurn, randomDelayMillis, 
        singleFrame, isViewerPresent, isComputerPlayerPresent, gameStateJSON);
    initGameTabs();
    return true;
  }
  
  private boolean initEmulator() {
    if (!validatateAndInitConfigInput()) {
      return false;
    }
    clearEmulator();
    //initialize ServerEmulator
    serverEmulator = new ServerEmulator(numberOfPlayers, this, timePerTurn, randomDelayMillis, 
        singleFrame, isViewerPresent, isComputerPlayerPresent);
    initGameTabs();
    return true;
  }
  
  private void initGameTabs() {
    gameTabs = new TabLayoutPanel(2, Unit.EM);
    gameTabs.setSize(gameFrameWidth + "px", (gameFrameHeight + 25) + "px");
    if (singleFrame) {
      totalPlayerFrames = 1;
    } else {
      totalPlayerFrames = numberOfPlayers;
    }
    for (int i = 0; i < totalPlayerFrames; i++) {
      Frame frame = new Frame(gameUrl);
      frame.getElement().setId(PLAYER_FRAME + i);
      frame.setSize("100%", "100%");
      if (i == totalPlayerFrames - 1 && !singleFrame && isComputerPlayerPresent) {
        gameTabs.add(frame, "AI Player");
      } else {
        gameTabs.add(frame, "Player " + serverEmulator.getPlayerIds().get(i));
      }
      playerFrames.add(frame);
    }
    
    //Adding a frame for VIEWER
    if (isViewerPresent) {
      Frame frame = new Frame(gameUrl);
      frame.getElement().setId(VIEWER_FRAME);
      frame.setSize("100%", "100%");
      gameTabs.add(frame, "Viewer");
      viewerFrame = frame;
    }
    gameTabsPanel.add(gameTabs);
    injectEventListener(serverEmulator, totalPlayerFrames);
    addSlider(0);
  }
  
  private boolean validatateAndInitConfigInput() {
    boolean validation = true;
    try {
      numberOfPlayers = getNumOfPlayers();
      numOfPlayers.setValidationState(ValidationState.SUCCESS);
    } catch (Exception ex) {
      numOfPlayers.setValidationState(ValidationState.ERROR);
      validation = false;
    }
    try {
      gameFrameWidth = Integer.parseInt(txtGameWidth.getText());
      width.setValidationState(ValidationState.SUCCESS);
    }
    catch(NumberFormatException ex) {
      width.setValidationState(ValidationState.ERROR);
      validation = false;
    }
    try {
      gameFrameHeight = Integer.parseInt(txtGameHeight.getText());
      height.setValidationState(ValidationState.SUCCESS);
    }
    catch(NumberFormatException ex) {
      height.setValidationState(ValidationState.ERROR);
      validation = false;
    }
    
    try {
      int time = Integer.parseInt(txtDefaultTimePerTurn.getText());
      if (time <= 0) {
        time = 0;
      }
      timePerTurn = time;
      timeLimit.setValidationState(ValidationState.SUCCESS);
    } catch (NumberFormatException ex) {
      timeLimit.setValidationState(ValidationState.ERROR);
      validation = false;
    }
    try {
      int time = Integer.parseInt(txtRandomDelayMillis.getText());
      if (time < 0) {
        time = 0;
      }
      randomDelayMillis = time;
      networkDelay.setValidationState(ValidationState.SUCCESS);
    } catch (NumberFormatException ex) {
      networkDelay.setValidationState(ValidationState.ERROR);
      validation = false;
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
      url.setValidationState(ValidationState.SUCCESS);
    } catch (Exception ex) {
      url.setValidationState(ValidationState.ERROR);
      validation = false;
    }
    isViewerPresent = viewerCheck.getValue();
    singleFrame = singlePlayerCheck.getValue();
    isComputerPlayerPresent = computerPlayerCheck.getValue();
    return validation;
  }

  private int getNumOfPlayers() {
    return n2.isActive() ? 2 : (n3.isActive() ? 3 : (n4.isActive() ? 4 : (n5.isActive() ? 5 : 
      (n6.isActive() ? 6 : (n7.isActive() ? 7 : (n8.isActive() ? 8 : 9))))));
  }
  
  private void setNumOfPlayers(int numberOfPlayers) {
   switch(numberOfPlayers){
     case 2: n2.setActive(true);
             break;
     case 3: n3.setActive(true);
             break;
     case 4: n4.setActive(true);
             break;
     case 5: n5.setActive(true);
             break;
     case 6: n6.setActive(true);
             break;
     case 7: n7.setActive(true);
             break;
     case 8: n8.setActive(true);
             break;
     case 9: n9.setActive(true);
             break;
     default: break;
   }
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
        JSONObject emulatorState = JSONParser.parseStrict(content).isObject();
        JSONObject config = emulatorState.get("emulatorConfig").isObject();
        JSONObject gameStateJSON = emulatorState.get("gameState").isObject();
        if (scrollPanel == null) {
          setupEmulatorGraphics();
        }
        txtGameWidth.setText(config.get("txtGameWidth").isString().stringValue());
        txtGameHeight.setText(config.get("txtGameHeight").isString().stringValue());
        txtDefaultTimePerTurn.setText(config.get("txtDefaultTimePerTurn").isString().stringValue());
        txtRandomDelayMillis.setText(config.get("txtRandomDelayMillis").isString().stringValue());
        txtGameWidth.setText(config.get("txtGameWidth").isString().stringValue());
        setNumOfPlayers((int)config.get("listNumPlayers").isNumber().doubleValue());
        txtGameUrl.setText(config.get("txtGameUrl").isString().stringValue());
        viewerCheck.setValue(config.get("viewerCheck").isBoolean().booleanValue());
        singlePlayerCheck.setValue(config.get("singlePlayerCheck").isBoolean().booleanValue());
        computerPlayerCheck.setValue(config.get("computerPlayerCheck").isBoolean().booleanValue());
        initEmulator(gameStateJSON);
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
    final JSONObject gameStateJSON = serverEmulator.getGameStateAsJSON();
    final JSONObject emulatorConfigJSON = getEmulatorConfigAsJSON();
    final JSONObject saveState = new JSONObject();
    saveState.put("gameState", gameStateJSON);
    saveState.put("emulatorConfig", emulatorConfigJSON);
    final String data = saveState.toString();
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
        Alert message = new Alert(
            "State saved successfully. Press Load button to load any saved states", 
            AlertType.SUCCESS);
        message.setDismissable(true);
        RootPanel.get("mainDiv").insert(message, 1);
      }
    }, keySet).center();
  }
   
  @UiHandler("btnLoadState")
  void onClickLoadStateButton(ClickEvent e) {
    displayLoadPopUp.center();
  }
  
  private native void injectEventListener(ServerEmulator emulator, int totalPlayerFrames) /*-{
    $wnd.postMessageListener = function(e) {
      for(var i = 0; i < totalPlayerFrames; i++) {
        var frameName = "playerFrame"+i;
        var frame = $doc.getElementById(frameName);
        if(e.source == frame.contentWindow || e.source.parent == frame.contentWindow) {
          emulator.@org.smg.gwt.emulator.backend.ServerEmulator::eventListner(Ljava/lang/String;I)(JSON.stringify(e.data), i);
        }
      }
    }
    $wnd.addEventListener("message", $wnd.postMessageListener, false);
  }-*/;
  
  private native void removeEventListener() /*-{
    $wnd.removeEventListener("message", $wnd.postMessageListener, false);
  }-*/;
  
  private void sendMessage(String frameId, Message message) {
    String jsonStr = GameApiJsonHelper.getJsonString(message);
    postMessageToFrame(frameId, jsonStr);
  }
  
  public void sendMessageForPlayer(int playerIndex, Message message, String playerId) {
    enhancedConsole.addGameApiMessage(message, playerId, ConsoleMessageType.OUTGOING);
    sendMessage(PLAYER_FRAME+playerIndex, message);
  }
  
  public void sendMessageForViewer(Message message) {
    sendMessage(VIEWER_FRAME, message);
  }
  
  private static native void postMessageToFrame(String frameId, String message) /*-{
    $doc.getElementById(frameId).contentWindow.postMessage(JSON.parse(message), "*");
  }-*/;
  

  private void resetConfigPanelFields() {
    txtGameWidth.setText(String.valueOf(gameFrameWidth));
    txtGameHeight.setText(String.valueOf(gameFrameHeight));
    txtDefaultTimePerTurn.setText(String.valueOf(timePerTurn));
    txtRandomDelayMillis.setText(String.valueOf(randomDelayMillis));
    setNumOfPlayers(numberOfPlayers);
    txtGameUrl.setText(gameUrl);
    viewerCheck.setValue(isViewerPresent);
    singlePlayerCheck.setValue(singleFrame);
    computerPlayerCheck.setValue(isComputerPlayerPresent);
  }
  
  public void handleGameOver(EndGame endGameOpn) {
    new PopupGameOver(endGameOpn).center();
  }
  
  private class PopupReloadEmulator extends DialogBox {
    PopupReloadEmulator() {
      setText("Reload Emulator");
      setWidget(emptyVerticalPanel);
    }
    
    public void showConfigPanel() {
      popupReloadEmulator.setWidget(mainConfigPanel);
      popupReloadEmulator.setWidth("500px");
      popupReloadEmulator.setVisible(true);
      popupReloadEmulator.center();
    }
    
    public void hideConfigPanel() {
      popupReloadEmulator.hide();
      popupReloadEmulator.setWidget(emptyVerticalPanel);
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
          resetConfigPanelFields();
          popupReloadEmulator.showConfigPanel();
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
  
  //send time = "" for infinite timer
  public void setTurnAndTimer(String playerTurnId, String time) {
    if (time == null || time.isEmpty()) {
      time = "";
      turnTimerLabel.setText("Turn : " + "Player " + playerTurnId);
    } else {
      turnTimerLabel.setText("Turn : " + "Player " + playerTurnId + " " + TIME_LEFT + time);
    }
    turnTimer.cancel();
    //Select the turn player tab(iframe)
    if (!singleFrame) {
      try {
        if (playerTurnId.equals(GameApi.AI_PLAYER_ID)) {
          gameTabs.selectTab(playerFrames.get(playerFrames.size() - 1));
        } else {
          gameTabs.selectTab(playerFrames.get(Integer.parseInt(playerTurnId) - 
              Integer.parseInt(ServerEmulator.FIRST_PLAYER_ID)));
        }
      } catch(NumberFormatException ex) {
      }
    } else {
      if (playerTurnId.equals(GameApi.AI_PLAYER_ID)) {
        gameTabs.getTabWidget(playerFrames.get(0)).getElement().setInnerHTML("AI Player");
      } else {
        gameTabs.getTabWidget(playerFrames.get(0)).getElement().setInnerHTML(
            "Player " + playerTurnId);
      }
      
    }
    if (time != null && time.length() != 0) {
      turnTimer.scheduleRepeating(1000);
    }
  }
  
  public void resetTimer() {
    turnTimerLabel.setText("");
    turnTimer.cancel();
  }
  
  public JSONObject getEmulatorConfigAsJSON() {
    JSONObject json = new JSONObject();
    json.put("txtGameWidth", new JSONString(txtGameWidth.getText()));
    json.put("txtGameHeight", new JSONString(txtGameHeight.getText()));
    json.put("txtDefaultTimePerTurn", new JSONString(txtDefaultTimePerTurn.getText()));
    json.put("txtRandomDelayMillis", new JSONString(txtRandomDelayMillis.getText()));
    json.put("listNumPlayers", new JSONNumber(numberOfPlayers));
    json.put("txtGameUrl", new JSONString(txtGameUrl.getText()));
    json.put("viewerCheck", JSONBoolean.getInstance(viewerCheck.getValue()));
    json.put("singlePlayerCheck", JSONBoolean.getInstance(singlePlayerCheck.getValue()));
    json.put("computerPlayerCheck", JSONBoolean.getInstance(computerPlayerCheck.getValue()));
    return json;
  }
}