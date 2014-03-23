package org.smg.gwt.emulator.client;

import com.emitrom.flash4j.clientio.client.ClientIO;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GwtEmulatorEntryPoint implements EntryPoint {


  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    
    GwtEmulatorGraphics graphicsLayout = new GwtEmulatorGraphics();
    
    FlowPanel flowPanel = new FlowPanel();
    flowPanel.add(graphicsLayout);
    RootPanel.get("mainDiv").add(flowPanel);
    ClientIO.init();
  }
}
