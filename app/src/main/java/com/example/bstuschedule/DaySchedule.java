package com.example.bstuschedule;

import java.util.*;

public class DaySchedule {
    public String name;
    public List<Lesson> lessons;

    public DaySchedule(String name) {
        this.name = name;
        lessons = new ArrayList<Lesson>();
    }

    @Override
    public String toString() {
        return "DaySchedule{" +
                "name='" + name + '\'' +
                ", lessons=" + lessons +
                '}' + "\n";
    }
}
