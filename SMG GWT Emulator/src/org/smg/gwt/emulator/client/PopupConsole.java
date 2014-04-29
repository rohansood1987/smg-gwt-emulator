package org.smg.gwt.emulator.client;

import org.smg.gwt.emulator.i18n.EmulatorConstants;

import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

public class PopupConsole extends PopinDialog {
  
  public PopupConsole(EnhancedConsole console, EmulatorConstants emulatorConstants) {
    console.setPopupReference(this);
    DialogPanel dialogPanel = new DialogPanel();
    dialogPanel.getDialogTitle().setText(emulatorConstants.console());
    dialogPanel.showCancelButton(false);
    dialogPanel.setOkButtonText(emulatorConstants.close());
    dialogPanel.getOkButton().addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        hide();
      }
    });
    ScrollPanel scrollPanel = new ScrollPanel();
    scrollPanel.setWidget(console);
    scrollPanel.setShowScrollBarY(true);
    scrollPanel.setHeight("160px");
    dialogPanel.getContent().add(scrollPanel);
    add(dialogPanel);
    scrollPanel.refresh();
  }
}
