package zella.examples;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.*;

public class HelloWorld {

    static ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();

    private static void runBg(Callable r) {
        ex.execute(() -> {
            try {
                r.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static Window newWindow() {
        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        Label label = new Label("Forename");

        panel.addComponent(label);
        panel.addComponent(new TextBox());

        panel.addComponent(new Label("Surname"));
        panel.addComponent(new TextBox());

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0))); // Empty space underneath labels
        panel.addComponent(new Button("Submit"));

        // Create window to hold the panel
        BasicWindow window = new BasicWindow(UUID.randomUUID().toString());
        window.setComponent(panel);

        return window;
    }

    public static void main(String[] args) throws Exception {

        // Setup terminal and screen layers
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        Window w1 = newWindow();

//        ex.scheduleAtFixedRate(() -> {
//            label.setText(UUID.randomUUID().toString());
//        }, 5, 5, TimeUnit.SECONDS);

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));

        gui.addWindow(w1);

        ex.schedule(() -> {
            gui.getGUIThread().invokeLater(() -> w1.close());
            try {
                Thread.sleep(3000);
                gui.getGUIThread().invokeLater(() -> gui.addWindow(newWindow()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 3, TimeUnit.SECONDS);

//

        while (true) {
            if (!gui.getGUIThread().processEventsAndUpdate())
                Thread.sleep(1);
        }
    }
}