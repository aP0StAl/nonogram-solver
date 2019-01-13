package com.apostal.nonogramsolver.ui.layouts;

import info.clearthought.layout.TableLayout;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Iterator;

public class FixedTableLayout extends TableLayout {
    public FixedTableLayout(double[][] size){
        super(size);
    }

    @Override
    public void removeLayoutComponent (Component component)
    {
        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            Entry entry = (Entry)iterator.next();
            Field componentField;
            Component currentComponent = null;
            try {
                componentField = (Entry.class).getDeclaredField("component");
                componentField.setAccessible(true);
                currentComponent = (Component)componentField.get(entry);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(component.equals(currentComponent))
                iterator.remove();
        }
        //list.remove(component);
        dirty = true;
    }
}
