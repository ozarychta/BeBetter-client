package com.ozarychta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

public class EnumArrayAdapter<T> extends ArrayAdapter<EnumWithLabel> {

    public EnumArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull EnumWithLabel[] objects) {
        super(context, resource, 0, Arrays.asList(objects));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckedTextView text = (CheckedTextView) convertView;

        if (text== null) {
            text = (CheckedTextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item,  parent, false);
        }

        text.setText(getItem(position).getLabel(getContext()));

        return text;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CheckedTextView text = (CheckedTextView) convertView;

        if (text== null) {
            text = (CheckedTextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item,  parent, false);
        }

        text.setText(getItem(position).getLabel(getContext()));

        return text;
    }
}
