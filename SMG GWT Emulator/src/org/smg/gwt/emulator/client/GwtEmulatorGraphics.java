package org.smg.gwt.emulator.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.game_api.GameApi;
import org.game_api.GameApi.EndGame;
import org.game_api.GameApi.GameApiJsonHelper;
import org.game_api.GameApi.Message;
import org.smg.gwt.emulator.backend.ServerEmulator;
import org.smg.gwt.emulator.client.EnhancedConsole.ConsoleMessageType;
import org.smg.gwt.emulator.i18n.ConsoleMessages;
import org.smg.gwt.emulator.i18n.EmulatorConstants;
import org.smg.gwt.emulator.i18n.StatusMessages;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.Carousel;
import com.googlecode.mgwt.ui.client.widget.FormListEntry;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.MCheckBox;
import com.googlecode.mgwt.ui.client.widget.MListBox;
import com.googlecode.mgwt.ui.client.widget.MTextArea;
import com.googlecode.mgwt.ui.client.widget.MTextBox;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ActionButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ArrowLeftButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ArrowRightButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBarText;
import com.googlecode.mgwt.ui.client.widget.buttonbar.CameraButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ComposeButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.NextSlideButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.PreviousSlideButton;
import com.googlecode.mgwt.ui.client.widget.buttonbar.RefreshButton;

@SuppressWarnings("deprecation")
public class GwtEmulatorGraphics extends Composite {
  public interface GwtEmulatorGraphicsUiBinder extends UiBinder<Widget, GwtEmulatorGraphics> {
  }
  
  @UiField
  LayoutPanel main;
  
  @UiField
  HeaderPanel headerPanel;
  
  @UiField
  VerticalPanel headerPanelLeft;
  
  @UiField
  ScrollPanel mainConfigPanel;
  
  @UiField
  ActionButton btnLoadState;
  
  @UiField
  RefreshButton btnReloadEmulator;
  
  @UiField
  CameraButton btnSaveState;
  
  @UiField
  ComposeButton btnEditState;
  
  @UiField
  PreviousSlideButton btnPrevState;
  
  @UiField
  NextSlideButton btnNextState;
  
  @UiField
  RoundPanel btnsPanel;
  
  @UiField
  ButtonBarText btnConsole;
  
  @UiField
  Button btnStart, btnCancel, btnReset, btnReload;
  
  @UiField
  FormListEntry numOfPlayersEntry, tokensInfoPanelEntry, timeLimitEntry, networkDelayEntry, urlEntry;
  
  @UiField
  FormListEntry viewerEntry, aiEntry, singlePlayerEntry;

  @UiField
  HTML turnLabel, configLabel, timerLabel;
  
  @UiField
  ButtonBar footerPanel;
  
  private ArrowLeftButton btnPreviousTab = new ArrowLeftButton();
  private HTML playerTabLabel = new HTML(); 
  private ArrowRightButton btnNextTab = new ArrowRightButton();
  private Carousel gameTabs = new Carousel();
  private ServerEmulator serverEmulator;
  private int numberOfPlayers;
  private List<Integer> playerTokens = new ArrayList<Integer>();
  private Storage stateStore;
  private FlexTable flexTable;
  private ClickHandler clearAllButtonHandler;
  private PopupLoadState displayLoadPopUp;
  private List<Frame> playerFrames = new ArrayList<Frame>();
  private Frame viewerFrame;
  private String gameUrl;
  private MListBox listNumPlayers;
  private EnhancedConsole enhancedConsole;
  private HorizontalPanel playerTabInfoPanel = new HorizontalPanel();
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
  private FlowPanel tokensInfoPanel;
  private MTextArea txtGameUrl;
  private MTextBox txtRandomDelayMillis, txtDefaultTimePerTurn;
  private MCheckBox viewerCheck, singlePlayerCheck, computerPlayerCheck;
  private String currentTurn = null;
  
  private EmulatorConstants emulatorConstants;
  private StatusMessages statusMessages;
  private ConsoleMessages consoleMessages;
  
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
        String timeText = timerLabel.getHTML();
        if (timeText != null && timeText.length() != 0) {
          int timeTextIndex = timeText.indexOf(statusMessages.timeLeftBold());
          String orderFinder[] = statusMessages.timeLeftOrder("1", "2").split(" ");
          if (orderFinder[0] == "1") {
            timeText = timeText.substring(timeTextIndex + statusMessages.timeLeftBold().length())
                .trim();
          } else {
            timeText = timeText.substring(0, timeTextIndex).trim();
          }
          int currentTime = Integer.parseInt(timeText);
          String timeHTML = statusMessages.timeLeftOrder(statusMessages.timeLeftBold(), 
              String.valueOf(currentTime - 1));
          SafeHtml timeSafeHTML = new SafeHtmlBuilder().appendHtmlConstant(timeHTML).toSafeHtml();
          timerLabel.setHTML(timeSafeHTML);
          if (currentTime - 1 < 0) {
            turnLabel.setHTML(new SafeHtmlBuilder().appendEscaped(emulatorConstants.gameEnded())
                .toSafeHtml());
            timerLabel.setHTML(new SafeHtmlBuilder().appendEscaped(
                emulatorConstants.pleaseRestart()).toSafeHtml());
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
      listNumPlayers.addItem(statusMessages.numPlayers(i));
    }
    numOfPlayersEntry.setWidget(emulatorConstants.numberOfPlayers(), listNumPlayers);
    tokensInfoPanel = new FlowPanel();
    tokensInfoPanelEntry.setWidget(emulatorConstants.playerTokens(), tokensInfoPanel);
    leftHorizontalPanel(tokensInfoPanel.getElement());
    
    txtDefaultTimePerTurn = new MTextBox();
    txtDefaultTimePerTurn.setText("0");
    timeLimitEntry.setWidget(emulatorConstants.timeLimit(), txtDefaultTimePerTurn);
    
    txtRandomDelayMillis = new MTextBox();
    txtRandomDelayMillis.setText("0");
    networkDelayEntry.setWidget(emulatorConstants.networkDelay(), txtRandomDelayMillis);
    
    txtGameUrl = new MTextArea();
    txtGameUrl.setText("http://2-dot-cheat-game.appspot.com/CheatGame.html");
    urlEntry.setWidget(emulatorConstants.url(), txtGameUrl);
    txtGameUrl.getElement().getStyle().setOverflow(Overflow.HIDDEN);
    
    viewerCheck = new MCheckBox();
    viewerCheck.setValue(false);
    viewerEntry.setWidget(emulatorConstants.addViewer(), viewerCheck);
    leftHorizontalPanel(viewerCheck.getElement());
    
    singlePlayerCheck = new MCheckBox();
    singlePlayerEntry.setWidget(emulatorConstants.singlePlayer(), singlePlayerCheck);
    leftHorizontalPanel(singlePlayerCheck.getElement());
    
    computerPlayerCheck = new MCheckBox();
    computerPlayerCheck.setValue(false);
    aiEntry.setWidget(emulatorConstants.aiPresent(), computerPlayerCheck);
    leftHorizontalPanel(computerPlayerCheck.getElement());
  }
  
  private void leftHorizontalPanel(Element element) {
    DOM.setStyleAttribute(element, "marginLeft", "inherit");
    DOM.setStyleAttribute(element, "marginRight", "auto");
    DOM.setStyleAttribute(element, "marginTop", "auto");
    DOM.setStyleAttribute(element, "marginBottom", "auto");
  }
  
  public GwtEmulatorGraphics() {
    emulatorConstants = (EmulatorConstants) GWT.create(EmulatorConstants.class);
    statusMessages = (StatusMessages) GWT.create(StatusMessages.class);
    consoleMessages = (ConsoleMessages) GWT.create(ConsoleMessages.class);
    GwtEmulatorGraphicsUiBinder uiBinder = GWT.create(GwtEmulatorGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    createFormListEntries();
    createPlayerTabInfoPanel();
    txtGameUrl.getElement().setAttribute("size", "40");
    enhancedConsole = new EnhancedConsole(consoleMessages, emulatorConstants);
    setButtonsVisibility(false);
    addSaveStateTable();
    changePlayerInfoPanel(2);
    listNumPlayers.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        int playerIndex = listNumPlayers.getSelectedIndex();
        changePlayerInfoPanel(MIN_PLAYERS + playerIndex);
      }
    });
    Window.addResizeHandler(new ResizeHandler() {
      @Override
      public void onResize(ResizeEvent event) {
        mainConfigPanel.refresh();
        resizeContainer();
      }
    });
  }

  private void createPlayerTabInfoPanel() {
    playerTabInfoPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    btnPreviousTab.setHeight("40px");
    btnNextTab.setHeight("40px");
    playerTabLabel.getElement().getStyle().setFontSize(12, Unit.PT);
    playerTabInfoPanel.add(btnPreviousTab);
    playerTabInfoPanel.add(playerTabLabel);
    playerTabInfoPanel.add(btnNextTab);
    playerTabInfoPanel.setCellHorizontalAlignment(btnPreviousTab, HasHorizontalAlignment.ALIGN_RIGHT);
    playerTabInfoPanel.setCellHorizontalAlignment(btnNextTab, HasHorizontalAlignment.ALIGN_LEFT);
    playerTabInfoPanel.setCellVerticalAlignment(playerTabLabel, HasVerticalAlignment.ALIGN_MIDDLE);
    playerTabInfoPanel.setCellWidth(playerTabLabel, "75px");
    playerTabInfoPanel.getElement().getStyle().setMarginTop(0, Unit.PX);
    btnPreviousTab.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        if (gameTabs.getSelectedPage() > 0) {
          gameTabs.setSelectedPage(gameTabs.getSelectedPage() - 1);
          gameTabs.refresh();
        }
      }
    });
    
    btnNextTab.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        int totalFrames = isViewerPresent ? totalPlayerFrames : totalPlayerFrames - 1;
        if (gameTabs.getSelectedPage() < totalFrames) {
          gameTabs.setSelectedPage(gameTabs.getSelectedPage() + 1);
          gameTabs.refresh();
        }
      }
    });
  }

  private void setButtonsVisibility(boolean visibility) {
    turnLabel.setVisible(visibility);
    timerLabel.setVisible(visibility);
    footerPanel.setVisible(visibility);
    if (visibility) {
      headerPanel.setCenterWidget(playerTabInfoPanel);
      headerPanel.setRightWidget(btnConsole);
      footerPanel.add(btnLoadState);
      resizeContainer();
    } else {
      headerPanel.setCenterWidget(configLabel);
      headerPanel.setRightWidget(btnLoadState);
    }
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
      Style style = textBox.getElement().getStyle();
      style.setDisplay(Display.INLINE_BLOCK);
      tokensInfoPanel.add(textBox);
    }
    for (int j = i; j < existingPlayers; j++) {
      tokensInfoPanel.remove(i);
    }
  }
  
  private void setupEmulatorGraphics() {
    btnStart.setVisible(false);
    btnsPanel.setVisible(true);
    setButtonsVisibility(true);
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
    new PopupConsole(enhancedConsole, emulatorConstants).center();
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
    }
    catch(Exception ex) {
    }
  }
  
  @UiHandler("btnPrevState")
  void onClickPrevStateButton(TapEvent e) {
    if (serverEmulator.currentSliderIndex > 0) {
      serverEmulator.currentSliderIndex--;
      String jsonState = serverEmulator.getSavedStateAtIndex(serverEmulator.currentSliderIndex);
      if (jsonState != null) {
        serverEmulator.loadGameStateFromJSON(JSONParser.parseStrict(jsonState).isObject());
      }
    }
  }
  
  @UiHandler("btnNextState")
  void onClickNextStateButton(TapEvent e) {
    if (serverEmulator.currentSliderIndex < serverEmulator.numOfSavedState() - 1) {
      serverEmulator.currentSliderIndex++;
      String jsonState = serverEmulator.getSavedStateAtIndex(serverEmulator.currentSliderIndex);
      if (jsonState != null) {
        serverEmulator.loadGameStateFromJSON(JSONParser.parseStrict(jsonState).isObject());
      }
    }
  }

  private void clearEmulator() {
    enhancedConsole.reset();
    if (gameTabs != null) {
      gameTabs.removeFromParent();
      gameTabs.clear();
      playerFrames.clear();
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
        randomDelayMillis, singleFrame, isViewerPresent, isComputerPlayerPresent, gameStateJSON,
        consoleMessages);
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
        randomDelayMillis, singleFrame, isViewerPresent, isComputerPlayerPresent, consoleMessages);
    initGameTabs();
    return true;
  }
  
  private void initGameTabs() {
    gameTabs = new Carousel();
    footerPanel.removeFromParent();
    mainConfigPanel.setVisible(false);
    main.add(gameTabs);
    main.add(footerPanel);
    
    if (singleFrame) {
      totalPlayerFrames = 1;
    } else {
      totalPlayerFrames = numberOfPlayers;
    }
    
    for (int i = 0; i < totalPlayerFrames; i++) {
      createGamePanel(PLAYER_FRAME + i);
    }
    
    //Adding a frame for VIEWER
    if (isViewerPresent) {
      createGamePanel(VIEWER_FRAME);
    }
    
    if (!singleFrame || isViewerPresent) {
      btnPreviousTab.setVisible(true);
      btnNextTab.setVisible(true);
    } else {
      btnPreviousTab.setVisible(false);
      btnNextTab.setVisible(false);
    }
    
    gameTabs.refresh();
    injectEventListener(serverEmulator, totalPlayerFrames);
    gameTabs.addSelectionHandler(new SelectionHandler<Integer>() {
      @Override
      public void onSelection(SelectionEvent<Integer> event) {
        String label = null;
        int i = event.getSelectedItem().intValue();
        if (isViewerPresent && i == totalPlayerFrames) {
          label = emulatorConstants.viewer();
        } else if (singleFrame && currentTurn != null) {
          if (currentTurn.equals(GameApi.AI_PLAYER_ID)) {
            label = emulatorConstants.aiPlayer();
          } else {
            label = statusMessages.player(currentTurn);
          }
        } else if(isComputerPlayerPresent && i == totalPlayerFrames - 1) {
          label = emulatorConstants.aiPlayer();
        } else {
          label = statusMessages.player(serverEmulator.getPlayerIds().get(i));
        }  
        playerTabLabel.setText(label);
      }
    });
  }
  
  private void createGamePanel(String frameId) {
    RoundPanel gameContainer = new RoundPanel();
    Frame frame = new Frame(gameUrl);
    frame.getElement().setId(frameId);
    frame.setWidth("100%");
    gameContainer.add(frame);
    gameTabs.add(gameContainer);
    playerFrames.add(frame);

    gameContainer.getElement().setAttribute("style", 
        gameContainer.getElement().getAttribute("style") + "-webkit-box-flex:1;");
    gameContainer.getElement().getStyle().setMargin(5, Unit.PX);
    gameContainer.getElement().getStyle().setPadding(5, Unit.PX);
  }
  
  private boolean validatateAndInitConfigInput() {
    boolean validation = true;
    try {
      numberOfPlayers = getNumOfPlayers();
    } catch (Exception ex) {
      CustomDialogPanel.alert(emulatorConstants.error(), emulatorConstants.invalidPlayers(), null,
          emulatorConstants.ok());
      validation = false;
    }
    try {
      playerTokens.clear();
      for (int i = 0; i < numberOfPlayers; i++) {
        playerTokens.add(Integer.parseInt(((MTextBox)tokensInfoPanel.getWidget(i)).getText()));
      }
    } catch (Exception ex) {
      CustomDialogPanel.alert(emulatorConstants.error(), emulatorConstants.invalidToken(), null,
          emulatorConstants.ok());
      validation = false;
    }
    
    try {
      int time = Integer.parseInt(txtDefaultTimePerTurn.getText());
      if (time <= 0) {
        time = 0;
      }
      timePerTurn = time;
    } catch (NumberFormatException ex) {
      CustomDialogPanel.alert(emulatorConstants.error(), emulatorConstants.invalidTime(), null,
          emulatorConstants.ok());
      validation = false;
    }
    try {
      int time = Integer.parseInt(txtRandomDelayMillis.getText());
      if (time < 0) {
        time = 0;
      }
      randomDelayMillis = time;
    } catch (NumberFormatException ex) {
      CustomDialogPanel.alert(emulatorConstants.error(), emulatorConstants.invalidDelay(), null,
          emulatorConstants.ok());
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
      CustomDialogPanel.alert(emulatorConstants.error(), emulatorConstants.invalidURL(), null,
          emulatorConstants.ok());
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
    }, emulatorConstants).center();
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
      Button load = new Button(emulatorConstants.load());
      load.setSmall(true);
      Button clear = new Button(emulatorConstants.clear());
      clear.setSmall(true);
      addLoadClearButtonHandlers(load, clear, i + 1);
      flexTable.setWidget(i + 1, 1, load);
      flexTable.setWidget(i + 1, 2, clear);
    }
    displayLoadPopUp = new PopupLoadState(flexTable, emulatorConstants);
    displayLoadPopUp.hide();
  }
  
  private void addHeaderToTable() {
    flexTable.setText(0, 0, emulatorConstants.savedStateName());
    flexTable.setText(0, 1, emulatorConstants.loadOption());
    Button clearAll = new Button(emulatorConstants.clearAll());
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
        txtDefaultTimePerTurn.setText(config.get("txtDefaultTimePerTurn").isString().stringValue());
        txtRandomDelayMillis.setText(config.get("txtRandomDelayMillis").isString().stringValue());
        setNumOfPlayers((int)config.get("listNumPlayers").isNumber().doubleValue());
        changePlayerInfoPanel(getNumOfPlayers());
        txtGameUrl.setText(config.get("txtGameUrl").isString().stringValue());
        viewerCheck.setValue(config.get("viewerCheck").isBoolean().booleanValue());
        singlePlayerCheck.setValue(config.get("singlePlayerCheck").isBoolean().booleanValue());
        computerPlayerCheck.setValue(config.get("computerPlayerCheck").isBoolean().booleanValue());
        initEmulator(gameStateJSON);
        setupEmulatorGraphics();
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
        Button load = new Button(emulatorConstants.load());
        Button clear = new Button(emulatorConstants.clear());
        load.setSmall(true);
        clear.setSmall(true);
        addLoadClearButtonHandlers(load, clear, row + 1);
        flexTable.setWidget(row + 1, 1, load);
        flexTable.setWidget(row + 1, 2, clear);
        CustomDialogPanel.alert(emulatorConstants.success(), emulatorConstants.stateSaveSuccess(), 
            null, emulatorConstants.ok());
      }
    }, keySet, emulatorConstants).center();
  }
   
  @UiHandler("btnLoadState")
  void onClickLoadStateButton(TapEvent e) {
    displayLoadPopUp.center();
  }
  
  
  private void resizeContainer() {
    Iterator<Widget> iterator = gameTabs.iterator();
    while(iterator.hasNext()) {
      RoundPanel r = (RoundPanel) iterator.next();
      Frame f = (Frame) r.iterator().next();
      r.setHeight(gameTabs.getElement().getClientHeight() - 10 + "px");
      f.setHeight(r.getElement().getClientHeight() - 20 + "px");
    }
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
  
  private void showConfigPanel() {
    gameTabs.setVisible(false);
    footerPanel.removeFromParent();
    mainConfigPanel.setVisible(true);
    main.add(footerPanel);
    setButtonsVisibility(false);
    mainConfigPanel.refresh();
  }
  
  private void hideConfigPanel() {
    mainConfigPanel.setVisible(false);
    footerPanel.removeFromParent();
    gameTabs.setVisible(true);
    main.add(footerPanel);
    setButtonsVisibility(true);
  }

  private class PopupGameOver extends PopinDialog {
    
    public PopupGameOver(EndGame endGame) {
      
      Map<String, Integer> scores = endGame.getPlayerIdToScore();
      List<String> playerIds = serverEmulator.getPlayerIds();
      DialogPanel mainPanel = new DialogPanel();
      
      mainPanel.setOkButtonText(emulatorConstants.restart());
      mainPanel.getOkButton().addTapHandler(new TapHandler() {
        @Override
        public void onTap(TapEvent event) {
          hide();
          resetConfigPanelFields();
          showConfigPanel();
        }
      });
      
      mainPanel.setCancelButtonText(emulatorConstants.cancel());
      mainPanel.getCancelButton().addTapHandler(new TapHandler() {
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
      scoreTable.setText(0, 0, emulatorConstants.player());
      scoreTable.setText(0, 1, emulatorConstants.score());
      scoreTable.getFlexCellFormatter().setStyleName(0, 0, "headerRow");
      scoreTable.getFlexCellFormatter().setStyleName(0, 1, "headerRow");
      
      for (int i = 0; i < playerIds.size(); i++) {
        String playerId = playerIds.get(i);
        int score = scores.get(playerId);
        scoreTable.setText(i + 1, 0, statusMessages.player(playerId));
        scoreTable.setText(i + 1, 1, "" + score);
        if (score == maxScore) {
            scoreTable.getFlexCellFormatter().setStyleName(i + 1, 0, "winnerRow");
            scoreTable.getFlexCellFormatter().setStyleName(i + 1, 1, "winnerRow");
        }
      }
      
      mainPanel.getDialogTitle().setText(emulatorConstants.finalScores());
      mainPanel.getContent().add(scoreTable);
      add(mainPanel);
    }
  }
  
  //send time = "" for infinite timer
  public void setTurnAndTimer(String playerTurnId, String time) {
    currentTurn = playerTurnId;
    turnLabel.setHTML(new SafeHtmlBuilder().appendHtmlConstant(
        statusMessages.playerTurnHTML(playerTurnId)).toSafeHtml());
    if (time == null || time.isEmpty()) {
      time = "";
      timerLabel.setHTML("");
      headerPanelLeft.getElement().getStyle().setTop(10, Unit.PX);
      headerPanelLeft.getElement().getStyle().setFontSize(12, Unit.PT);
    } else {
      String timeHTML = statusMessages.timeLeftOrder(statusMessages.timeLeftBold(), time);
      SafeHtml timeSafeHTML = new SafeHtmlBuilder().appendHtmlConstant(timeHTML).toSafeHtml();
      timerLabel.setHTML(timeSafeHTML);
      headerPanelLeft.getElement().getStyle().setTop(5, Unit.PX);
      headerPanelLeft.getElement().getStyle().setFontSize(10, Unit.PT);
    }
    turnTimer.cancel();
    //Select the turn player tab(iframe)
    if (!singleFrame) {
      try {
        if (playerTurnId.equals(GameApi.AI_PLAYER_ID)) {
          if (isViewerPresent) {
            gameTabs.setSelectedPage(playerFrames.size() - 2);
          } else {
            gameTabs.setSelectedPage(playerFrames.size() - 1);
          }
        } else {
          gameTabs.setSelectedPage(Integer.parseInt(playerTurnId) - 
              Integer.parseInt(ServerEmulator.FIRST_PLAYER_ID));
        }
      } catch(NumberFormatException ex) {
      }
    } else {
      if (playerTurnId.equals(GameApi.AI_PLAYER_ID)) {
        playerTabLabel.setText(emulatorConstants.aiPlayer());
      } else {
        playerTabLabel.setText(statusMessages.player(playerTurnId));
      }
    }
    
    if (time != null && time.length() != 0) {
      turnTimer.scheduleRepeating(1000);
    }
  }
  
  public void resetTimer() {
    turnLabel.setHTML("");
    timerLabel.setHTML("");
    turnTimer.cancel();
  }
  
  public JSONObject getEmulatorConfigAsJSON() {
    JSONObject json = new JSONObject();
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