package org.smg.gwt.emulator.client;

import java.util.Map;

import org.smg.gwt.emulator.backend.ServerEmulator;
import org.smg.gwt.emulator.data.GameApi.Message;
import org.smg.gwt.emulator.data.GameApiJsonHelper;

import com.emitrom.flash4j.clientio.client.ClientIO;
import com.emitrom.flash4j.core.client.events.Event;
import com.emitrom.flash4j.core.client.events.handlers.EventHandler;
import com.emitrom.flash4j.core.client.net.FileFilter;
import com.emitrom.flash4j.core.client.net.FileReference;
import com.emitrom.flash4j.core.client.utils.ByteArray;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
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
  TextArea console;
  
  @UiField
  Button btnEditState;
  
  @UiField
  ListBox listNumPlayers;
  
  @UiField
  Button btnLoadState;
  
  @UiField
  Button btnSaveState;
  
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
  private int gameFrameWidth, gameFrameHeight;
  
  public GwtEmulatorGraphics() {
    GwtEmulatorGraphicsUiBinder uiBinder = GWT.create(GwtEmulatorGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    gamePanel.setVisible(false);
  }
  
  @UiHandler("btnStart")
  void onClickStartButton(ClickEvent e) {
    if (!validatateConfigInput()) {
      return;
    }
    int numberOfPlayers = Integer.parseInt(
        listNumPlayers.getValue(listNumPlayers.getSelectedIndex()));
    // initialize ServerEmulator
    serverEmulator = new ServerEmulator(numberOfPlayers, this);
    gameTabs = new TabLayoutPanel(1.5, Unit.EM);
    gameTabs.setSize(gameFrameWidth + "px", (gameFrameHeight + 25) + "px");
    String url = txtGameUrl.getText();
    for (int i = 0; i < numberOfPlayers; i++) {
      //TODO: Add actual player name as tab name here
      Frame frame = new Frame(url);
      frame.getElement().setId("frame" + i);
      frame.setSize("100%", "100%");
      gameTabs.add(frame, "Player " + (i + 1));
    }
    gameTabsPanel.add(gameTabs);
    console.setSize("400px", "500px");
    console.setEnabled(false);
    injectEventListener(serverEmulator, numberOfPlayers);
    addSlider(0);
    mainConfigPanel.setVisible(false);
    gamePanel.setVisible(true);
  }
  
  private boolean validatateConfigInput() {
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
    return true;
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
        new PopupEditState.StateEntered() {      
          @Override
          public void setUpdatedStateInfo(Map<String, Object> updatedState, Map<String, Object> updatedVisibilityMap) {
            serverEmulator.updateStateManually(updatedState, updatedVisibilityMap);
          }
    }).center();
  }
  
  @UiHandler("btnSaveState")
  void onClickSaveStateButton(ClickEvent e) {
    String data = serverEmulator.saveGameStateJSONAsString();
    ClientIO.saveFile(data, "SaveState.txt");
  }
  
  @UiHandler("btnLoadState")
  void onClickLoadStateButton(ClickEvent e) {
    final FileReference fileReference = ClientIO.browse(new FileFilter("LoadState", ".txt"));
    fileReference.addEventHandler(Event.SELECT, new EventHandler() {
        @Override
        public void onEvent(Event event) {
            fileReference.load();
            fileReference.addEventHandler(Event.COMPLETE, new EventHandler() {
                @Override
                public void onEvent(Event event) {
                    ByteArray data = fileReference.getData();
                    String content = data.readUTFBytes(data.getBytesAvailable());
                    serverEmulator.loadGameStateFromJSON(JSONParser.parseStrict(content).isObject());
                    serverEmulator.resetSliderState();
                }
            });
        }
    });
  }
  
  private native void injectEventListener(ServerEmulator emulator, int numberOfPlayers) /*-{
    function postMessageListener(e) {
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
    $wnd.addEventListener("message", postMessageListener, false);
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
  
}