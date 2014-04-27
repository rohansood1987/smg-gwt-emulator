package org.smg.gwt.emulator.client;

import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;

public class PopupConsole extends PopinDialog {
  
  public PopupConsole(EnhancedConsole console) {
    DialogPanel containerPanel = new DialogPanel();
    containerPanel.getDialogTitle().setText("Console");
    containerPanel.setCancelButtonText("Close");
    containerPanel.getCancelButton().addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        hide();
      }
    });
    containerPanel.getContent().add(console);
    add(containerPanel);
  }
  
  @Override
  public void hide() {
    super.hide();
    GwtEmulatorGraphics.refreshContainer();
  }
}
