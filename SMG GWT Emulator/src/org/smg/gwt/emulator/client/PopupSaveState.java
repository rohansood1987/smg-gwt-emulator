package org.smg.gwt.emulator.client;

import java.util.Set;

import org.smg.gwt.emulator.i18n.EmulatorConstants;

import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.ConfirmDialog.ConfirmCallback;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.Dialogs;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.MTextBox;

public class PopupSaveState extends PopinDialog {
  
  public interface NameEntered {
    public void setName(String name);
  }
  final MTextBox stateName = new MTextBox();
  
  public PopupSaveState(final NameEntered name, final Set<String> keySet, 
      final EmulatorConstants emulatorConstants) {
    
    // init
    DialogPanel containerPanel = new DialogPanel();
    containerPanel.setOkButtonText(emulatorConstants.save());
    containerPanel.getDialogTitle().setText(emulatorConstants.saveState());
    final Label lblStatus = new Label(emulatorConstants.saveStateMessage());
    containerPanel.setCancelButtonText(emulatorConstants.cancel());
    // add listeners
    containerPanel.getCancelButton().addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        hide();
      }
    });
    
    containerPanel.getOkButton().addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        try {
          String nameValue = stateName.getValue();
          if (nameValue.isEmpty()) {
            CustomDialogPanel.alert(emulatorConstants.alert(), emulatorConstants.enterValidName(),
                null, emulatorConstants.ok());
          } else if (keySet.contains(nameValue)) {
            CustomDialogPanel.confirm(emulatorConstants.confirm(), 
                emulatorConstants.confirmOverwrite(), new ConfirmCallback() {
              @Override
              public void onOk() {
                name.setName(stateName.getValue());
                hide();
              }
              @Override
              public void onCancel() {
              }
            }, emulatorConstants.ok(), emulatorConstants.cancel());
          } else {
            name.setName(stateName.getValue());
            hide();
          }
        }
        catch(Exception ex) {
          lblStatus.setText(emulatorConstants.enterValidName());
        }
      }
    });
    
    // place widgets
    containerPanel.getContent().add(lblStatus);
    containerPanel.getContent().add(stateName);
    add(containerPanel);
  }
  
  @Override
  public void center() {
    super.center();
    stateName.setFocus(true);
  }
  
  @Override
  public void hide() {
    super.hide();
    GwtEmulatorGraphics.refreshContainer();
  }
}
