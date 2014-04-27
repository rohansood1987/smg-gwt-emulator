package org.smg.gwt.emulator.client;

import com.google.gwt.user.client.ui.FlexTable;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;

public class PopupLoadState extends PopinDialog {
  
  public PopupLoadState(final FlexTable table) {
    
    // init
    DialogPanel containerPanel = new DialogPanel();
    containerPanel.getDialogTitle().setText("Load State");
    // add listeners
    containerPanel.getCancelButton().addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        hide();
      }
    });
      
    // place widgets
    containerPanel.getContent().add(table);
    containerPanel.showOkButton(false);
    add(containerPanel);
  }
  
  @Override
  public void hide() {
    super.hide();
    GwtEmulatorGraphics.refreshContainer();
  }
}
