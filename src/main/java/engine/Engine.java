/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package engine;

import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import component.room.CutsceneRoom;
import component.room.PlayableRoom;
import engine.database.DBManager;
import engine.parser.Parser;
import engine.parser.ParserOutput;
import gui.GUIManager;
import gui.MainFrame;
import rest.WeatherFetcher;
import sound.SoundManager;
import sound.SoundManager.Mode;
import utility.Utils;

public class Engine extends Thread {

    private final GameManager game;

    private Parser parser;

    private final MainFrame gui;

    public Engine(GameManager game, MainFrame gui) {
        this.game = game;
        this.gui = gui;
        try {
            AtomicBoolean crashed = new AtomicBoolean(false);
            Set<String> stopwords = Utils.loadFileListInSet(new File("resources/stopwords"));
            parser = new Parser(stopwords);
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    e.printStackTrace();
                    MainFrame.showFatalError(gui, e.getMessage());
                    crashed.set(true);
                }
            });

            DBManager.createDB();

            this.game.init();

            if (!crashed.get())
                commandPerformed(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            gui.setVisible(false);
            MainFrame.showFatalError(gui, ex.getMessage());
        }
    }

    public void commandPerformed(String command) {
        new Thread("Engine") {
            public void run() {
                if (!WeatherFetcher.isRaining()) {
                    SoundManager.playWav("resources/sound/ambience.wav", Mode.MUSIC);
                } else {
                    SoundManager.playWav("resources/sound/rainAmbience.wav", Mode.MUSIC);
                }

                if (GameManager.getCurrentRoom() instanceof PlayableRoom) {
                    ParserOutput p = parser.parse(command);
                    if (command == null) {
                    } else if (p == null || p.getCommand() == null) {
                        GUIManager.appendOutput("Non capisco quello che mi vuoi dire.");
                        GUIManager.printOutput();
                    } else {
                        game.nextMove(p, gui);
                    }
                } else if (GameManager.getCurrentRoom() instanceof CutsceneRoom) {
                    CutsceneRoom currentRoom = (CutsceneRoom) GameManager.getCurrentRoom();

                    if (command == null) {
                        GUIManager.updateRoomInformations();
                    }

                    GUIManager.waitUntilEnterIsPressed();

                    if (!currentRoom.isFinalRoom()) {
                        if (currentRoom.getNextRoom() != null) {
                            GameManager.setCurrentRoom(currentRoom.getNextRoom());
                        } else {
                            throw new Error(
                                    "Couldn't find the next room of " + currentRoom.getName()
                                            + " (" + currentRoom.getId()
                                            + "). Check the JSON file for correct room IDs.");
                        }
                    } else {
                        gui.dispatchEvent(new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));
                    }
                }

                // After the move
                if (GameManager.getCurrentRoom() instanceof PlayableRoom) {
                    PlayableRoom currentRoom = (PlayableRoom) GameManager.getCurrentRoom();
                    GUIManager.updateRoomInformations(currentRoom.isCurrentlyDark() ? true : false);
                } else {
                    commandPerformed(null);
                }
            }
        }.start();

    }
}
