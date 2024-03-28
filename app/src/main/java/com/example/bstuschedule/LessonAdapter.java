package com.example.bstuschedule;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private final ArrayList<Lesson> items;
    private View currentView;
    private final Context context;

    public LessonAdapter(@NonNull Context context, int resource, ArrayList<Lesson> items) {
        super();
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_item, parent, false);
        currentView = view;

        return new LessonAdapter.LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = items.get(position);
        holder.teacher.setText(lesson.teacher);
        holder.room.setText(lesson.room);
        holder.type.setText(lesson.type);
        holder.time.setText(lesson.time);
        holder.name.setText(lesson.name);

    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        TextView name;
        TextView type;
        TextView room;
        TextView teacher;

        public LessonViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);
            room = itemView.findViewById(R.id.room);
            teacher = itemView.findViewById(R.id.teacher);
        }
    }
}
