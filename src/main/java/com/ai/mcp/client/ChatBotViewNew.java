package com.ai.mcp.client;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;

@PageTitle("New Chat Bot")
@Menu(title = "Chatbot", order = 10)
@Route("/chat-bot-new")
@RouteAlias("chat-bot-new")
public class ChatBotViewNew extends Composite<SplitLayout> {

    private final ChatService chatService;
    private final McpClientHandlers mcpClientHandlers;
    private final MessageList messageList;
    private final String chatId = UUID.randomUUID().toString();
    private final TextArea textArea ;
    public ChatBotViewNew(ChatService chatService, McpClientHandlers mcpClientHandlers) {
        this.chatService = chatService;
        this.mcpClientHandlers = mcpClientHandlers;


        //Create a scrolling MessageList
        messageList = new MessageList();
        messageList.setMarkdown(true);
        var scroller = new Scroller(messageList);
        scroller.setHeightFull();
//        getContent().addToPrimary(scroller);

        //create a MessageInput and set a submit-listener
        var messageInput = new MessageInput();
        messageInput.addSubmitListener(this::onSubmit);
        messageInput.setWidthFull();

        getContent().addToPrimary(scroller, messageInput);

        textArea = new TextArea();
        textArea.setMinRows(4);
        textArea.setMaxRows(8);
        textArea.setReadOnly(true);

        getContent().addToSecondary(textArea);
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
        uiOptional.ifPresent(ui -> {

            chatService.chatStream(userPrompt, chatId)
                .subscribe(token ->
                        ui.access(() -> {
                            responseMessage.appendText(token);
//                            ((Scroller) getContent().getPrimaryComponent().getChildren()).scrollToBottom();
                        }));
            Flux.fromIterable(mcpClientHandlers.getMessages()).subscribe(
                    message -> {
                        ui.access(() -> {textArea.setValue(textArea.getValue() + "\n" +message);});
                    }
            );

        });

    }
}