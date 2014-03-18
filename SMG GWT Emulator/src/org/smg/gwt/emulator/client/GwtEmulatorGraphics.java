package org.smg.gwt.emulator.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
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
  Frame gameFrame;
  
  @UiField
  TextArea console;
  
  public GwtEmulatorGraphics() {
    GwtEmulatorGraphicsUiBinder uiBinder = GWT.create(GwtEmulatorGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
    injectEventListener(this);
  }
  
  @UiHandler("btnStart")
  void onClickStartButton(ClickEvent e) {
    String url = txtGameUrl.getText();
    gameFrame.setUrl(url);
  }
  
  public void eventListner(String message) {
    console.setText(console.getText() + "\nMsg: " + message);
  }
  
  private native void injectEventListener(GwtEmulatorGraphics graphics) /*-{
    function postMessageListener(e) {
      graphics.@org.smg.gwt.emulator.client.GwtEmulatorGraphics::eventListner(Ljava/lang/String;)(e.data);
    }
    $wnd.addEventListener("message", postMessageListener, false);
  }-*/;
  
}
