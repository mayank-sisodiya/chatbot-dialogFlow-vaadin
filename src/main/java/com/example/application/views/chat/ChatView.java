package com.example.application.views.chat;

import com.example.application.dto.MessageList;
import com.example.application.service.ChatService;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.communication.PushMode;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;

@Route(value = "chat", layout = MainView.class)
@PageTitle("DiaglowFlow Chatbot Demo")
@CssImport("styles/views/chat/chat-view.css")
public class ChatView extends VerticalLayout {

    private final ApplicationContext applicationContext;
    private final ScheduledExecutorService executorService;
    private final UI ui;
    private final MessageList messageList = new MessageList();
    private final TextField message = new TextField();
    private String userId;
    private final ChatService chatService;

    public ChatView(ApplicationContext applicationContext, ScheduledExecutorService executorService, ChatService chatService) {
        this.applicationContext = applicationContext;
        this.executorService = executorService;
        this.chatService = chatService;
        ui = UI.getCurrent();

        message.setPlaceholder("Enter a message...");
        message.setSizeFull();

        Button send = new Button(VaadinIcon.ENTER.create(), event -> sendMessage());
        send.addClickShortcut(Key.ENTER);

        HorizontalLayout inputLayout = new HorizontalLayout(message, send);
        inputLayout.addClassName("inputLayout");

        add(messageList, inputLayout);
        expand(messageList);
        setSizeFull();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        ui.getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
    }

    private void sendMessage() {
        String text = message.getValue();
        if (!text.trim().isEmpty()) {
            messageList.addMessage("You("+userId+")", text, true);
            message.clear();

            executorService.schedule(() -> {
                String answer = chatService.getReply(text);
                ui.access(() -> messageList.addMessage("Bot", answer, false));
            }, new Random().ints(1000, 3000).findFirst().getAsInt(), TimeUnit.MILLISECONDS);
        }
    }

}
