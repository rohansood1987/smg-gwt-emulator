package org.smg.gwt.emulator.client;

import org.smg.gwt.emulator.i18n.EmulatorConstants;

import com.google.gwt.user.client.ui.FlexTable;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;

public class PopupLoadState extends PopinDialog {
  
  public PopupLoadState(final FlexTable table, EmulatorConstants emulatorConstants) {
    
    // init
    DialogPanel containerPanel = new DialogPanel();
    containerPanel.getDialogTitle().setText(emulatorConstants.loadState());
    containerPanel.setCancelButtonText(emulatorConstants.cancel());
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
}
