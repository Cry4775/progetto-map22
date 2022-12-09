package engine.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import component.entity.AbstractEntity;
import component.entity.container.AbstractContainer;
import component.entity.interfaces.IFillable;
import engine.Inventory;
import engine.command.Command;

/**
 * <p>
 * The game's parser.
 * </p>
 * <p>
 * Only recognizes simple phrases of the following format:
 * 
 * <pre>
 * {@code <action> <object> <object>}
 * </pre>
 * </p>
 */
public class Parser {
    private final Set<String> stopwords;
    private final List<Command> commands;

    public Parser(Set<String> stopwords, List<Command> commands) {
        this.stopwords = stopwords;
        this.commands = commands;
    }

    /**
     * Parses the given command based on the defined rules and matches with the game objects.
     * 
     * @param command the command took from user input.
     * @param objects the current room list of objects.
     * @param inventory the inventory.
     * @return an object specifying the parsed command, room object and inventory object if any.
     */
    public Result parse(String command, List<AbstractEntity> objects, Inventory inventory) {
        List<String> tokens = parseString(command == null ? "" : command, stopwords);

        if (!tokens.isEmpty()) {
            Command cmd = parseCommand(tokens.get(0));
            if (cmd != null) {
                if (tokens.size() > 1) {
                    AbstractEntity objRoom = parseObject(tokens.get(1), objects);
                    AbstractEntity objInv = null;
                    if (objRoom == null && tokens.size() > 2) {
                        objRoom = parseObject(tokens.get(2), objects);
                    }
                    if (objRoom == null) {
                        objInv = parseObject(tokens.get(1), inventory);
                    }
                    if (objInv == null && tokens.size() > 2) {
                        objInv = parseObject(tokens.get(2), inventory);
                        if (objInv == null) {
                            objInv = parseObject(tokens.get(1), inventory);
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

    /**
     * Parses the given string and deletes the stopwords.
     * 
     * @param string the string to parse.
     * @param stopwords the stopwords to use.
     * @return the list of words without the stopwords.
     */
    private static List<String> parseString(String string, Set<String> stopwords) {
        List<String> tokens = new ArrayList<>();
        String[] split = string.toLowerCase().split("\\s+");

        if (split.length > 1) {
            for (String t : split) {
                if (!stopwords.contains(t)) {
                    tokens.add(t);
                }
            }
        } else if (split.length == 1) {
            tokens.add(split[0]);
        }
        return tokens;
    }

    /**
     * Parses the given token by searching for a matching command.
     * 
     * @param token the word representing the command.
     * @return the {@link engine.command.Command Command} object.
     */
    private Command parseCommand(String token) {
        for (Command command : commands) {
            if (command.getName().equals(token)
                    || command.getAlias() != null && command.getAlias().contains(token)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Parses the given token by searching for a matching object.
     * 
     * @param token the name of the object.
     * @param objects the list of object where the name should be found.
     * @return the object if it exists, {@code null} otherwise.
     */
    private AbstractEntity parseObject(String token, List<AbstractEntity> objects) {
        if (objects != null) {
            for (AbstractEntity obj : objects) {
                if (equalsObjectName(token, obj)) {
                    return obj;
                } else if (obj instanceof AbstractContainer) {
                    AbstractContainer container = (AbstractContainer) obj;

                    if (container.isContentRevealed()) {
                        for (AbstractEntity _obj : AbstractContainer.getAllObjectsInside(container)) {
                            if (equalsObjectName(token, _obj)) {
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
                        if (equalsObjectName(token, fillable.getEligibleItem()))
                            return fillable.getEligibleItem();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Parses the given token by searching for a matching object.
     * 
     * @param token the name of the object.
     * @param inventory the inventory where the name should be found.
     * @return the object if it exists, {@code null} otherwise.
     */
    private AbstractEntity parseObject(String token, Inventory inventory) {
        return parseObject(token, inventory.getObjects());
    }

    /**
     * Compares the given token to the given object names, ignoring case considerations.
     * 
     * @param token the name of the object.
     * @param obj the object to check.
     * @return {@code true} if it matches, {@code false} otherwise.
     */
    private boolean equalsObjectName(String token, AbstractEntity obj) {
        if (obj != null && token != null) {
            Stream<String> aliases = obj.getAlias() != null ? obj.getAlias().stream() : Stream.empty();
            if (token.equalsIgnoreCase(obj.getName()) || aliases.anyMatch(alias -> alias.equalsIgnoreCase(token))) {
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
