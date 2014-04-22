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
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.extras.slider.client.ui.Slider;
import org.smg.gwt.emulator.backend.ServerEmulator;
import org.smg.gwt.emulator.client.EnhancedConsole.ConsoleMessageType;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.Dialogs;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.Carousel;
import com.googlecode.mgwt.ui.client.widget.FormListEntry;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.MCheckBox;
import com.googlecode.mgwt.ui.client.widget.MListBox;
import com.googlecode.mgwt.ui.client.widget.MRadioButton;
import com.googlecode.mgwt.ui.client.widget.MTextBox;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.WidgetList;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ActionButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBarText;
import com.googlecode.mgwt.ui.client.widget.buttonbar.CameraButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ComposeButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.RefreshButton;

public class GwtEmulatorGraphics extends Composite {
  public interface GwtEmulatorGraphicsUiBinder extends UiBinder<Widget, GwtEmulatorGraphics> {
  }
  
  /*@UiField
  LayoutPanel mainPanel;*/
  
  @UiField
  LayoutPanel mainEmulatorPanel;
  
  HorizontalPanel tokensInfoPanel;
  
  MTextBox txtGameUrl, txtRandomDelayMillis;
  
  MTextBox txtGameWidth, txtGameHeight, txtDefaultTimePerTurn;
  
  @UiField
  MCheckBox viewerCheck, singlePlayerCheck;
  
  @UiField
  MCheckBox computerPlayerCheck;
  
  /*@UiField
  HeaderPanel turnTimerPanel;*/
  
  @UiField
  com.googlecode.mgwt.ui.client.widget.Button btnStart;
  
  @UiField
  ActionButton btnLoadState;
  
  @UiField
  RefreshButton btnReloadEmulator;
  
  @UiField
  CameraButton btnSaveState;
  
  @UiField
  ComposeButton btnEditState;
  
  @UiField
  RoundPanel btnsPanel;
  
  @UiField
  ButtonBarText btnConsole;
  
  /*@UiField
  WidgetList totalPlayersButtonGroup;*/
  
  @UiField
  com.googlecode.mgwt.ui.client.widget.Button btnCancel, btnReset, btnReload;
  
  @UiField
  org.gwtbootstrap3.client.ui.Button previousState, nextState;
  
  @UiField
  FormListEntry numOfPlayersEntry;
  
  @UiField
  FormListEntry tokensInfoPanelEntry;
  
  @UiField
  FormListEntry widthEntry, heightEntry;
  
  @UiField
  FormListEntry timeLimitEntry;
  
 /* @UiField
  MRadioButton n2, n3, n4, n5, n6, n7, n8, n9;
  
  MRadioButton numPlayerRadioBtn[];*/
  
  MListBox listNumPlayers;
  
  @UiField
  FormListEntry networkDelayEntry, urlEntry;//, otherOptionsEntry;

  @UiField
  LayoutPanel mainConfigPanel;

  @UiField
  VerticalPanel consolePanel;
  
  @UiField
  LayoutPanel gameTabsPanel;
  
  @UiField
  HTML turnLabel;
  
  /*@UiField
  HTML timeLeftBold;*/
  
  @UiField
  HTML timerLabel;
  
  @UiField
  ButtonBar headerPanel;
  
  @UiField
  Slider sliderBar;
  
  @UiField
  HorizontalPanel otherCheckBoxPanel;
  
  private Carousel gameTabs;
  private ServerEmulator serverEmulator;
  private int gameFrameWidth, gameFrameHeight, numberOfPlayers;
  private List<Integer> playerTokens = new ArrayList<Integer>();
  private Storage stateStore;
  private FlexTable flexTable;
  private ClickHandler clearAllButtonHandler;
  private PopupLoadState displayLoadPopUp;
  private List<Frame> playerFrames = new ArrayList<Frame>();
  private Frame viewerFrame;
  private String gameUrl;
  private EnhancedConsole enhancedConsole;
  //private final PopupReloadEmulator popupReloadEmulator = new PopupReloadEmulator();
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
  private final static String TIME_LEFT_BOLD = "Time Left : ";
  private final static String TURN_BOLD = "<b>Turn : </b>";
  public EnhancedConsole getConsole() {
    return enhancedConsole;
  }
  
  public void showWaitCursor() {
    RootPanel.get().getElement().getStyle().setProperty("cursor", "wait");
  }
 
  public void showDefaultCursor() {
    RootPanel.get().getElement().getStyle().setProperty("cursor", "default");
  }
  
  private Timer turnTimer = new Timer() {
    @Override
    public void run() {
      try {
        String timeText = timerLabel.getText();
        if (timeText != null && timeText.length() != 0) {
          int timeTextIndex = timeText.indexOf(TIME_LEFT_BOLD);
          timeText = timeText.substring(timeTextIndex + TIME_LEFT_BOLD.length());
          int currentTime = Integer.parseInt(timeText);
          timerLabel.setHTML(TIME_LEFT_BOLD + String.valueOf(currentTime - 1));
          if (currentTime - 1 < 0) {
            turnLabel.setHTML("Game Ended. Please Restart !");
            timerLabel.setHTML("");
            /*timeLeftBold.setHTML("");*/
            removeEventListener();
            this.cancel();
            clearFormValidation();
            showConfigPanel();
          }
        }
      } catch(Exception ex) {
        this.cancel();
        resetTimer();
      }
    }
  };
  
  private void createFormListEntries() {
    listNumPlayers = new MListBox();
    for (int i = MIN_PLAYERS; i <= MAX_PLAYERS; ++i) {
      listNumPlayers.addItem(i + " Players");
    }
    numOfPlayersEntry.setWidget("Number of Players", listNumPlayers);
    tokensInfoPanel = new HorizontalPanel();
    tokensInfoPanelEntry.setWidget("Player Tokens", tokensInfoPanel);
    leftHorizontalPanel(tokensInfoPanel);
    
    txtGameWidth = new MTextBox();
    txtGameWidth.setText("800");
    
    txtGameHeight = new MTextBox();
    txtGameHeight.setText("450");
    widthEntry.setWidget("Width", txtGameWidth);
    heightEntry.setWidget("Height", txtGameHeight);
    
    txtDefaultTimePerTurn = new MTextBox();
    txtDefaultTimePerTurn.setText("0");
    timeLimitEntry.setWidget("Time-limit Per Turn", txtDefaultTimePerTurn);
    
    txtRandomDelayMillis = new MTextBox();
    txtRandomDelayMillis.setText("0");
    networkDelayEntry.setWidget("Network Delay", txtRandomDelayMillis);
    
    txtGameUrl = new MTextBox();
    txtGameUrl.setText("http://2-dot-cheat-game.appspot.com/CheatGame.html");
    urlEntry.setWidget("URL", txtGameUrl);
    
    /*WidgetList otherOptions = new WidgetList();
    viewerCheck = new MRadioButton("viewer");
    viewerCheck.setText("Add Viewer");
    singlePlayerCheck = new MRadioButton("single");
    singlePlayerCheck.setText("Single Player Mode");
    computerPlayerCheck = new MRadioButton("AI");
    computerPlayerCheck.setText("AI Player Present");
    otherOptions.add(viewerCheck);
    otherOptions.add(singlePlayerCheck);
    otherOptions.add(computerPlayerCheck);
    otherOptionsEntry.setWidget("Other Options", otherOptions);*/
  }
  
  public void leftHorizontalPanel(HorizontalPanel element) {
    DOM.setStyleAttribute(element.getElement(), "marginLeft", "inherit");
    DOM.setStyleAttribute(element.getElement(), "marginRight", "auto");
    DOM.setStyleAttribute(element.getElement(), "marginTop", "auto");
    DOM.setStyleAttribute(element.getElement(), "marginBottom", "auto");
  }
  public GwtEmulatorGraphics() {
    GwtEmulatorGraphicsUiBinder uiBinder = GWT.create(GwtEmulatorGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    /*numPlayerRadioBtn = new MRadioButton[] {n2, n3, n4, n5, n6, n7, n8, n9};*/
    createFormListEntries();
    /*turnTimerPanel.setVisible(false);*/
    turnLabel.setVisible(false);
    timerLabel.setVisible(false);
    txtGameUrl.getElement().setAttribute("size", "40");
    enhancedConsole = new EnhancedConsole();
    /*popupReloadEmulator.hide();*/
    btnReloadEmulator.setVisible(false);
    btnSaveState.setVisible(false);
    btnEditState.setVisible(false);
    btnConsole.setVisible(false);
    addSaveStateTable();
    changePlayerInfoPanel(2);
    listNumPlayers.addChangeHandler(new ChangeHandler() {
      
      @Override
      public void onChange(ChangeEvent event) {
        // TODO Auto-generated method stub
        int playerIndex = listNumPlayers.getSelectedIndex();
        changePlayerInfoPanel(MIN_PLAYERS + playerIndex);
      }
    });
    /*for (int i = 0; i < 8; i++) {
      listNumPlayers.get.addTapHandler(new TapHandler() {
        
        @Override
        public void onTap(TapEvent event) {
          // TODO Auto-generated method stub
          MRadioButton btn = (MRadioButton)event.getSource();
          int num = Integer.parseInt(btn.getText());
          changePlayerInfoPanel(num);
        }
      });
    }*/
  }
  
  private void changePlayerInfoPanel(int num) {
    int existingPlayers = tokensInfoPanel.getWidgetCount();
    int i;
    for(i = 0; i < num; i++) {
      if (i < existingPlayers) {
        continue;
      }
      MTextBox textBox = new MTextBox();
      textBox.setWidth("70px");
      textBox.setValue("10000");
      tokensInfoPanel.add(textBox);
    }
    for (int j = i; j < existingPlayers; j++) {
      tokensInfoPanel.remove(i);
    }
  }
  
  private void setupEmulatorGraphics() {
    //mainPanel.remove(mainConfigPanel);
    mainConfigPanel.setVisible(false);
    //mainEmulatorPanel.setVisible(true);
    scrollPanel = new ScrollPanel();
    scrollPanel.add(enhancedConsole);
    scrollPanel.setHeight(gameFrameHeight - 100 + "px");
    scrollPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
    scrollPanel.getElement().getStyle().setBorderWidth(1, Unit.MM);
    scrollPanel.getElement().getStyle().setBorderColor("lightgrey");
    consolePanel.add(scrollPanel);
    btnStart.setVisible(false);
    btnsPanel.setVisible(true);
    btnReloadEmulator.setVisible(true);
    btnSaveState.setVisible(true);
    btnEditState.setVisible(true);
    btnConsole.setVisible(true);
  }
  
  @UiHandler("btnStart")
  void onClickStartButton(TapEvent e) {
    if (!initEmulator())
      return;
    setupEmulatorGraphics();
  }
  
  @UiHandler("btnReloadEmulator")
  void onClickReloadEmulatorButton(TapEvent e) {
    resetConfigPanelFields();
    showConfigPanel();
  }
  
  @UiHandler("btnConsole")
  void onClickConsoleButton(TapEvent e) {
    
  }
  
  @UiHandler("btnCancel")
  void onClickCancelButton(TapEvent e) {
    hideConfigPanel();
  }
  
  @UiHandler("btnReset")
  void onClickResetButton(TapEvent e) {
    resetConfigPanelFields();
  }
  
  @UiHandler("btnReload")
  void onClickReloadButton(TapEvent e) {
    try {
      if (!initEmulator())
        return;
      hideConfigPanel();
      scrollPanel.setHeight(gameFrameHeight - 100 + "px");
    }
    catch(Exception ex) {
    }
  }
  
  @UiHandler("previousState")
  void onClickPreviousState(ClickEvent e) {
    sliderBar.setValue((double) ((serverEmulator.currentSliderIndex - 1) * sliderBar.getStep()), true);
  }
  
  @UiHandler("nextState")
  void onClickNextState(ClickEvent e) {
    sliderBar.setValue((double) ((serverEmulator.currentSliderIndex + 1) * sliderBar.getStep()), true);
  }
  
  private void clearEmulator() {
    enhancedConsole.reset();
    if (gameTabs != null) {
      gameTabsPanel.remove(gameTabs);
      gameTabs.clear();
      playerFrames.clear();
      sliderBar.setMax(0);
      removeEventListener();
      resetTimer();
      clearFormValidation();
    }
  }
  
  private void clearFormValidation() {
  }
  
  private boolean initEmulator(JSONObject gameStateJSON) {
    if (!validatateAndInitConfigInput()) {
      return false;
    }
    clearEmulator();
    //initialize ServerEmulator
    serverEmulator = new ServerEmulator(numberOfPlayers, this, playerTokens, timePerTurn,
        randomDelayMillis, singleFrame, isViewerPresent, isComputerPlayerPresent, gameStateJSON);
    initGameTabs();
    return true;
  }
  
  private boolean initEmulator() {
    if (!validatateAndInitConfigInput()) {
      return false;
    }
    clearEmulator();
    //initialize ServerEmulator
    serverEmulator = new ServerEmulator(numberOfPlayers, this, playerTokens, timePerTurn,
        randomDelayMillis, singleFrame, isViewerPresent, isComputerPlayerPresent);
    initGameTabs();
    return true;
  }
  
  private void initGameTabs() {
    mainEmulatorPanel.removeFromParent();
    gameTabs = new Carousel();
    /*turnTimerPanel.setVisible(true);*/
    turnLabel.setVisible(true);
    timerLabel.setVisible(true);
    gameTabsPanel.add(gameTabs);
    //gameTabs.setSize(gameFrameWidth + "px", (gameFrameHeight + 25) + "px");
    if (singleFrame) {
      totalPlayerFrames = 1;
    } else {
      totalPlayerFrames = numberOfPlayers;
    }
    for (int i = 0; i < totalPlayerFrames; i++) {
      ScrollPanel frameScroll = new ScrollPanel();
      RoundPanel roundPanel = new RoundPanel();
      roundPanel.getElement().setAttribute("style", roundPanel.getElement().getAttribute("style") + "-webkit-box-flex:1;");
      if (i == totalPlayerFrames - 1 && !singleFrame && isComputerPlayerPresent) {
        roundPanel.add(new HTML("AI Player"));
      } else {
        roundPanel.add(new HTML("Player " + serverEmulator.getPlayerIds().get(i)));
      }
      roundPanel.setHeight("100%");
      Frame frame = new Frame(gameUrl);
      frame.getElement().setId(PLAYER_FRAME + i);
      frame.setSize("100%", "85%");
      roundPanel.add(frame);
      frameScroll.setWidget(roundPanel);
      gameTabs.add(frameScroll);
      playerFrames.add(frame);
    }
    
    //Adding a frame for VIEWER
    if (isViewerPresent) {
      Frame frame = new Frame(gameUrl);
      frame.getElement().setId(VIEWER_FRAME);
      ScrollPanel frameScroll = new ScrollPanel();
      RoundPanel roundPanel = new RoundPanel();
      roundPanel.getElement().setAttribute("style", roundPanel.getElement().getAttribute("style") + "-webkit-box-flex:1;");
      roundPanel.add(new HTML("Viewer"));
      roundPanel.setHeight("100%");
      frame.setSize("100%", "85%");
      roundPanel.add(frame);
      frameScroll.setWidget(roundPanel);
      gameTabs.add(frameScroll);
      //gameTabs.add(frame, "Viewer");
      viewerFrame = frame;
    }
    gameTabs.refresh();
    injectEventListener(serverEmulator, totalPlayerFrames);
    addSlider();
  }
  
  private boolean validatateAndInitConfigInput() {
    boolean validation = true;
    try {
      numberOfPlayers = getNumOfPlayers();
    } catch (Exception ex) {
      Dialogs.alert("Error", "Invalid Number of Players", null);
      validation = false;
    }
    try {
      playerTokens.clear();
      for (int i = 0; i < numberOfPlayers; i++) {
        playerTokens.add(Integer.parseInt(((MTextBox)tokensInfoPanel.getWidget(i)).getText()));
      }
    } catch (Exception ex) {
      Dialogs.alert("Error", "Invalid Token", null);
      validation = false;
    }
    try {
      gameFrameWidth = Integer.parseInt(txtGameWidth.getText());
      if (gameFrameWidth < 0) {
        throw new NumberFormatException("Width should be positive");
      }
      gameFrameHeight = Integer.parseInt(txtGameHeight.getText());
      if (gameFrameHeight < 0) {
        throw new NumberFormatException("Height should be positive");
      }
    }
    catch(NumberFormatException ex) {
      Dialogs.alert("Error", "Invalid Size", null);
      validation = false;
    }
    
    try {
      int time = Integer.parseInt(txtDefaultTimePerTurn.getText());
      if (time <= 0) {
        time = 0;
      }
      timePerTurn = time;
    } catch (NumberFormatException ex) {
      Dialogs.alert("Error", "Invalid Time", null);
      validation = false;
    }
    try {
      int time = Integer.parseInt(txtRandomDelayMillis.getText());
      if (time < 0) {
        time = 0;
      }
      randomDelayMillis = time;
    } catch (NumberFormatException ex) {
      Dialogs.alert("Error", "Invalid network delay", null);
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
    } catch (Exception ex) {
      Dialogs.alert("Error", "Invalid URL", null);
      validation = false;
    }
    isViewerPresent = viewerCheck.getValue();
    singleFrame = singlePlayerCheck.getValue();
    isComputerPlayerPresent = computerPlayerCheck.getValue();
    return validation;
  }

  private int getNumOfPlayers() {
    return listNumPlayers.getSelectedIndex() + MIN_PLAYERS;
  }
  
  private void setNumOfPlayers(int numberOfPlayers) {
    listNumPlayers.setSelectedIndex(numberOfPlayers - MIN_PLAYERS);
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
  
  private void addSlider() {
    sliderBar.setMin(10);
    sliderBar.setMax(10);
    sliderBar.setStep(10);
    sliderBar.setValue((double)10);
    sliderBar.getElement().getParentElement().getParentElement().getStyle().
                          setVerticalAlign(VerticalAlign.MIDDLE);
    previousState.getElement().getStyle().setMarginRight(10, Unit.PX);
    nextState.getElement().getStyle().setMarginLeft(10, Unit.PX);

    sliderBar.addValueChangeHandler(new ValueChangeHandler<Double>() {
      
      @Override
      public void onValueChange(ValueChangeEvent<Double> event) {
        if(event.getValue() / sliderBar.getStep() > serverEmulator.numOfSavedState() - 1) {
          sliderBar.setValue((serverEmulator.numOfSavedState() - 1.0) * sliderBar.getStep(), true);
          return;
        }
        if(event.getValue() / sliderBar.getStep() < 0) {
          sliderBar.setValue(0.0, true);
          return;
        }
        if(event.getValue() / sliderBar.getStep() == serverEmulator.currentSliderIndex) {
          return;
        }
        serverEmulator.currentSliderIndex = (int) (
            event.getValue() / sliderBar.getStep());
        String jsonState = serverEmulator.getSavedStateAtIndex(serverEmulator.currentSliderIndex);
        if (jsonState != null) {
          serverEmulator.loadGameStateFromJSON(JSONParser.parseStrict(jsonState).isObject());
        }
      }
    });
  }
  
  public void incrementSliderMaxValue(int value) {
    sliderBar.setStep(10.0/serverEmulator.numOfSavedState());
    sliderBar.setValue(10d);
  }

  @UiHandler("btnEditState")
  void onClickEditStateButton(TapEvent e) {
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
      btnSaveState.setVisible(false);
      btnLoadState.setVisible(false);
      return;
    }
    flexTable = new FlexTable();
    addHeaderToTable();
    flexTable.setBorderWidth(2);
    for (int i = 0; i < stateStore.getLength(); i++){
      flexTable.setText(i + 1, 0, stateStore.key(i));
      Button load = new Button("Load");
      load.setSmall(true);
      Button clear = new Button("Clear");
      clear.setSmall(true);
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
    clearAll.setSmall(true);
    clearAll.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        stateStore.clear();
        flexTable.removeAllRows();
        addHeaderToTable();
        displayLoadPopUp.center();
      }
    });
    flexTable.setWidget(0, 2, clearAll);
  }
  
  private void addLoadClearButtonHandlers(Button load, Button clear, final int row) {
    load.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
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
        setNumOfPlayers((int)config.get("listNumPlayers").isNumber().doubleValue());
        changePlayerInfoPanel(getNumOfPlayers());
        txtGameUrl.setText(config.get("txtGameUrl").isString().stringValue());
        viewerCheck.setValue(config.get("viewerCheck").isBoolean().booleanValue());
        singlePlayerCheck.setValue(config.get("singlePlayerCheck").isBoolean().booleanValue());
        computerPlayerCheck.setValue(config.get("computerPlayerCheck").isBoolean().booleanValue());
        initEmulator(gameStateJSON);
        if (scrollPanel == null) {
          setupEmulatorGraphics();
        } else {
          scrollPanel.setHeight(gameFrameHeight - 100 + "px");
        }
        displayLoadPopUp.hide();
      }
    });
    
    clear.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        String key = flexTable.getText(row, 0);
        stateStore.removeItem(key);
        flexTable.removeRow(row);
        displayLoadPopUp.center();
      }
    });
  }

  @UiHandler("btnSaveState")
  void onClickSaveStateButton(TapEvent e) {
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
  void onClickLoadStateButton(TapEvent e) {
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
    changePlayerInfoPanel(numberOfPlayers);
    clearFormValidation();
  }
  
  public void handleGameOver(EndGame endGameOpn) {
    new PopupGameOver(endGameOpn).center();
  }
  
  /*private class PopupReloadEmulator extends DialogBox {
    PopupReloadEmulator() {
      setText("Reload Emulator");
      setWidget(emptyVerticalPanel);
    }*/
    
    public void showConfigPanel() {
      /*popupReloadEmulator.setWidget(mainConfigPanel);
      popupReloadEmulator.setWidth("500px");
      popupReloadEmulator.setVisible(true);
      popupReloadEmulator.center();*/
      mainConfigPanel.setVisible(true);
      /*turnTimerPanel.setVisible(false);*/
      turnLabel.setVisible(false);
      timerLabel.setVisible(false);
      gameTabsPanel.setVisible(false);
    }
    
    public void hideConfigPanel() {
      /*popupReloadEmulator.hide();
      popupReloadEmulator.setWidget(emptyVerticalPanel);*/
      mainConfigPanel.setVisible(false);
      /*turnTimerPanel.setVisible(true);*/
      turnLabel.setVisible(true);
      timerLabel.setVisible(true);
      gameTabsPanel.setVisible(true);
    }
  //}
  
  private class PopupGameOver extends PopinDialog {
    public PopupGameOver(EndGame endGame) {
      
      Map<String, Integer> scores = endGame.getPlayerIdToScore();
      List<String> playerIds = serverEmulator.getPlayerIds();
      
      /*setText("Game Over");*/
      
      Button btnRestartGame = new Button("Restart");
      btnRestartGame.setSmall(true);
      btnRestartGame.addTapHandler(new TapHandler() {
        @Override
        public void onTap(TapEvent event) {
          hide();
          resetConfigPanelFields();
          showConfigPanel();
        }
      });
      
      Button btnCancel = new Button("Cancel");
      btnCancel.setSmall(true);
      btnCancel.addTapHandler(new TapHandler() {
        @Override
        public void onTap(TapEvent event) {
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
      
      WidgetList mainPanel = new WidgetList();
      mainPanel.add(new Label("Final Scores:"));
      mainPanel.add(scoreTable);
      HorizontalPanel buttonsPanel = new HorizontalPanel();
      buttonsPanel.add(btnCancel);
      buttonsPanel.add(btnRestartGame);
      mainPanel.add(buttonsPanel);
      add(mainPanel);
    }
  }
  
  //send time = "" for infinite timer
  public void setTurnAndTimer(String playerTurnId, String time) {
    turnLabel.setHTML(TURN_BOLD + "Player " + playerTurnId);
    if (time == null || time.isEmpty()) {
      time = "";
      /*timeLeftBold.setHTML("");*/
      timerLabel.setHTML("");
    } else {
      /*timeLeftBold.setHTML(TIME_LEFT_BOLD);*/
      timerLabel.setHTML(TIME_LEFT_BOLD + time);
    }
    turnTimer.cancel();
    //Select the turn player tab(iframe)
    if (!singleFrame) {
      try {
        if (playerTurnId.equals(GameApi.AI_PLAYER_ID)) {
         /* Window.alert(playerFrames.size() - 1 +  "");*/
          gameTabs.setSelectedPage(playerFrames.size() - 1);
          //gameTabs.refresh();
          //gameTabs.selectTab(playerFrames.get(playerFrames.size() - 1));
        } else {
         /* Window.alert(Integer.parseInt(playerTurnId) - 
              Integer.parseInt(ServerEmulator.FIRST_PLAYER_ID) + "");*/
          gameTabs.setSelectedPage(Integer.parseInt(playerTurnId) - 
              Integer.parseInt(ServerEmulator.FIRST_PLAYER_ID));
          //gameTabs.refresh();
          /*gameTabs.selectTab(playerFrames.get(Integer.parseInt(playerTurnId) - 
              Integer.parseInt(ServerEmulator.FIRST_PLAYER_ID)));*/
        }
      } catch(NumberFormatException ex) {
      }
    } else {
      if (playerTurnId.equals(GameApi.AI_PLAYER_ID)) {
        ScrollPanel scrollPanel = (ScrollPanel)(gameTabs.iterator().next());
        RoundPanel roundPanel = (RoundPanel)(scrollPanel.iterator().next());
        HTML html = (HTML)(roundPanel.iterator().next());
        html.setHTML("AI Player");
        //gameTabs.getTabWidget(playerFrames.get(0)).getElement().setInnerHTML("AI Player");
      } else {
        ScrollPanel scrollPanel = (ScrollPanel)(gameTabs.iterator().next());
        RoundPanel roundPanel = (RoundPanel)(scrollPanel.iterator().next());
        HTML html = (HTML)(roundPanel.iterator().next());
        html.setHTML("Player " + playerTurnId);
        /*gameTabs.getTabWidget(playerFrames.get(0)).getElement().setInnerHTML(
            "Player " + playerTurnId);*/
      }
      
    }
    if (time != null && time.length() != 0) {
      turnTimer.scheduleRepeating(1000);
    }
  }
  
  public void resetTimer() {
    turnLabel.setHTML("");
    /*timeLeftBold.setHTML("");*/
    timerLabel.setHTML("");
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