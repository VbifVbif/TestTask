package org.mike.delivery.object;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class HierarchicalObject {
    private String name;
    private String price;
    private String quantity;
    private String cost;
    private List<HierarchicalObject> subObjects;

    public HierarchicalObject() {
        this.subObjects = new ArrayList<>();
    }

    public HierarchicalObject(String name, String price, String quantity, String cost) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.cost = cost;
        this.subObjects = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<HierarchicalObject> getSubObjects() {
        return subObjects;
    }

    public void addSubObject(HierarchicalObject subObject) {
        this.subObjects.add(subObject);
    }

    public void removeSubObject(HierarchicalObject subObject) {
        this.subObjects.remove(subObject);
    }

    @Override
    public String toString() {
        return "HierarchicalObject{" +
                "name='" + name + '\'' +
                "price='" + price + '\'' +
                "quantity='" + quantity + '\'' +
                "cost='" + cost + '\'' +
                ", subObjects=" + subObjects +
                '}';
    }
}
