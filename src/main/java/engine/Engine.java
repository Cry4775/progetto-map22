/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package engine;

import java.awt.Image;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.ImageIcon;
import component.room.CutsceneRoom;
import component.room.PlayableRoom;
import engine.command.CommandType;
import engine.database.DBManager;
import engine.parser.Parser;
import engine.parser.ParserOutput;
import gui.MainFrame;
import rest.WeatherFetcher;
import sound.SoundManager;
import sound.SoundManager.Mode;
import utility.Utils;

public class Engine {

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
                    gui.showFatalError(e.getMessage());
                    crashed.set(true);
                }
            });

            DBManager.createDB();

            this.game.init();

            if (!crashed.get())
                execute();
        } catch (Exception ex) {
            ex.printStackTrace();
            gui.setVisible(false);
            gui.showFatalError(ex.getMessage());
        }
    }

    public void execute() {
        if (!WeatherFetcher.isRaining()) {
            SoundManager.playWav("resources/sound/ambience.wav", Mode.MUSIC);
        } else {
            SoundManager.playWav("resources/sound/rainAmbience.wav", Mode.MUSIC);
        }

        gui.setTitle("The Haunted House - 2021-22");

        if (GameManager.getCurrentRoom() instanceof PlayableRoom) {
            PlayableRoom currentRoom = (PlayableRoom) GameManager.getCurrentRoom();

            if (currentRoom.isCurrentlyDark()) {
                gui.appendText("È completamente buio e non riesci a vedere niente.", false);
                updateGUI("Buio", "resources/img/buio.jpg");
            } else {
                gui.appendText(GameManager.getCurrentRoom().getDescription(), false);
                updateGUI();
            }
        } else {
            gui.appendText(GameManager.getCurrentRoom().getDescription(), false);
            updateGUI();
            gui.waitForEnterKey();
        }
    }

    public void commandPerformed(String command) {
        if (!WeatherFetcher.isRaining()) {
            SoundManager.playWav("resources/sound/ambience.wav", Mode.MUSIC);
        } else {
            SoundManager.playWav("resources/sound/rainAmbience.wav", Mode.MUSIC);
        }

        if (GameManager.getCurrentRoom() instanceof PlayableRoom) {
            PlayableRoom currentRoom = (PlayableRoom) GameManager.getCurrentRoom();
            ParserOutput p = parser.parse(command, game.getCommands(), currentRoom.getObjects(),
                    GameManager.getInventory());
            if (p == null || p.getCommand() == null) {
                gui.appendText("Non capisco quello che mi vuoi dire.", false);
            } else if (p.getCommand() != null && p.getCommand().getType() == CommandType.END) {
                gui.dispatchEvent(new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));
            } else {
                game.nextMove(p, gui);
            }
        } else if (GameManager.getCurrentRoom() instanceof CutsceneRoom) {
            CutsceneRoom currentRoom = (CutsceneRoom) GameManager.getCurrentRoom();

            if (!currentRoom.isFinalRoom()) {
                if (currentRoom.getNextRoom() != null) {
                    GameManager.setCurrentRoom(currentRoom.getNextRoom());
                    gui.appendText(GameManager.getCurrentRoom().getDescription(), false);
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

        if (GameManager.getCurrentRoom() instanceof PlayableRoom) {
            PlayableRoom currentRoom = (PlayableRoom) GameManager.getCurrentRoom();

            if (currentRoom.isCurrentlyDark()) {
                updateGUI("Buio", "resources/img/buio.jpg");
            } else {
                updateGUI();
            }
        } else {
            updateGUI();
        }

        if (GameManager.getCurrentRoom() instanceof CutsceneRoom) {
            gui.waitForEnterKey();
        }
    }

    public void updateGUI() {
        gui.getLblRoomName().setText(GameManager.getCurrentRoom().getName());
        Image roomImg = new ImageIcon(GameManager.getCurrentRoom().getImgPath()).getImage()
                .getScaledInstance(581, 300, Image.SCALE_SMOOTH);
        gui.getLblRoomImage().setIcon(new ImageIcon(roomImg));
        game.setCompassLabels(gui);
    }

    public void updateGUI(String roomName, String imageURL) {
        gui.getLblRoomName().setText(roomName);
        Image roomImg = new ImageIcon(imageURL).getImage()
                .getScaledInstance(581, 300, Image.SCALE_SMOOTH);
        gui.getLblRoomImage().setIcon(new ImageIcon(roomImg));
        game.setCompassLabels(gui);
    }
}
