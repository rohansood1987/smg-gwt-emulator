package org.smg.gwt.emulator.client;

import java.util.Map;

import org.game_api.GameApi.GameApiJsonHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PopupEditState extends DialogBox {
  
  public interface StateEntered {
    public void setUpdatedStateInfo(
        Map<String, Object> updatedState, Map<String, Object> updatedVisibilityMap,
        Map<String, Integer> updatedTokensMap);
  }
  
  final TextArea txtAreaState = new TextArea();
  final TextArea txtAreaVisibility = new TextArea();
  final TextArea txtAreaTokens = new TextArea();
  
  
  public PopupEditState(final String existingState, final String visibilityMap,
      final String tokensMap, final StateEntered stateEntered) {
    
    // init
    setText("State Editor");
    Button btnCancel = new Button("Cancel");
    Button btnReset = new Button("Reset");
    Button btnUpdate = new Button("Update");
    final Label lblStatus = new Label("Please edit the state.");
    txtAreaState.setText(existingState);
    txtAreaVisibility.setText(visibilityMap);
    txtAreaTokens.setText(tokensMap);
    
    
    // add listeners
    btnCancel.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        hide();
      }
    });
    
    btnReset.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        txtAreaState.setText(existingState);
        txtAreaVisibility.setText(visibilityMap);
        txtAreaTokens.setText(tokensMap);
      }
    });
    
    btnUpdate.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
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
    VerticalPanel mainVertPanel = new VerticalPanel();
    mainVertPanel.add(lblStatus);
    mainVertPanel.add(new Label("State:"));
    txtAreaState.setSize("400px", "120px");
    mainVertPanel.add(txtAreaState);
    mainVertPanel.add(new Label("Visibility Map:"));
    txtAreaVisibility.setSize("400px", "120px");
    mainVertPanel.add(txtAreaVisibility);
    mainVertPanel.add(new Label("Tokens Map:"));
    txtAreaTokens.setSize("400px", "60px");
    mainVertPanel.add(txtAreaTokens);
    HorizontalPanel btnsPanel = new HorizontalPanel();
    btnsPanel.add(btnCancel);
    btnsPanel.add(btnReset);
    btnsPanel.add(btnUpdate);
    mainVertPanel.add(btnsPanel);
    setWidget(mainVertPanel);
  }
  
  @Override
  public void center() {
    super.center();
    txtAreaState.setFocus(true);
  }

}
