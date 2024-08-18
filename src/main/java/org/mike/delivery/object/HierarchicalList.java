package org.mike.delivery.object;

import java.util.ArrayList;
import java.util.List;

public class HierarchicalList {

    private List<HierarchicalObject> objects;

    public HierarchicalList() {
        this.objects = new ArrayList<>();
    }

    public static HierarchicalList createList(HierarchicalObject... objects) {
        HierarchicalList list = new HierarchicalList();
        for (HierarchicalObject obj : objects) {
            list.addObject(obj);
        }
        return list;
    }

    public void addObject(HierarchicalObject obj) {
        this.objects.add(obj);
    }

    public void printObjects() {
        for (HierarchicalObject obj : objects) {
            System.out.println(obj);
        }
    }
    public String getObjectsAsString() {
        StringBuilder sb = new StringBuilder();
        for (HierarchicalObject obj : objects) {
            sb.append(obj.toString()).append("\n");
        }
        return sb.toString();
    }


    public List<HierarchicalObject> getObjects() {
        return objects;
    }

    public void setObjects(List<HierarchicalObject> objects) {
        this.objects = objects;
    }


}
