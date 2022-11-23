package engine;

import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import component.room.CutsceneRoom;
import component.room.PlayableRoom;
import engine.database.DBManager;
import engine.parser.Parser;
import engine.parser.ParserResult;
import gui.GUIManager;
import gui.MainFrame;
import rest.WeatherFetcher;
import sound.SoundManager;
import sound.SoundManager.Mode;
import utility.Utils;

public class Engine {
    private static Parser parser;
    private static MainFrame gui;
    private static GameManager gameManager;
    private static boolean firstExecution = true;

    private Engine() {}

    public static void initialize(MainFrame gui) {
        try {
            Engine.gui = gui;

            AtomicBoolean crashed = new AtomicBoolean(false);
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    e.printStackTrace();
                    GUIManager.showFatalError("Thread " + t.getName() + ": " + e.getMessage());
                    crashed.set(true);
                }
            });

            DBManager.createDB();
            gameManager = GameManager.getInstance();
            gameManager.initialize();

            Set<String> stopwords = Utils.loadFileListInSet(new File("resources/stopwords"));
            parser = new Parser(stopwords, gameManager.getCommands());

            if (!crashed.get())
                commandPerformed(null);
        } catch (Exception e) {
            e.printStackTrace();
            GUIManager.showFatalError(e.getMessage());
        }
    }

    public static void commandPerformed(String command) {

        if (firstExecution) {
            firstExecution = false;
            GUIManager.createWeatherProgressMonitor();
        }

        new Thread("Engine") {
            public void run() {
                if (!WeatherFetcher.isRaining()) {
                    SoundManager.playWav("resources/sound/ambience.wav", Mode.MUSIC);
                } else {
                    SoundManager.playWav("resources/sound/rainAmbience.wav", Mode.MUSIC);
                }

                if (gameManager.getCurrentRoom() instanceof PlayableRoom) {
                    PlayableRoom currentRoom = (PlayableRoom) gameManager.getCurrentRoom();
                    ParserResult p = parser.parse(command, currentRoom.getObjects(), gameManager.getInventory());

                    if (command == null) {
                        GUIManager.updateRoomInformations(currentRoom, gameManager.getPreviousRoom());
                        return;
                    }

                    if (p == null || p.getCommand() == null) {
                        GUIManager.appendOutput("Non capisco quello che mi vuoi dire.");
                        GUIManager.printOutput();
                    } else {
                        gameManager.nextMove(p);
                    }
                } else if (gameManager.getCurrentRoom() instanceof CutsceneRoom) {
                    CutsceneRoom currentRoom = (CutsceneRoom) gameManager.getCurrentRoom();

                    GUIManager.updateRoomInformations(currentRoom, gameManager.getPreviousRoom());
                    GUIManager.waitUntilEnterIsPressed();

                    if (!currentRoom.isFinalRoom()) {
                        gameManager.nextRoom();
                    } else {
                        gui.dispatchEvent(new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));
                    }
                }

                commandPerformed(null);
            }
        }.start();
    }
}
