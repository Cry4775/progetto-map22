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
import component.room.PlayableRoom;
import engine.GameManager;
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
            for (AbstractEntity obj : objects) {
                if ((obj.getName() != null && obj.getName().equals(token))
                        || (obj.getAlias() != null && obj.getAlias().contains(token))) {
                    return obj;
                } else if (obj instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) obj;

                    if (container.isContentRevealed()) {
                        for (AbstractEntity _obj : AbstractContainer
                                .getAllObjectsInside(container)) {
                            if (_obj.getName().equals(token)
                                    || (_obj.getAlias() != null
                                            && _obj.getAlias().contains(token))) {
                                if (_obj.getParent() instanceof AbstractContainer) {
                                    AbstractContainer parentContainer =
                                            (AbstractContainer) _obj.getParent();

                                    if (parentContainer.isContentRevealed()) {
                                        return _obj;
                                    }
                                }

                            }
                        }
                    }
                } else if (obj instanceof IFillable) {
                    IFillable fillable = (IFillable) obj;

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
    public ParserOutput parse(String command) {
        List<AbstractEntity> objects = ((PlayableRoom) GameManager.getInstance().getCurrentRoom()).getObjects();
        List<AbstractEntity> inventory = GameManager.getInstance().getInventory();
        List<String> tokens = Utils.parseString(command == null ? "" : command, stopwords);

        if (!tokens.isEmpty()) {
            Command cmd = checkForCommand(tokens.get(0), GameManager.getInstance().getCommands());
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
