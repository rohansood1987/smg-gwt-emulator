package org.smg.gwt.emulator.client;

import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

public class PopupConsole extends PopinDialog {
  
  public PopupConsole(EnhancedConsole console) {
    console.setPopupReference(this);
    DialogPanel dialogPanel = new DialogPanel();
    dialogPanel.getDialogTitle().setText("Console");
    dialogPanel.showCancelButton(false);
    dialogPanel.setOkButtonText("Close");
    dialogPanel.getOkButton().addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        hide();
      }
    });
    ScrollPanel scrollPanel = new ScrollPanel();
    scrollPanel.setWidget(console);
    dialogPanel.getContent().add(scrollPanel);
    add(dialogPanel);
    scrollPanel.refresh();
  }
  
  public void temporaryHide() {
    super.hide();
  }
  
  @Override
  public void hide() {
    super.hide();
    GwtEmulatorGraphics.refreshContainer();
  }
}
