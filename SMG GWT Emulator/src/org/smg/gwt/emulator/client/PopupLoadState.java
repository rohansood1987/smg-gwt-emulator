package org.smg.gwt.emulator.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PopupLoadState extends DialogBox {
    
  public PopupLoadState(final FlexTable table) {
    
    // init
    setText("Load State");
    Button btnCancel = new Button("Cancel");
    
    // add listeners
    btnCancel.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        hide();
      }
    });
      
    // place widgets
    VerticalPanel mainVertPanel = new VerticalPanel();
    mainVertPanel.add(table);
    mainVertPanel.add(btnCancel);
    setWidget(mainVertPanel);
  }
}
