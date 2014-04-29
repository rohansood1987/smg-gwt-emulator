package org.smg.gwt.emulator.client;

import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.dialog.AlertDialog;
import com.googlecode.mgwt.ui.client.dialog.ConfirmDialog;
import com.googlecode.mgwt.ui.client.dialog.ConfirmDialog.ConfirmCallback;
import com.googlecode.mgwt.ui.client.dialog.Dialogs.AlertCallback;
import com.googlecode.mgwt.ui.client.dialog.Dialog;

public final class CustomDialogPanel {
  
  private CustomDialogPanel() {
  }
  
  public static Dialog alert(String title, String message, final AlertCallback callback,
      String buttonName) {
    AlertDialog alertDialog = new AlertDialog(
        MGWTStyle.getTheme().getMGWTClientBundle().getDialogCss(), title, message, buttonName);
    alertDialog.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        if (callback != null) {
          callback.onButtonPressed();
        }
      }
    });
    
    alertDialog.show();
    return alertDialog;
  }
  
  public static Dialog confirm(String title, String message, ConfirmCallback callback,
      String okButtonText, String cancelButtonText) {
    ConfirmDialog confirmDialog = new ConfirmDialog(
        MGWTStyle.getTheme().getMGWTClientBundle().getDialogCss(), 
        title, message, callback, okButtonText, cancelButtonText);
    confirmDialog.show();
    return confirmDialog;
  }
}

