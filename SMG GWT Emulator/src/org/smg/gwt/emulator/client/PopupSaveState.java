package org.smg.gwt.emulator.client;

import java.util.Set;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.ConfirmDialog.ConfirmCallback;
import com.googlecode.mgwt.ui.client.dialog.Dialogs;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.MTextBox;
import com.googlecode.mgwt.ui.client.widget.WidgetList;

public class PopupSaveState extends PopinDialog {
  
  public interface NameEntered {
    public void setName(String name);
  }
  final MTextBox stateName = new MTextBox();
  
  @SuppressWarnings("deprecation")
  public PopupSaveState(final NameEntered name, final Set<String> keySet) {
    
    // init
    Button btnCancel = new Button("Cancel");
    Button btnSave = new Button("Save");
    
    btnCancel.setSmall(true);
    btnSave.setSmall(true);

    final Label lblStatus = new Label("Please enter name to save this state");
    
    // add listeners
    btnCancel.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        hide();
      }
    });
    
    btnSave.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        try {
          String nameValue = stateName.getValue();
          if (nameValue.isEmpty()) {
            Dialogs.alert("Alert", "Please enter valid name", null);
          } else if (keySet.contains(nameValue)) {
            Dialogs.confirm("Confirm", "Name already exists. Do you want to overwrite?", new ConfirmCallback() {
              @Override
              public void onOk() {
                name.setName(stateName.getValue());
                hide();
              }
              @Override
              public void onCancel() {
              }
            });
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
    WidgetList mainVertPanel = new WidgetList();
    mainVertPanel.add(lblStatus);
    mainVertPanel.add(stateName);
    HorizontalPanel btnsPanel = new HorizontalPanel();
    btnsPanel.add(btnCancel);
    btnsPanel.add(btnSave);
    DOM.setStyleAttribute(btnsPanel.getElement(), "margin", "auto");
    mainVertPanel.add(btnsPanel);
    add(mainVertPanel);
  }
  
  @Override
  public void center() {
    super.center();
    stateName.setFocus(true);
  }
  
}
