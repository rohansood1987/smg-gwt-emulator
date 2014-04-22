package org.smg.gwt.emulator.client;

import com.google.gwt.user.client.ui.FlexTable;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.WidgetList;

public class PopupLoadState extends PopinDialog {
    
  public PopupLoadState(final FlexTable table) {
    
    // init
   /* setText("Load State");*/
    Button btnCancel = new Button("Cancel");
    
    // add listeners
    btnCancel.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        hide();
      }
    });
      
    // place widgets
    WidgetList mainVertPanel = new WidgetList();
    mainVertPanel.add(table);
    mainVertPanel.add(btnCancel);
    add(mainVertPanel);
  }
}
