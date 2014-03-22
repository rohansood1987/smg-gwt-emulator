package org.smg.gwt.emulator.client;

import java.util.Map;

import org.smg.gwt.emulator.data.GameApi.Game;
import org.smg.gwt.emulator.data.GameApiJsonHelper;

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
        Map<String, Object> updatedState, Map<String, Object> updatedVisibilityMap);
  }
  
  final TextArea txtAreaState = new TextArea();
  final TextArea txtAreaVisibility = new TextArea();
  
  public PopupEditState(final String existingState, final String visibilityMap,
      final StateEntered stateEntered) {
    
    // init
    setText("State Editor");
    Button btnCancel = new Button("Cancel");
    Button btnReset = new Button("Reset");
    Button btnUpdate = new Button("Update");
    final Label lblStatus = new Label("Please edit the state.");
    txtAreaState.setText(existingState);
    txtAreaVisibility.setText(visibilityMap);
    
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
      }
    });
    
    btnUpdate.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        Map<String, Object> updatedStateMap = null;
        Map<String, Object> visibilityMap = null;
        try {
          updatedStateMap = GameApiJsonHelper.getMapObject(txtAreaState.getText());
          visibilityMap = GameApiJsonHelper.getMapObject(txtAreaVisibility.getText());
          hide();
          stateEntered.setUpdatedStateInfo(updatedStateMap, visibilityMap);
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
    mainVertPanel.add(txtAreaState);
    mainVertPanel.add(new Label("Visibility Map:"));
    mainVertPanel.add(txtAreaVisibility);
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
