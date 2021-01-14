package com.example.adwindow.adwindow_client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.adwindow.adwindow_client.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ScreenTitleAdapter extends RecyclerView.Adapter<ScreenTitleAdapter.ViewHolder> {

    private List<String> screenLocTitles;
    private HashMap<String, Boolean> checkBoxStates;

    public boolean areAllChecked() {
        return allChecked;
    }

    private boolean allChecked;

    public ScreenTitleAdapter(List<String> screenLocTitles) {
        this.screenLocTitles = screenLocTitles;
        checkBoxStates = new HashMap<String, Boolean>();
        allChecked = false;
    }

    @NonNull
    @Override
    public ScreenTitleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View screenLocTitleView = inflater.inflate(R.layout.screen_choose,parent,false);
        return new ViewHolder(screenLocTitleView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScreenTitleAdapter.ViewHolder holder, int position) {
        String screenLocTitle = screenLocTitles.get(position);
        holder.screenLocTitleTextView.setText(screenLocTitle);
        holder.screenCheck.setChecked(checkBoxStates.containsKey(screenLocTitle)?checkBoxStates.get(screenLocTitle):false);
    }

    public void checkAllLocations()
    {
        for(String screenLocTitle : screenLocTitles )
        {
            checkBoxStates.put(screenLocTitle, true);
            allChecked = true;
        }
    }

    public void uncheckAllLocations()
    {
        for(String screenLocTitle : screenLocTitles )
        {
            checkBoxStates.put(screenLocTitle, false);
            allChecked = false;
        }
    }

    public List<String> getAllCheckedLocations()
    {
        List<String> checkedLocs = new ArrayList<>();
        for (String key : checkBoxStates.keySet()) {
            if (checkBoxStates.containsKey(key) && checkBoxStates.get(key)) {
                checkedLocs.add(key);
            }
        }
        return checkedLocs;
    }

    @Override
    public int getItemCount() {
        return screenLocTitles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView screenLocTitleTextView;
        public CheckBox screenCheck;
        private Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            screenLocTitleTextView = itemView.findViewById(R.id.screenLoc);
            screenCheck = itemView.findViewById(R.id.screenCheck);
            screenCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = screenLocTitleTextView.getText().toString();
                    if(checkBoxStates.get(title)!=null && checkBoxStates.get(title))
                    {
                        checkBoxStates.put(title, false);
                    }
                    else
                    {
                        checkBoxStates.put(title, true);
                    }
                }
            });

        }
    }
}
