package engine.loader;

import java.util.List;
import engine.command.Command;
import engine.command.Command.Type;

/** Runnable that loads all the possible game commands. */
public class CommandsLoader implements Runnable {
    List<Command> commands;

    public CommandsLoader(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public void run() {
        Command north = new Command(Type.NORTH, "nord");
        north.addAlias("n");
        north.addAlias("north");

        Command south = new Command(Type.SOUTH, "sud");
        south.addAlias("s");
        south.addAlias("south");

        Command east = new Command(Type.EAST, "est");
        east.addAlias("e");
        east.addAlias("east");

        Command west = new Command(Type.WEST, "ovest");
        west.addAlias("w");
        west.addAlias("o");
        west.addAlias("west");

        Command southWest = new Command(Type.SOUTH_WEST, "sudovest");
        southWest.addAlias("so");
        southWest.addAlias("sw");
        southWest.addAlias("sud-ovest");
        southWest.addAlias("southwest");
        southWest.addAlias("south-west");

        Command southEast = new Command(Type.SOUTH_EAST, "sudest");
        southEast.addAlias("se");
        southEast.addAlias("sud-est");
        southEast.addAlias("south-east");
        southEast.addAlias("southeast");

        Command northWest = new Command(Type.NORTH_WEST, "nordovest");
        northWest.addAlias("no");
        northWest.addAlias("nw");
        northWest.addAlias("northwest");
        northWest.addAlias("nord-ovest");
        northWest.addAlias("north-west");

        Command northEast = new Command(Type.NORTH_EAST, "nordest");
        northEast.addAlias("ne");
        northEast.addAlias("northeast");
        northEast.addAlias("north-east");
        northEast.addAlias("nord-est");

        Command up = new Command(Type.UP, "su");
        up.addAlias("sopra");
        up.addAlias("superiore");
        up.addAlias("up");
        up.addAlias("sali");
        up.addAlias("upstairs");

        Command down = new Command(Type.DOWN, "giu");
        down.addAlias("sotto");
        down.addAlias("inferiore");
        down.addAlias("down");
        down.addAlias("scendi");
        down.addAlias("downstairs");

        Command inventory = new Command(Type.INVENTORY, "inventario");
        inventory.addAlias("inv");
        inventory.addAlias("inventory");

        Command lookAt = new Command(Type.LOOK_AT, "osserva");
        lookAt.addAlias("guarda");
        lookAt.addAlias("esamina");
        lookAt.addAlias("x");
        lookAt.addAlias("look");
        lookAt.addAlias("vedi");

        Command pickUp = new Command(Type.PICK_UP, "prendi");
        pickUp.addAlias("pick");
        pickUp.addAlias("pickup");
        pickUp.addAlias("raccogli");

        Command open = new Command(Type.OPEN, "apri");
        open.addAlias("open");

        Command push = new Command(Type.PUSH, "spingi");
        push.addAlias("push");
        push.addAlias("premi");
        push.addAlias("schiaccia");

        Command pull = new Command(Type.PULL, "tira");
        pull.addAlias("pull");

        Command move = new Command(Type.MOVE, "sposta");
        move.addAlias("move");
        move.addAlias("muovi");
        move.addAlias("trascina");

        Command insert = new Command(Type.INSERT, "inserisci");
        insert.addAlias("insert");
        insert.addAlias("metti");

        Command wear = new Command(Type.WEAR, "indossa");
        wear.addAlias("vesti");
        wear.addAlias("mettiti");
        wear.addAlias("wear");
        wear.addAlias("equip");
        wear.addAlias("equipaggia");

        Command unwear = new Command(Type.UNWEAR, "togli");
        unwear.addAlias("togliti");
        unwear.addAlias("unwear");
        unwear.addAlias("unequip");
        unwear.addAlias("disequipaggia");
        unwear.addAlias("rimuovi");

        Command turnOn = new Command(Type.TURN_ON, "accendi");
        turnOn.addAlias("light");

        Command turnOff = new Command(Type.TURN_OFF, "spegni");
        turnOff.addAlias("spengi");

        Command talkTo = new Command(Type.TALK_TO, "parla");
        talkTo.addAlias("talk");
        talkTo.addAlias("speak");
        talkTo.addAlias("conversa");
        talkTo.addAlias("chiedi");
        talkTo.addAlias("ask");

        Command pour = new Command(Type.POUR, "versa");
        pour.addAlias("butta");
        pour.addAlias("pour");
        pour.addAlias("throw");

        Command read = new Command(Type.READ, "leggi");
        read.addAlias("read");

        Command save = new Command(Type.SAVE, "salva");
        save.addAlias("save");

        commands.add(north);
        commands.add(northWest);
        commands.add(northEast);
        commands.add(south);
        commands.add(southWest);
        commands.add(southEast);
        commands.add(east);
        commands.add(west);
        commands.add(up);
        commands.add(down);
        commands.add(inventory);
        commands.add(lookAt);
        commands.add(pickUp);
        commands.add(open);
        commands.add(push);
        commands.add(pull);
        commands.add(move);
        commands.add(pour);
        commands.add(talkTo);
        commands.add(turnOff);
        commands.add(turnOn);
        commands.add(unwear);
        commands.add(wear);
        commands.add(insert);
        commands.add(read);
        commands.add(save);
    }

}
