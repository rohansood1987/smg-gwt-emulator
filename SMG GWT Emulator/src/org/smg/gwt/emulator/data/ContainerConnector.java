package org.smg.gwt.emulator.data;

import java.util.List;

import org.smg.gwt.emulator.data.GameApi.Container;
import org.smg.gwt.emulator.data.GameApi.Game;
import org.smg.gwt.emulator.data.GameApi.GameReady;
import org.smg.gwt.emulator.data.GameApi.MakeMove;
import org.smg.gwt.emulator.data.GameApi.Message;
import org.smg.gwt.emulator.data.GameApi.Operation;
import org.smg.gwt.emulator.data.GameApi.UpdateUI;
import org.smg.gwt.emulator.data.GameApi.VerifyMove;
import org.smg.gwt.emulator.data.GameApi.VerifyMoveDone;

public class ContainerConnector implements Container {
  
  private final Game game;
  
  public ContainerConnector(Game game) {
    this.game = game;
    injectEventListener(this);
  }

  @Override
  public void sendGameReady() {
    GameReady gameReady = new GameReady();
    postMessageToParent(GameApiJsonHelper.getJsonString(gameReady));
  }

  @Override
  public void sendVerifyMoveDone(VerifyMoveDone verifyMoveDone) {
    postMessageToParent(GameApiJsonHelper.getJsonString(verifyMoveDone));
  }

  @Override
  public void sendMakeMove(List<Operation> operations) {
    MakeMove makeMove = new MakeMove(operations);
    postMessageToParent(GameApiJsonHelper.getJsonString(makeMove));
  }
  
  public static native void postMessageToParent(String message) /*-{
    $wnd.parent.postMessage(JSON.parse(message), "*");
  }-*/;
  
  public void eventListner(String message) {
    Message messageObj = GameApiJsonHelper.getMessageObject(message);
    if (messageObj instanceof UpdateUI) {
      game.sendUpdateUI((UpdateUI) messageObj);
    } else if (messageObj instanceof VerifyMove) {
      game.sendVerifyMove((VerifyMove) messageObj);
    }
  }
  
  private native void injectEventListener(ContainerConnector containerConnector) /*-{
    function postMessageListener(e) {
       containerConnector.@org.smg.gwt.emulator.data.ContainerConnector::eventListner(Ljava/lang/String;)(JSON.stringify(e.data));
    }
    $wnd.addEventListener("message", postMessageListener, false);
  }-*/;

}
