package org.smg.gwt.emulator.client;

import java.util.Map;

import org.game_api.GameApi.GameApiJsonHelper;
import org.smg.gwt.emulator.i18n.EmulatorConstants;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.theme.base.DialogCss;
import com.googlecode.mgwt.ui.client.widget.MTextArea;
import com.googlecode.mgwt.ui.client.widget.base.ButtonBase;

public class PopupEditState extends PopinDialog {
  
  private static class DialogButton extends ButtonBase {

    public DialogButton(DialogCss css, String text, boolean isOkButton) {
      super(css);
      setText(text);
      addStyleName(isOkButton ? css.okbutton() : css.cancelbutton());
    }
    
    public DialogButton(String text, boolean isOkButton) {
      this(MGWTStyle.getTheme().getMGWTClientBundle().getDialogCss(), text, isOkButton);
    }
  }
  public interface StateEntered {
    public void setUpdatedStateInfo(
        Map<String, Object> updatedState, Map<String, Object> updatedVisibilityMap,
        Map<String, Integer> updatedTokensMap);
  }
  
  private final MTextArea txtAreaState = new MTextArea();
  private final MTextArea txtAreaVisibility = new MTextArea();
  private final MTextArea txtAreaTokens = new MTextArea();
  
  public PopupEditState(final String existingState, final String visibilityMap,
      final String tokensMap, final StateEntered stateEntered, 
      final EmulatorConstants emulatorConstants) {
    DialogPanel containerPanel = new DialogPanel();
    DialogButton btnCancel = new DialogButton(emulatorConstants.cancel(), false);
    DialogButton btnReset = new DialogButton(emulatorConstants.reset(), true);
    DialogButton btnUpdate = new DialogButton(emulatorConstants.update(), true);
    
    containerPanel.getDialogTitle().setText(emulatorConstants.editState());
    final Label lblStatus = new Label(emulatorConstants.editStateMessage());
    txtAreaState.setText(existingState);
    txtAreaVisibility.setText(visibilityMap);
    txtAreaTokens.setText(tokensMap);
    
    // add listeners
    btnCancel.addTapHandler(new TapHandler() {
      
      @Override
      public void onTap(TapEvent event) {
        hide();
      }
    });
    
    btnReset.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        txtAreaState.setText(existingState);
        txtAreaVisibility.setText(visibilityMap);
        txtAreaTokens.setText(tokensMap);
      }
    });
    
    btnUpdate.addTapHandler(new TapHandler() {
      @SuppressWarnings("unchecked")
      @Override
      public void onTap(TapEvent event) {
        Map<String, Object> updatedStateMap = null;
        Map<String, Object> visibilityMap = null;
        Map<String, Integer> tokensMap = null;
        try {
          updatedStateMap = GameApiJsonHelper.getMapObject(txtAreaState.getText());
          visibilityMap = GameApiJsonHelper.getMapObject(txtAreaVisibility.getText());
          tokensMap = (Map<String, Integer>)(Map<String, ? extends Object>)GameApiJsonHelper.getMapObject(txtAreaTokens.getText());
          hide();
          stateEntered.setUpdatedStateInfo(updatedStateMap, visibilityMap, tokensMap);
        }
        catch(Exception ex) {
          lblStatus.setText(emulatorConstants.enterValidInfo());
        }
      }
    });
    
    // place widgets
    containerPanel.getContent().add(lblStatus);
    containerPanel.getContent().add(new Label(emulatorConstants.state()));
    containerPanel.getContent().add(txtAreaState);
    containerPanel.getContent().add(new Label(emulatorConstants.visibilityMap()));
    containerPanel.getContent().add(txtAreaVisibility);
    containerPanel.getContent().add(new Label(emulatorConstants.visibilityMap()));
    containerPanel.getContent().add(txtAreaTokens);
    
    FlowPanel buttonContainer = new FlowPanel();
    buttonContainer.addStyleName(MGWTStyle.getTheme().getMGWTClientBundle().getDialogCss().footer());
    buttonContainer.add(btnCancel);
    buttonContainer.add(btnReset);
    buttonContainer.add(btnUpdate);
    containerPanel.getContent().add(buttonContainer);
    
    containerPanel.showCancelButton(false);
    containerPanel.showOkButton(false);
    add(containerPanel);
  }
  
  @Override
  public void center() {
    super.center();
    txtAreaState.setFocus(true);
  }
}
