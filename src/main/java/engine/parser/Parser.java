/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package engine.parser;

import java.util.List;
import java.util.Set;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IFillable;
import engine.command.Command;
import utility.Utils;

/**
 * @author pierpaolo
 */
public class Parser {

    private final Set<String> stopwords;

    public Parser(Set<String> stopwords) {
        this.stopwords = stopwords;
    }

    private Command checkForCommand(String token, List<Command> commands) {
        for (Command command : commands) {
            if (command.getName().equals(token)
                    || command.getAlias() != null && command.getAlias().contains(token)) {
                return command;
            }
        }
        return null;
    }

    private AbstractEntity checkForObject(String token, List<AbstractEntity> objects) {
        if (objects != null) {
            for (int i = 0; i < objects.size(); i++) {
                if ((objects.get(i).getName() != null
                        && objects.get(i).getName().equals(token))
                        || (objects.get(i).getAlias() != null
                                && objects.get(i).getAlias().contains(token))) {
                    return objects.get(i);
                } else if (objects.get(i) instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) objects.get(i);

                    if (container.isContentRevealed()) {
                        for (AbstractEntity obj : container.getList()) {
                            if (obj.getName().equals(token)
                                    || (obj.getAlias() != null && obj.getAlias().contains(token))) {
                                return obj;
                            }
                        }
                    }
                } else if (objects.get(i) instanceof IFillable) {
                    IFillable fillable = (IFillable) objects.get(i);

                    if (fillable.isFilled()) {
                        if (fillable.getEligibleItem().getName().equals(token)
                                || fillable.getEligibleItem().getAlias() != null
                                        && fillable.getEligibleItem().getAlias()
                                                .contains(token)) {
                            return fillable.getEligibleItem();
                        }
                    }
                }
            }
        }
        return null;
    }

    /*
     * ATTENZIONE: il parser è implementato in modo abbastanza independete dalla lingua, ma
     * riconosce solo frasi semplici del tipo <azione> <oggetto> <oggetto>.
     * Eventuali articoli o preposizioni vengono semplicemente rimossi.
     */
    public ParserOutput parse(String command, List<Command> commands, List<AbstractEntity> objects,
            List<AbstractEntity> inventory) {
        List<String> tokens = Utils.parseString(command, stopwords);
        if (!tokens.isEmpty()) {
            Command cmd = checkForCommand(tokens.get(0), commands);
            if (cmd != null) {
                if (tokens.size() > 1) {
                    AbstractEntity objRoom = checkForObject(tokens.get(1), objects);
                    AbstractEntity objInv = null;
                    if (objRoom == null && tokens.size() > 2) {
                        objRoom = checkForObject(tokens.get(2), objects);
                    }
                    if (objRoom == null) {
                        objInv = checkForObject(tokens.get(1), inventory);
                    }
                    if (objInv == null && tokens.size() > 2) {
                        objInv = checkForObject(tokens.get(2), inventory);
                        if (objInv == null) {
                            objInv = checkForObject(tokens.get(1), inventory);
                        }
                    }

                    if (objRoom != null && objInv != null) {
                        return new ParserOutput(cmd, objRoom, objInv);
                    } else if (objRoom != null) {
                        return new ParserOutput(cmd, objRoom, null);
                    } else if (objInv != null) {
                        return new ParserOutput(cmd, null, objInv);
                    } else {
                        return new ParserOutput(cmd, null, null);
                    }
                } else {
                    return new ParserOutput(cmd, null);
                }
            } else {
                return new ParserOutput(null, null);
            }
        } else {
            return null;
        }
    }

}
