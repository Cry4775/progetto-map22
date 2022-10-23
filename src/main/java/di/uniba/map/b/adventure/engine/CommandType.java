/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose
 * Tools | Templates and open the template in the editor.
 */
package di.uniba.map.b.adventure.engine;

/**
 * @author pierpaolo
 */
public enum CommandType {
    END, INVENTORY, NORTH, NORTH_EAST, NORTH_WEST,
    SOUTH, SOUTH_EAST, SOUTH_WEST, EAST, WEST, UP, DOWN,
    OPEN, CLOSE, PUSH, PULL, WALK_TO, PICK_UP, TALK_TO,
    GIVE, USE, LOOK_AT, TURN_ON, TURN_OFF, MOVE, INSERT,
    WEAR, UNWEAR, POUR, READ
}
