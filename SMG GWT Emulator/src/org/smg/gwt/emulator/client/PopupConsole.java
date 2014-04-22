package org.smg.gwt.emulator.client;

import java.util.List;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

public class PopupConsole extends PopinDialog {
  
  private final List<Widget> widgetsToHide;
  private Button closeBtn;
  
  public PopupConsole(EnhancedConsole console, final List<Widget> widgetsToHide) {
    this.widgetsToHide = widgetsToHide;
    LayoutPanel panel = new LayoutPanel();
    ScrollPanel scrollPanel = new ScrollPanel();
    
    panel.add(scrollPanel);
    scrollPanel.add(console);
    closeBtn = new Button("Close");
    closeBtn.addTapHandler(new TapHandler() {
      @Override
      public void onTap(TapEvent event) {
        hide();
        GwtEmulatorGraphics.setVisible(widgetsToHide, true);
      }
    });
    panel.add(closeBtn);
    add(panel);
  }
  
  @Override
  public void center() {
    super.center();
    GwtEmulatorGraphics.setVisible(widgetsToHide, false);
  }

}
