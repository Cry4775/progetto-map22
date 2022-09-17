/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package di.uniba.map.b.adventure;

import java.awt.Image;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import javax.swing.ImageIcon;
import di.uniba.map.b.adventure.parser.Parser;
import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.CommandType;
import di.uniba.map.b.adventure.type.CutsceneRoom;
import di.uniba.map.b.adventure.type.PlayableRoom;

public class Engine {

    private final GameDescription game;

    private Parser parser;

    private final GameJFrame gui;

    public Engine(GameDescription game, GameJFrame gui) {
        this.game = game;
        this.gui = gui;
        try {
            this.game.init();
        } catch (IOException ex) {
            System.err.println(ex);
            // TODO GUI FATAL ERROR
        }
        try {
            Set<String> stopwords = Utils.loadFileListInSet(new File("./resources/stopwords"));
            parser = new Parser(stopwords);
        } catch (IOException ex) {
            System.err.println(ex);
            // TODO GUI FATAL ERROR
        }
    }

    public void execute() {
        gui.setTitle("The Haunted House - 2021-22");
        gui.getLblRoomName().setText(game.getCurrentRoom().getName());

        Image roomImg = new ImageIcon(game.getCurrentRoom().getImgPath()).getImage()
                .getScaledInstance(581, 300, Image.SCALE_SMOOTH);
        gui.getLblRoomImage().setIcon(new ImageIcon(roomImg));

        game.setCompassLabels(gui);

        gui.appendTextEdtOutput(game.getCurrentRoom().getDescription(), false);

        if (game.getCurrentRoom() instanceof CutsceneRoom) {
            gui.waitForEnterKey();
        }
    }

    public void commandPerformed(String command) {
        if (game.getCurrentRoom() instanceof PlayableRoom) {
            PlayableRoom currentRoom = (PlayableRoom) game.getCurrentRoom();
            ParserOutput p = parser.parse(command, game.getCommands(), currentRoom.getObjects(),
                    game.getInventory());
            if (p == null || p.getCommand() == null) {
                gui.appendTextEdtOutput("Non capisco quello che mi vuoi dire.", false);
            } else if (p.getCommand() != null && p.getCommand().getType() == CommandType.END) {
                gui.dispatchEvent(new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));
            } else {
                game.nextMove(p, gui);
            }
        } else if (game.getCurrentRoom() instanceof CutsceneRoom) {
            CutsceneRoom currentRoom = (CutsceneRoom) game.getCurrentRoom();

            if (!currentRoom.isFinalRoom()) {
                if (currentRoom.getNextRoom() != null) {
                    game.setCurrentRoom(currentRoom.getNextRoom());
                    gui.appendTextEdtOutput(game.getCurrentRoom().getDescription(), false);
                } else {
                    // TODO errore
                }
            } else {
                gui.dispatchEvent(new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));
            }
        }

        if (game.getCurrentRoom() instanceof PlayableRoom) {
            PlayableRoom currentRoom = (PlayableRoom) game.getCurrentRoom();

            if (currentRoom.isCurrentlyDark()) {
                updateGUI("Buio", "./resources/img/buio.jpg");
            } else {
                updateGUI();
            }
        } else {
            updateGUI();
        }

        if (game.getCurrentRoom() instanceof CutsceneRoom) {
            gui.waitForEnterKey();
        }
    }

    public void updateGUI() {
        gui.getLblRoomName().setText(game.getCurrentRoom().getName());
        Image roomImg = new ImageIcon(game.getCurrentRoom().getImgPath()).getImage()
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
