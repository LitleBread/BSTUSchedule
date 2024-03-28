package com.example.bstuschedule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DayScheduleAdapter extends RecyclerView.Adapter<DayScheduleAdapter.DayViewHolder> {
    private Context context;
    private List<DaySchedule> dayList;
    private View currentView;

    public DayScheduleAdapter(Context context, List<DaySchedule> dayList) {
        this.context = context;
        this.dayList = dayList;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item, parent, false);
        currentView = view;
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        DaySchedule daySchedule = dayList.get(position);
        holder.dayName.setText(daySchedule.name);
        holder.lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        LessonAdapter lessonAdapter = new LessonAdapter(context, R.layout.lesson_item, (ArrayList<Lesson>) daySchedule.lessons);
        this.currentView.setOnLongClickListener(v ->{
            Toast.makeText(currentView.getContext(), holder.dayName.getText(), (int)10000).show();
            return true;
        });
        holder.lessonsRecyclerView.setAdapter(lessonAdapter);

        Log.i("dset " + holder.dayName, "");
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dayName;
        RecyclerView lessonsRecyclerView;

        public DayViewHolder(View itemView) {
            super(itemView);
            dayName = itemView.findViewById(R.id.dayName);
            lessonsRecyclerView = itemView.findViewById(R.id.lessons);

        }
    }
}
