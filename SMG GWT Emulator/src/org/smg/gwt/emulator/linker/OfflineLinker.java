package org.smg.gwt.emulator.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;

/**
 * This code was taken from the following blog-post:
 * http://classpattern.com/gwt-offline-application.html
 *
 */
@LinkerOrder(Order.POST) 
public class OfflineLinker extends AbstractLinker {

  @Override
  public String getDescription() {
    return "Offline Linker";
  }

  @Override
  public ArtifactSet link(TreeLogger logger, LinkerContext context,ArtifactSet artifacts) throws UnableToCompleteException {
    ArtifactSet artifactset = new ArtifactSet(artifacts);
    String renamedAppName = "smg_gwt_emulator";

    StringBuilder builder= new StringBuilder("CACHE MANIFEST\n");
    builder.append("# Cache Version 10\n");
    builder.append("CACHE:\n");

    builder.append("/GwtEmulator.css\n");
    builder.append("/GwtEmulator.html\n");    
    for(EmittedArtifact emitted: artifacts.find(EmittedArtifact.class))
    {
      if(emitted.isPrivate())
      {
        logger.log(Type.DEBUG, "-- offline cache. Skipping: " + emitted.getPartialPath());
        System.out.println(emitted.getPartialPath());
        continue;
      }
      if(emitted.getPartialPath().endsWith(".symbolMap"))continue;
      if(emitted.getPartialPath().endsWith(".txt"))continue;
      builder.append("/" + renamedAppName + "/" + emitted.getPartialPath()).append("\n");
    }
    builder.append("/" + renamedAppName + "/hosted.html\n");
    builder.append("/" + renamedAppName + "/smg_gwt_emulator.nocache.js\n");
    builder.append("NETWORK:\n");
    builder.append("*\n");
    EmittedArtifact manifest= emitString(logger, builder.toString(), "offline.appcache");
    artifactset.add(manifest);
    return artifactset;
  }
}