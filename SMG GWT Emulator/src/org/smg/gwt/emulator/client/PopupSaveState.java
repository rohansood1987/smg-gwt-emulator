package org.smg.gwt.emulator.client;

import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PopupSaveState extends DialogBox {
  
  public interface NameEntered {
    public void setName(String name);
  }
  final TextBox stateName = new TextBox();
  
  public PopupSaveState(final NameEntered name, final Set<String> keySet) {
    
    // init
    setText("Save State");
    Button btnCancel = new Button("Cancel");
    Button btnSave = new Button("Save");
    final Label lblStatus = new Label("Please enter name to save this state");
    
    // add listeners
    btnCancel.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        hide();
      }
    });
    
    btnSave.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        try {
          String nameValue = stateName.getValue();
          if (nameValue.isEmpty()) {
            Window.alert("Please enter valid name");
          } else if (keySet.contains(nameValue)) {
            Window.alert("Name already exists. Please enter valid name");
          } else {
            name.setName(stateName.getValue());
            hide();
          }
        }
        catch(Exception ex) {
          lblStatus.setText("Please enter valid name");
        }
      }
    });
    
    // place widgets
    VerticalPanel mainVertPanel = new VerticalPanel();
    mainVertPanel.add(lblStatus);
    mainVertPanel.add(stateName);
    HorizontalPanel btnsPanel = new HorizontalPanel();
    btnsPanel.add(btnCancel);
    btnsPanel.add(btnSave);
    mainVertPanel.add(btnsPanel);
    setWidget(mainVertPanel);
  }
  
  @Override
  public void center() {
    super.center();
    stateName.setFocus(true);
  }

}
