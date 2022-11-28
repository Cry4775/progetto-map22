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
import engine.Inventory;
import engine.command.Command;
import utility.Utils;

public class Parser {

    private final Set<String> stopwords;
    private final List<Command> commands;

    public Parser(Set<String> stopwords, List<Command> commands) {
        this.stopwords = stopwords;
        this.commands = commands;
    }

    /*
     * ATTENZIONE: il parser è implementato in modo abbastanza independete dalla lingua, ma
     * riconosce solo frasi semplici del tipo <azione> <oggetto> <oggetto>.
     * Eventuali articoli o preposizioni vengono semplicemente rimossi.
     */
    public Result parse(String command, List<AbstractEntity> objects, Inventory inventory) {
        List<String> tokens = Utils.parseString(command == null ? "" : command, stopwords);

        if (!tokens.isEmpty()) {
            Command cmd = checkForCommand(tokens.get(0));
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
                        return new Result(cmd, objRoom, objInv);
                    } else if (objRoom != null) {
                        return new Result(cmd, objRoom);
                    } else if (objInv != null) {
                        return new Result(cmd, null, objInv);
                    } else {
                        return new Result(cmd);
                    }
                } else {
                    return new Result(cmd);
                }
            } else {
                return new Result();
            }
        } else {
            return null;
        }
    }

    private Command checkForCommand(String token) {
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
                if (tokenEqualsObjName(token, obj)) {
                    return obj;
                } else if (obj instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) obj;

                    if (container.isContentRevealed()) {
                        for (AbstractEntity _obj : AbstractContainer.getAllObjectsInside(container)) {
                            if (tokenEqualsObjName(token, _obj)) {
                                // Check if the object is inside another container and if that's opened
                                if (_obj.getParent() instanceof AbstractContainer) {
                                    AbstractContainer parentContainer = (AbstractContainer) _obj.getParent();

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
                        if (tokenEqualsObjName(token, fillable.getEligibleItem()))
                            return fillable.getEligibleItem();
                    }
                }
            }
        }
        return null;
    }

    private AbstractEntity checkForObject(String token, Inventory inventory) {
        return checkForObject(token, inventory.getObjects());
    }

    private boolean tokenEqualsObjName(String token, AbstractEntity obj) {
        if (obj != null && token != null) {
            if (token.equals(obj.getName()) || (obj.getAlias() != null && obj.getAlias().contains(token))) {
                return true;
            }

            return false;
        } else {
            throw new IllegalArgumentException("Invalid argument, token and obj can't be null.");
        }

    }

    public class Result {
        private Command command;
        private AbstractEntity roomObject;
        private AbstractEntity invObject;

        public Result() {}

        public Result(Command command) {
            this.command = command;
        }

        public Result(Command command, AbstractEntity roomObject) {
            this.command = command;
            this.roomObject = roomObject;
        }

        public Result(Command command, AbstractEntity roomObject, AbstractEntity invObject) {
            this.command = command;
            this.roomObject = roomObject;
            this.invObject = invObject;
        }

        public Command getCommand() {
            return command;
        }

        public AbstractEntity getObject() {
            if (roomObject != null) {
                return roomObject;
            }

            if (invObject != null) {
                return invObject;
            }

            return null;
        }

        public AbstractEntity getRoomObject() {
            return roomObject;
        }

        public AbstractEntity getInvObject() {
            return invObject;
        }
    }

}
