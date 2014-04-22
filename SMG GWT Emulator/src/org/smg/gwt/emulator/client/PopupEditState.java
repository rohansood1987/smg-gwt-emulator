package org.smg.gwt.emulator.client;

import java.util.Map;

import org.game_api.GameApi.GameApiJsonHelper;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.MTextArea;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;
import com.googlecode.mgwt.ui.client.widget.WidgetList;

public class PopupEditState extends PopinDialog {
  
  public interface StateEntered {
    public void setUpdatedStateInfo(
        Map<String, Object> updatedState, Map<String, Object> updatedVisibilityMap,
        Map<String, Integer> updatedTokensMap);
  }
  
  final MTextArea txtAreaState = new MTextArea();
  final MTextArea txtAreaVisibility = new MTextArea();
  final MTextArea txtAreaTokens = new MTextArea();
  
  
  public PopupEditState(final String existingState, final String visibilityMap,
      final String tokensMap, final StateEntered stateEntered) {
    
    // init
    /*setText("State Editor");*/
    Button btnCancel = new Button("Cancel");
    Button btnReset = new Button("Reset");
    Button btnUpdate = new Button("Update");
    final Label lblStatus = new Label("Please edit the state.");
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
          lblStatus.setText("Please enter valid information");
        }
      }
    });
    
    // place widgets
    ScrollPanel scrollPanel = new ScrollPanel();
    WidgetList mainVertPanel = new WidgetList();
    scrollPanel.setWidget(mainVertPanel);
    mainVertPanel.add(lblStatus);
    mainVertPanel.add(new Label("State:"));
    //txtAreaState.setSize("400px", "120px");
    mainVertPanel.add(txtAreaState);
    mainVertPanel.add(new Label("Visibility Map:"));
    //txtAreaVisibility.setSize("400px", "120px");
    mainVertPanel.add(txtAreaVisibility);
    mainVertPanel.add(new Label("Tokens Map:"));
    //txtAreaTokens.setSize("400px", "60px");
    mainVertPanel.add(txtAreaTokens);
    HorizontalPanel btnsPanel = new HorizontalPanel();
    btnsPanel.add(btnCancel);
    btnsPanel.add(btnReset);
    btnsPanel.add(btnUpdate);
    mainVertPanel.add(btnsPanel);
    add(scrollPanel);
  }
  
  @Override
  public void center() {
    super.center();
    txtAreaState.setFocus(true);
  }

}
