package engine.parser;

import component.entity.AbstractEntity;
import engine.command.Command;

public class ParserResult {
    private Command command;
    private AbstractEntity roomObject;
    private AbstractEntity invObject;

    public ParserResult() {}

    public ParserResult(Command command) {
        this.command = command;
    }

    public ParserResult(Command command, AbstractEntity roomObject) {
        this.command = command;
        this.roomObject = roomObject;
    }

    public ParserResult(Command command, AbstractEntity roomObject, AbstractEntity invObject) {
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
