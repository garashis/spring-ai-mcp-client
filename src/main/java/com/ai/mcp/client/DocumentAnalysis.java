package com.ai.mcp.client;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.tika.exception.WriteLimitReachedException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;

import java.io.InputStream;

@Menu(title = "Document Analysis", order = 3)
@Route("document-analysis")
public class DocumentAnalysis extends VerticalLayout {

    private final ChatClient chatClient;

    public static final String SUMMARIZATION_SYSTEM_MESSAGE = """
        Summarize the following text into a concise paragraph that captures the main points and essential details without losing important information. 
        The summary should be as short as possible while remaining clear and informative.
        Use bullet points or numbered lists to organize the information if it helps to clarify the meaning. 
        Focus on the key facts, events, and conclusions. 
        Avoid including minor details or examples unless they are crucial for understanding the main ideas.
        """;

    private static final String ACTION_POINTS_SYSTEM_MESSAGE = """
        Please provide a list of action points based on the following text.
        The action points should be clear, specific, actionable, and have a person assigned to them.
        Use bullet points or numbered lists to organize the information if it helps to clarify the meaning.
        Include only the most important action items and avoid listing tasks that are already completed or not relevant.
        """;

    private final Div output = new Div();

    public DocumentAnalysis(ChatClient.Builder builder) {
        chatClient = builder.build();

        buildView();
    }

    private void buildView() {
        var modeSelector = new RadioButtonGroup<String>();

        var heading = new H1("Document Analysis Bot");
        heading.addClassName(LumoUtility.FontSize.XLARGE);

        var header = new HorizontalLayout(heading, getUpload(modeSelector));
        header.setAlignItems(Alignment.BASELINE);
        header.addClassName(LumoUtility.FlexWrap.WRAP);


        modeSelector.setItems("Summarize", "Identify action points");
        modeSelector.setValue("Summarize");
        modeSelector.addValueChangeListener(e -> output.removeAll());

        add(header, modeSelector, output);

    }

    private @NonNull Upload getUpload(RadioButtonGroup<String> modeSelector) {
        InMemoryUploadHandler inMemoryHandler = UploadHandler.inMemory(
                (metadata, data) -> {
                    // Get other information about the file.
                    String fileName = metadata.fileName();
                    String mimeType = metadata.contentType();
                    long contentLength = metadata.contentLength();

                    // Do something with the file data...
                    parseFile(data, modeSelector.getValue());
                });
        return new Upload(inMemoryHandler);
    }

    private void parseFile(byte[] data, String mode) {
        var parser = new AutoDetectParser();
        var handler = new BodyContentHandler();

        try (InputStream stream = TikaInputStream.get(data)) {
            parser.parse(stream, handler, new Metadata());
            analyzeFile(handler.toString(), mode);
        } catch (WriteLimitReachedException ex) {
            Notification.show(ex.getMessage());
            analyzeFile(handler.toString(), mode);
        } catch (Exception ex) {
            output.add(new H2("Parsing Data failed: " + ex.getMessage()));
            throw new RuntimeException(ex);
        }
    }

    private void analyzeFile(String content, String mode) {
        output.removeAll();
        var response = new Markdown("");
        output.add(response);


        var ui = UI.getCurrent();
        chatClient.prompt()
            .system(mode.equals("Summarize") ? SUMMARIZATION_SYSTEM_MESSAGE : ACTION_POINTS_SYSTEM_MESSAGE)
            .user("Text to summarize: " + content)
            .stream()
            .content()
            .subscribe(token -> ui.access(() -> response.appendContent(token)));

    }
}
