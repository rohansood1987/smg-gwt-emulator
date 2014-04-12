package org.smg.gwt.emulator.client;

import java.util.ArrayList;
import java.util.List;

import org.game_api.GameApi.Message;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EnhancedConsole extends VerticalPanel {
  
  private List<ConsoleMessage> messages;
  
  private static class MessagePopup extends DialogBox {
    public MessagePopup(GameApiMessage message) {
      setText(message.message.getMessageName());
      VerticalPanel panel = new VerticalPanel();
      Label lblFrom = new Label("From: Player " + message.playerId);
      lblFrom.getElement().getStyle().setFontWeight(FontWeight.BOLD);
      panel.add(lblFrom);
      ScrollPanel scrollPanel = new ScrollPanel();
      scrollPanel.setSize("350px", "175px");
      scrollPanel.add(new Label(message.message.toString()));
      panel.add(scrollPanel);
      Button btnOk = new Button("OK");
      btnOk.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          hide();
        }
      });
      panel.add(btnOk);
      setWidget(panel);
    }
  }
  
  private static class InfoPopup extends DialogBox {
    public InfoPopup(InfoMessage message) {
      VerticalPanel panel = new VerticalPanel();
      setText("Info Message");
      ScrollPanel scrollPanel = new ScrollPanel();
      scrollPanel.setSize("350px", "175px");
      scrollPanel.add(new Label(message.message));
      panel.add(scrollPanel);
      Button btnOk = new Button("OK");
      btnOk.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          hide();
        }
      });
      panel.add(btnOk);
      setWidget(panel);
    }
  }
  
  ClickHandler gameApiMessageClickHandler = new ClickHandler() {
    @Override
    public void onClick(ClickEvent event) {
      GameApiMessage message = (GameApiMessage)event.getSource();
      new MessagePopup(message).center();
    }
  };
  
  ClickHandler infoMessageClickHandler = new ClickHandler() {
    @Override
    public void onClick(ClickEvent event) {
      InfoMessage message = (InfoMessage)event.getSource();
      new InfoPopup(message).center();
    }
  };
  
  public EnhancedConsole() {
    messages = new ArrayList<ConsoleMessage>();
  }
  
  public void reset() {
    messages.clear();
    this.clear();
  }
  
  public void addGameApiMessage(Message message, String playerId, ConsoleMessageType type) {
    GameApiMessage consoleMessage = new GameApiMessage(message, playerId, type);
    consoleMessage.addClickHandler(gameApiMessageClickHandler);
    messages.add(consoleMessage);
    this.insert(consoleMessage, 0);
  }
  
  public void addInfoMessage(String message) {
    InfoMessage infoMessage = new InfoMessage(message);
    infoMessage.addClickHandler(infoMessageClickHandler);
    messages.add(infoMessage);
    this.insert(infoMessage, 0);
  }
  
  public static enum ConsoleMessageType {
    INFO, OUTGOING, INCOMING
  }
  
  public static abstract class ConsoleMessage extends Label {
    public abstract ConsoleMessageType getMessageType();
  }
  
  public static class GameApiMessage extends ConsoleMessage {
    
    private Message message;
    private String playerId;
    private ConsoleMessageType type;
    
    public GameApiMessage(Message message, String playerId, ConsoleMessageType type) {
      this.message = message;
      this.playerId = playerId;
      this.type = type;
      this.setText((type == ConsoleMessageType.OUTGOING ? "To: " : "From: ") + 
            playerId + ", Type: " + message.getMessageName());
      this.getElement().getStyle().setCursor(Cursor.POINTER);
    }

    @Override
    public ConsoleMessageType getMessageType() {
      return type;
    }
  }

  public static class InfoMessage extends ConsoleMessage {
    private String message;
    
    @Override
    public ConsoleMessageType getMessageType() {
      return ConsoleMessageType.INFO;
    }
    
    public InfoMessage(String message) {
      this.message = message;
      this.setText("Info: " + message.substring(0, 40));
      this.getElement().getStyle().setCursor(Cursor.POINTER);
      this.getElement().getStyle().setColor("GREEN");
    }
  }
}
