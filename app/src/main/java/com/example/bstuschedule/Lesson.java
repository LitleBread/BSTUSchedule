package com.example.bstuschedule;

public class Lesson {
    public String time;
    public String teacher;
    public String room;
    public String name;
    public String type;
    public Lesson(String time, String name, String teacher, String room, String type){
        this.name = name;
        this.time = time;
        this.teacher = teacher;
        this.room = room;
        this.type = type;
    }
    public Lesson(){}

    @Override
    public String toString() {
        return "Lesson{" +
                "time='" + time + '\'' +
                ", teacher='" + teacher + '\'' +
                ", room='" + room + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

