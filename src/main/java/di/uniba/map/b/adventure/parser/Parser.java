/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.parser;

import di.uniba.map.b.adventure.Utils;
import di.uniba.map.b.adventure.type.AdvItemContainer;
import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.Command;
import java.util.List;
import java.util.Set;

/**
 *
 * @author pierpaolo
 */
public class Parser {

    private final Set<String> stopwords;

    public Parser(Set<String> stopwords) {
        this.stopwords = stopwords;
    }

    private int checkForCommand(String token, List<Command> commands) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).getName().equals(token)
                    || (commands.get(i).getAlias() != null && commands.get(i).getAlias().contains(token))) {
                return i;
            }
        }
        return -1;
    }

    private AdvObject checkForObject(String token, List<AdvObject> objects) {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).getName().equals(token)
                    || (objects.get(i).getAlias() != null && objects.get(i).getAlias().contains(token))) {
                return objects.get(i);
            } else if (objects.get(i) instanceof AdvItemContainer) {
                AdvItemContainer container = (AdvItemContainer) objects.get(i);

                if (container.isOpen()) {
                    for (AdvObject obj : container.getList()) {
                        if (obj.getName().equals(token)
                                || (obj.getAlias() != null && obj.getAlias().contains(token))) {
                            return obj;
                        }
                    }
                }
            }
        }
        return null;
    }

    /*
     * ATTENZIONE: il parser è implementato in modo abbastanza independete dalla
     * lingua, ma riconosce solo
     * frasi semplici del tipo <azione> <oggetto> <oggetto>. Eventuali articoli o
     * preposizioni vengono semplicemente
     * rimossi.
     */
    public ParserOutput parse(String command, List<Command> commands, List<AdvObject> objects,
            List<AdvObject> inventory) {
        List<String> tokens = Utils.parseString(command, stopwords);
        if (!tokens.isEmpty()) {
            int ic = checkForCommand(tokens.get(0), commands);
            if (ic > -1) {
                if (tokens.size() > 1) {
                    AdvObject objRoom = checkForObject(tokens.get(1), objects);
                    AdvObject objInv = null;
                    if (objRoom == null && tokens.size() > 2) {
                        objRoom = checkForObject(tokens.get(2), objects);
                    }
                    if (objRoom == null) {
                        objInv = checkForObject(tokens.get(1), inventory);
                        if (objInv == null && tokens.size() > 2) {
                            objInv = checkForObject(tokens.get(2), inventory);
                        }
                    }
                    if (objRoom != null && objInv != null) {
                        return new ParserOutput(commands.get(ic), objRoom, objInv);
                    } else if (objRoom != null) {
                        return new ParserOutput(commands.get(ic), objRoom, null);
                    } else if (objInv != null) {
                        return new ParserOutput(commands.get(ic), null, objInv);
                    } else {
                        return new ParserOutput(commands.get(ic), null, null);
                    }
                } else {
                    return new ParserOutput(commands.get(ic), null);
                }
            } else {
                return new ParserOutput(null, null);
            }
        } else {
            return null;
        }
    }

}
