package org.smg.gwt.emulator.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.animation.AnimationHelper;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GwtEmulatorEntryPoint implements EntryPoint {


  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    
    // set viewport and other settings for mobile
    MGWT.applySettings(MGWTSettings.getAppSetting());

    // build animation helper and attach it
    AnimationHelper animationHelper = new AnimationHelper();
    RootPanel.get().add(animationHelper);

    GwtEmulatorGraphics graphicsLayout = new GwtEmulatorGraphics();
    
    // animate
    animationHelper.goTo(graphicsLayout, Animation.SLIDE);
  }
}
