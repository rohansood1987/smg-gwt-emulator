package org.smg.gwt.emulator.client;

import org.smg.gwt.emulator.backend.ServerEmulator;
import org.smg.gwt.emulator.data.GameApiJsonHelper;
import org.smg.gwt.emulator.data.GameApi.Message;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class GwtEmulatorGraphics extends Composite {
  public interface GwtEmulatorGraphicsUiBinder extends UiBinder<Widget, GwtEmulatorGraphics> {
  }
  
  @UiField
  TextBox txtGameUrl;
  
  @UiField
  Button btnStart;
  
  @UiField
  AbsolutePanel gameTabsPanel;
  
  @UiField
  TextArea console;
  
  @UiField
  ListBox listNumPlayers;
  
  private TabLayoutPanel gameTabs;
  private ServerEmulator serverEmulator;
  
  public GwtEmulatorGraphics() {
    GwtEmulatorGraphicsUiBinder uiBinder = GWT.create(GwtEmulatorGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  @UiHandler("btnStart")
  void onClickStartButton(ClickEvent e) {
    // initialize ServerEmulator
    int numberOfPlayers = Integer.parseInt(
        listNumPlayers.getValue(listNumPlayers.getSelectedIndex()));
    serverEmulator = new ServerEmulator(numberOfPlayers, this);
    gameTabs = new TabLayoutPanel(1.5, Unit.EM);
    gameTabs.setSize("800px", "400px");
    String url = txtGameUrl.getText();
    for (int i = 0; i < numberOfPlayers; i++) {
      //TODO: Add actual player name as tab name here
      Frame frame = new Frame(url);
      frame.getElement().setId("frame" + i);
      frame.setSize("750px", "350px");
      gameTabs.add(frame, "Player " + (i + 1));
    }
    gameTabsPanel.add(gameTabs);
    injectEventListener(serverEmulator, numberOfPlayers);
  }
  
  private native void injectEventListener(ServerEmulator emulator, int numberOfPlayers) /*-{
    function postMessageListener(e) {
      for(var i = 0; i < numberOfPlayers; i++) {
        var frameName = "frame"+i;
        var frame = $doc.getElementById(frameName);
        //if(frame) { $wnd.alert(frameName + " exists");}
        //else {$wnd.alert(frameName + " doesnt exist");}
        
        if(e.source == frame.contentWindow) {
          //$wnd.alert(frameName + " attached!");
          emulator.@org.smg.gwt.emulator.backend.ServerEmulator::eventListner(Ljava/lang/String;Ljava/lang/Integer;)(JSON.stringify(e.data), i);
        }
      }
    }
    $wnd.addEventListener("message", postMessageListener, false);
  }-*/;
  
  public void sendMessage(int playerIndex, Message message) {
    postMessageToFrame("frame"+playerIndex, GameApiJsonHelper.getJsonString(message));
  }
  
  private static native void postMessageToFrame(String frameName, String message) /*-{
    $doc.getElementById(frameName).postMessage(JSON.parse(message), "*");
  }-*/;
  
  public void logToConsole(String msg) {
    console.setText(console.getText() + "\n\n" + msg);
  }
  
}
