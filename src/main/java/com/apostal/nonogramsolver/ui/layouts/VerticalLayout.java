package com.apostal.nonogramsolver.ui.layouts;

import java.awt.*;

public class VerticalLayout implements LayoutManager {
    private Dimension size = new Dimension();

    // Следующие два метода не используются
    public void addLayoutComponent   (String name, Component comp) {}
    public void removeLayoutComponent(Component comp) {}

    // Метод определения минимального размера для контейнера
    public Dimension minimumLayoutSize(Container c) {
        return calculateBestSize(c);
    }
    // Метод определения предпочтительного размера для контейнера
    public Dimension preferredLayoutSize(Container c) {
        return calculateBestSize(c);
    }
    // Метод расположения компонентов в контейнере
    public void layoutContainer(Container container)
    {
        // Список компонентов
        Component list[] = container.getComponents();
        int currentY = 5;
        for (Component aList : list) {
            // Определение предпочтительного размера компонента
            Dimension pref = aList.getPreferredSize();
            // Размещение компонента на экране
            aList.setBounds(5, currentY, pref.width, pref.height);
            // Учитываем промежуток в 5 пикселов
            currentY += 5;
            // Смещаем вертикальную позицию компонента
            currentY += pref.height;
        }
    }
    // Метод вычисления оптимального размера контейнера
    private Dimension calculateBestSize(Container c)
    {
        // Вычисление длины контейнера
        Component[] list = c.getComponents();
        int maxWidth = 0;
        for (Component aList : list) {
            int width = aList.getWidth();
            // Поиск компонента с максимальной длиной
            if (width > maxWidth)
                maxWidth = width;
        }
        // Размер контейнера в длину с учетом левого отступа
        size.width = maxWidth + 5;
        // Вычисление высоты контейнера
        int height = 0;
        for (Component aList : list) {
            height += 5;
            height += aList.getHeight();
        }
        size.height = height;
        return size;
    }
}