package com.example.bstuschedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class PeriodAdapter extends ArrayAdapter<Period> {

    private List<Period> periods;
    private Context context;

    public PeriodAdapter(Context context, int resource, List<Period> periods) {
        super(context, resource, periods);
        this.context = context;
        this.periods = periods;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        // Customize the layout if needed
        TextView textView = listItem.findViewById(android.R.id.text1);
        textView.setText(periods.get(position).getShowValue());

        return listItem;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View dropdownItem = convertView;
        if (dropdownItem == null) {
            dropdownItem = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        // Customize the dropdown layout if needed
        TextView textView = dropdownItem.findViewById(android.R.id.text1);
        textView.setText(periods.get(position).getShowValue());

        return dropdownItem;
    }
}

