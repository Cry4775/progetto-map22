/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.parser;

import di.uniba.map.b.adventure.component.entity.AbstractEntity;
import di.uniba.map.b.adventure.engine.command.Command;

/**
 * @author pierpaolo
 */
public class ParserOutput {

    private Command command;

    private AbstractEntity object;

    private AbstractEntity invObject;

    public ParserOutput(Command command, AbstractEntity object) {
        this.command = command;
        this.object = object;
    }

    public ParserOutput(Command command, AbstractEntity object, AbstractEntity invObejct) {
        this.command = command;
        this.object = object;
        this.invObject = invObejct;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public AbstractEntity getObject() {
        return object;
    }

    public void setObject(AbstractEntity object) {
        this.object = object;
    }

    public AbstractEntity getInvObject() {
        return invObject;
    }

    public void setInvObject(AbstractEntity invObject) {
        this.invObject = invObject;
    }

}
