package com.ai.mcp.client;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.Instant;
import java.util.UUID;

@PageTitle("Chat Bot")
@Menu(title = "Chatbot", order = 0)
@Route("")
@RouteAlias("chat-bot")
public class ChatBotView extends Composite<VerticalLayout> {

    private final ChatService chatService;
    private final MessageList messageList;
    private final String chatId = UUID.randomUUID().toString();

    public ChatBotView(ChatService chatService) {
        this.chatService = chatService;

        //Create a scrolling MessageList
        messageList = new MessageList();
        messageList.setMarkdown(true);
        var scroller = new Scroller(messageList);
        scroller.setHeightFull();
        getContent().addAndExpand(scroller);

        //create a MessageInput and set a submit-listener
        var messageInput = new MessageInput();
        messageInput.addSubmitListener(this::onSubmit);
        messageInput.setWidthFull();

        getContent().add(messageInput);
    }

    private void onSubmit(MessageInput.SubmitEvent submitEvent) {
        //create and handle a prompt message
        var promptMessage = new MessageListItem(submitEvent.getValue(), Instant.now(), "You");
        promptMessage.setUserColorIndex(0);
        promptMessage.addClassNames("current-user");
        messageList.addItem(promptMessage);

        //create and handle the response message
        var responseMessage = new MessageListItem("", Instant.now(), "AI");
        responseMessage.setUserColorIndex(1);
        messageList.addItem(responseMessage);

        //append a response message to the existing UI
        var userPrompt = submitEvent.getValue();
        var uiOptional = submitEvent.getSource().getUI();
        uiOptional.ifPresent(ui -> chatService.chatStream(userPrompt, chatId)
                .subscribe(token ->
                        ui.access(() -> {
                            responseMessage.appendText(token);
                            ((Scroller) getContent().getComponentAt(0)).scrollToBottom();
                        })));

    }
}