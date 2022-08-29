/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.type;

import java.util.ArrayList;
import java.util.List;
import di.uniba.map.b.adventure.entities.AbstractEntity;

/**
 * @author pierpaolo
 */
public class Inventory {

    private List<AbstractEntity> list = new ArrayList<>();

    public List<AbstractEntity> getList() {
        return list;
    }

    public void setList(List<AbstractEntity> list) {
        this.list = list;
    }

    public void add(AbstractEntity o) {
        list.add(o);
    }

    public void remove(AbstractEntity o) {
        list.remove(o);
    }
}
