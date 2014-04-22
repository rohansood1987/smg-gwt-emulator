package org.smg.gwt.emulator.client;

import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.WidgetList;

public class PopupLoadState extends PopinDialog {
    
  final List<Widget> widgetsToHide;
  
  public PopupLoadState(final FlexTable table, final List<Widget> widgets) {
    
    this.widgetsToHide = widgets;
    // init
    Button btnCancel = new Button("Cancel");
   
    btnCancel.setSmall(true);

    // add listeners
    btnCancel.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        hide();
        GwtEmulatorGraphics.setVisible(widgetsToHide, true);
      }
    });
      
    // place widgets
    WidgetList mainVertPanel = new WidgetList();
    mainVertPanel.add(table);
    mainVertPanel.add(btnCancel);
    add(mainVertPanel);
  }
  
  @Override
  public void center() {
    super.center();
    GwtEmulatorGraphics.setVisible(widgetsToHide, false);
  }
}
