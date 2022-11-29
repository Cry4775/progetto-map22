package gui;

import java.awt.Image;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import component.room.AbstractRoom;
import component.room.PlayableRoom;
import rest.WeatherFetcher;

public class GUIManager {
    private static MainFrame gui;

    private static String lastDescription;

    protected static void registerGUI(MainFrame mainFrame) {
        Objects.requireNonNull(mainFrame);

        gui = mainFrame;
        OutputManager.registerGUI(mainFrame);
        CompassManager.registerGUI(mainFrame);
    }

    public static void waitUntilEnterIsPressed() {
        OutputManager.waitForEnterKey();
    }

    public static void appendOutput(StringBuilder stringBuilder) {
        OutputManager.append(stringBuilder.toString());
    }

    public static void appendOutput(String string) {
        OutputManager.append(string);
    }

    public static void printOutput() {
        OutputManager.printAsOutput();
    }

    protected static void printInput(String text) {
        OutputManager.printAsInput(text);
    }

    public static boolean isOutputEmpty() {
        return OutputManager.isOutputEmpty();
    }

    public static void updateRoomInformations(AbstractRoom currentRoom, AbstractRoom previousRoom) {
        if (currentRoom instanceof PlayableRoom) {
            PlayableRoom pRoom = (PlayableRoom) currentRoom;
            updateRoomInformations(currentRoom, previousRoom, pRoom.isDark() ? true : false);
        } else {
            updateRoomInformations(currentRoom, previousRoom, false);
        }
    }

    private static void updateRoomInformations(AbstractRoom currentRoom, AbstractRoom previousRoom, boolean dark) {
        // TODO é hardcoded, bisogna permettere la personalizzazione
        String description;
        if (dark) {
            CompassManager.updateCompass(currentRoom, previousRoom);
            updateRoomLabels("Buio", "resources/img/buio.jpg");
            description = "È completamente buio e non riesci a vedere niente.";
        } else {
            CompassManager.updateCompass(currentRoom, previousRoom);
            updateRoomLabels(currentRoom.getName(), currentRoom.getImgPath());
            description = currentRoom.getDescription();
        }

        if (!description.equals(lastDescription)) {
            lastDescription = description;
            appendOutput(description);
        }

        printOutput();
    }

    private static void updateRoomLabels(String roomName, String imageURL) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui.getLblRoomName().setText(roomName);
                Image roomImg = new ImageIcon(imageURL).getImage().getScaledInstance(581, 300, Image.SCALE_SMOOTH);
                gui.getLblRoomImage().setIcon(new ImageIcon(roomImg));
            }
        });
    }

    public static void showFatalError(String message) {
        gui.createFatalErrorDialog(message);
    }

    public static int showLoadingConfirmation() {
        AtomicInteger chosenOption = new AtomicInteger();

        try {
            Runnable runnable = new Runnable() {
                public void run() {
                    chosenOption.set(JOptionPane.showConfirmDialog(null,
                            "An existing savegame has been found. Do you wish to load it?",
                            "Loading savegame",
                            JOptionPane.YES_NO_OPTION));
                }
            };

            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeAndWait(runnable);
            } else {
                runnable.run();
            }
        } catch (InvocationTargetException | InterruptedException e) {
            throw new Error(e);
        }

        return chosenOption.get();

    }

    public static int getCurrentActionsCounterValue() {
        String[] strings = gui.getLblActions().getText().split(".*: ");
        for (String string : strings) {
            if (string.matches("[0-9]+")) {
                return Integer.parseInt(string);
            }
        }

        return 0;
    }

    public static void setCurrentActionsCounterValue(int value) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui.getLblActions().setText("Azioni: " + Integer.toString(value));
            }
        });
    }

    public static void increaseActionsCounter() {
        int currentValue = getCurrentActionsCounterValue();
        setCurrentActionsCounterValue(currentValue + 1);
    }

    public static void createWeatherProgressMonitor() {
        ProgressMonitor monitor = new ProgressMonitor(gui, "Processing...", "", 0, 100);
        monitor.setProgress(0);

        AtomicInteger progress = new AtomicInteger();

        new SwingWorker<Void, Boolean>() {
            private void cycle(int sleepTime, int maxPercentage, WeatherFetcher.State currentState)
                    throws InterruptedException {
                while (WeatherFetcher.getState().equals(currentState)) {
                    if (progress.get() == 0) {
                        for (int i = 0; i < maxPercentage; i++) {
                            if (monitor.isCanceled())
                                gui.dispatchEvent(new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));

                            Thread.sleep(sleepTime);
                            publish(true);
                            progress.incrementAndGet();

                            if (!WeatherFetcher.getState().equals(currentState)) {
                                return;
                            }
                        }
                    } else {
                        if (progress.get() < maxPercentage) {
                            int maxIt = maxPercentage - progress.get();
                            for (int i = 0; i < maxIt; i++) {
                                if (monitor.isCanceled())
                                    gui.dispatchEvent(new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));

                                Thread.sleep(sleepTime);
                                publish(true);
                                progress.incrementAndGet();

                                if (!WeatherFetcher.getState().equals(currentState)) {
                                    return;
                                }
                            }
                        }
                    }
                    if (progress.get() >= 100)
                        return;

                    if (monitor.isCanceled())
                        gui.dispatchEvent(new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));
                }
            }

            @Override
            protected Void doInBackground() throws Exception {
                cycle(2, 10, WeatherFetcher.State.FETCHING_IP);
                cycle(2, 30, WeatherFetcher.State.FETCHING_LOCATION_KEY);
                cycle(5, 90, WeatherFetcher.State.FETCHING_WEATHER);
                cycle(5, 100, WeatherFetcher.State.DONE);

                return null;
            }

            @Override
            protected void process(List<Boolean> chunks) {
                monitor.setProgress(progress.get());
            }

            @Override
            protected void done() {
                gui.setVisible(true);
            }

        }.execute();
    }
}
