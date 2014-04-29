package org.smg.gwt.emulator.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.game_api.GameApi.Message;
import org.smg.gwt.emulator.i18n.ConsoleMessages;
import org.smg.gwt.emulator.i18n.EmulatorConstants;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

public class EnhancedConsole extends VerticalPanel {
  
  private List<ConsoleMessage> messages;
  private ConsoleMessages consoleMessages;
  private EmulatorConstants emulatorConstants;
  private static PopupConsole popupDialog;
  
  private static void showPopupDialog() {
    popupDialog.show();
  }
  
  private static class MessagePopup extends PopinDialog {
    private static String ALL;
    private final HTML dataHtml = new HTML();
    private final HorizontalPanel listBoxPanel = new HorizontalPanel(); 
    
    public MessagePopup(GameApiMessage message, ConsoleMessages consoleMessages, 
        EmulatorConstants emulatorConstants) {
      ALL = "---" + emulatorConstants.all() + "---";
      Map<String, Object> messageMap = message.message.toMessage();
      DialogPanel dialogPanel = new DialogPanel();
      String msgDetails = null;
      if (message.type == ConsoleMessageType.OUTGOING) {
        msgDetails = consoleMessages.outgoingMessageDetails(message.playerId);
      } else {
        msgDetails = consoleMessages.incomingMessageDetails(message.playerId);
      }
      dialogPanel.getDialogTitle().setText(consoleMessages.messageNameAndDetails(
          message.message.getMessageName(), msgDetails));
      VerticalPanel panel = new VerticalPanel();
      setupListBox(messageMap);
      ScrollPanel listBoxScrollPanel = new ScrollPanel();
      listBoxScrollPanel.setWidget(listBoxPanel);
      listBoxScrollPanel.setScrollingEnabledX(true);
      listBoxScrollPanel.setShowScrollBarX(true);
      panel.add(listBoxScrollPanel);
      listBoxScrollPanel.refresh();
      ScrollPanel scrollPanel = new ScrollPanel();
      //scrollPanel.setSize("350px", "175px");
      //scrollPanel.add(new Label(message.message.toString()));
      dataHtml.setHTML(messageMap.toString());
      scrollPanel.setWidget(dataHtml);
      scrollPanel.setHeight("150px");
      scrollPanel.setWidth("100%");
      scrollPanel.setShowScrollBarY(true);
      scrollPanel.refresh();
      panel.add(scrollPanel);
      dialogPanel.showCancelButton(false);
      dialogPanel.setOkButtonText(emulatorConstants.ok());
      dialogPanel.getOkButton().addTapHandler(new TapHandler() {
        @Override
        public void onTap(TapEvent event) {
          hide();
          showPopupDialog();
        }
      });
      dialogPanel.getContent().add(panel);
      add(dialogPanel);
    }
    
    private void setupListBox(final Map<String, Object> map) {
      ListBox listBox = new ListBox();
      listBox.addItem(ALL);
      for (String key : map.keySet()) {
        listBox.addItem(key);
      }
      listBox.addChangeHandler(new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent event) {
          ListBox listBox = (ListBox)event.getSource();
          String item = (String)listBox.getValue(listBox.getSelectedIndex());
          if (item.equals(ALL)) {
            removeAfter(listBox);
            dataHtml.setHTML(map.toString()); 
          }
          else {
            Object value = map.get(item);
            dataHtml.setHTML(value != null ? value.toString() : "null");
            removeAfter(listBox);
            if (value instanceof Map && !((Map)value).isEmpty()) {
              setupListBox((Map<String, Object>)value);
            }
            else if (value instanceof List && !((List)value).isEmpty()) {
              setupListBoxForList((List)value);
            }
          }
        }
      });
      listBox.setWidth("120px");
      listBoxPanel.add(listBox);
    }
    
    private void setupListBoxForList(final List<Object> list) {
      ListBox listBox = new ListBox();
      listBox.addItem(ALL);
      for (int i = 0; i < list.size(); i++) {
        Object item = list.get(i);
        String iStr = i + "";
        if (item instanceof Map && ((Map)item).get("type") != null) {
          listBox.addItem(iStr + " (" + ((Map)item).get("type") + ")", iStr);
        }
        else {
          listBox.addItem(iStr, iStr);
        }
      }
      listBox.addChangeHandler(new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent event) {
          ListBox listBox = (ListBox)event.getSource();
          String item = (String)listBox.getValue(listBox.getSelectedIndex());
          if (item.equals(ALL)) {
            removeAfter(listBox);
            dataHtml.setHTML(list.toString()); 
          }
          else {
            Object value = list.get(Integer.parseInt(item));
            dataHtml.setHTML(value != null ? value.toString() : "null");
            removeAfter(listBox);
            if (value instanceof Map && !((Map)value).isEmpty()) {
              setupListBox((Map<String, Object>)value);
            }
            else if (value instanceof List && !((List)value).isEmpty()) {
              setupListBoxForList((List)value);
            } 
          }
        }
      });
      listBox.setWidth("120px");
      listBoxPanel.add(listBox);
      center();
    }
    
    private void removeAfter(ListBox listBox) {
      int currentIndex = listBoxPanel.getWidgetIndex(listBox);
      //remove remaining listboxes when "ALL" is selected
      while (listBoxPanel.getWidgetCount() > currentIndex + 1) {
        listBoxPanel.remove(currentIndex + 1);
      }
    }
  }
  
  private static class InfoPopup extends PopinDialog {
    public InfoPopup(InfoMessage message, EmulatorConstants emulatorConstants, 
        ConsoleMessages consoleMessages) {
      DialogPanel dialogPanel = new DialogPanel();
      dialogPanel.getDialogTitle().setText(consoleMessages.infoMessageConstant());
      ScrollPanel scrollPanel = new ScrollPanel();
      scrollPanel.setWidget(new Label(message.message));
      scrollPanel.setHeight("160px");
      scrollPanel.setShowScrollBarY(true);
      scrollPanel.refresh();
      dialogPanel.getContent().add(scrollPanel);
      dialogPanel.showCancelButton(false);
      dialogPanel.setOkButtonText(emulatorConstants.ok());
      dialogPanel.getOkButton().addTapHandler(new TapHandler() {
        @Override
        public void onTap(TapEvent event) {
          hide();
          showPopupDialog();
        }
      });
      add(dialogPanel);
    }
  }
  
  ClickHandler gameApiMessageClickHandler = new ClickHandler() {
    @Override
    public void onClick(ClickEvent event) {
      GameApiMessage message = (GameApiMessage)event.getSource();
      popupDialog.hide();
      new MessagePopup(message, consoleMessages, emulatorConstants).center();
    }
  };
  
  ClickHandler infoMessageClickHandler = new ClickHandler() {
    @Override
    public void onClick(ClickEvent event) {
      InfoMessage message = (InfoMessage)event.getSource();
      popupDialog.hide();
      new InfoPopup(message, emulatorConstants, consoleMessages).center();
    }
  };

  public EnhancedConsole(ConsoleMessages consoleMessages, EmulatorConstants emulatorConstants) {
    messages = new ArrayList<ConsoleMessage>();
    this.consoleMessages = consoleMessages;
    this.emulatorConstants = emulatorConstants;
  }
  
  public void reset() {
    messages.clear();
    this.clear();
  }
  
  public void addGameApiMessage(Message message, String playerId, ConsoleMessageType type) {
    GameApiMessage consoleMessage = new GameApiMessage(message, playerId, type, consoleMessages);
    consoleMessage.addClickHandler(gameApiMessageClickHandler);
    messages.add(consoleMessage);
    this.insert(consoleMessage, 0);
  }
  
  public void addInfoMessage(String message) {
    InfoMessage infoMessage = new InfoMessage(message, consoleMessages);
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
    
    public GameApiMessage(Message message, String playerId, ConsoleMessageType type,
        ConsoleMessages consoleMessages) {
      this.message = message;
      this.playerId = playerId;
      this.type = type;
      if (type == ConsoleMessageType.OUTGOING) {
        this.setText(consoleMessages.outgoingGameApiMessage(playerId, message.getMessageName()));
      } else {
        this.setText(consoleMessages.incomingGameApiMessage(playerId, message.getMessageName()));
      }
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
    
    public InfoMessage(String message, ConsoleMessages consoleMessages) {
      this.message = message;
      this.setText(consoleMessages.infoMessage(
          message.length() > 40 ? message.substring(0, 40) : message));
      this.getElement().getStyle().setCursor(Cursor.POINTER);
      this.getElement().getStyle().setColor("GREEN");
    }
  }

  public void setPopupReference(PopupConsole popupDialog) {
    this.popupDialog = popupDialog;
  }
}
