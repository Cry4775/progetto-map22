/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.parser;

import component.entity.AbstractEntity;
import engine.command.Command;

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