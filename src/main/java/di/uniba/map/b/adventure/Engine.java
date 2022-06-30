/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure;

import di.uniba.map.b.adventure.parser.Parser;
import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.CommandType;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.swing.ImageIcon;
import java.awt.event.WindowEvent;

/**
 * ATTENZIONE: l'Engine è molto spartano, in realtà demanda la logica alla
 * classe che implementa GameDescription e si occupa di gestire I/O sul
 * terminale.
 *
 * @author pierpaolo
 */
public class Engine {

    private final GameDescription game;

    private Parser parser;

    private final GameJFrame gui;

    public Engine(GameDescription game, GameJFrame gui) {
        this.game = game;
        this.gui = gui;
        try {
            this.game.init();
        } catch (Exception ex) {
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

        gui.appendTextEdtOutput(game.getCurrentRoom().getDescription());
    }

    public void commandPerformed(String command) {
        ParserOutput p = parser.parse(command, game.getCommands(),
                game.getCurrentRoom().getObjects(),
                game.getInventory());
        if (p == null || p.getCommand() == null) {
            gui.appendTextEdtOutput("Non capisco quello che mi vuoi dire.");
        } else if (p.getCommand() != null && p.getCommand().getType() == CommandType.END) {
            gui.dispatchEvent(new WindowEvent(gui, WindowEvent.WINDOW_CLOSING));
        } else {
            game.nextMove(p, gui);
        }
    }
}
