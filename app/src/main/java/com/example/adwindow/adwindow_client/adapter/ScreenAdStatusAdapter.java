package com.example.adwindow.adwindow_client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.adwindow.adwindow_client.R;
import com.example.adwindow.adwindow_client.adapter.model.ScreenAdStatus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ScreenAdStatusAdapter extends ArrayAdapter<ScreenAdStatus> {

    public ScreenAdStatusAdapter(Context context, List<ScreenAdStatus> screenAdStatuses) {
        super(context, 0, screenAdStatuses);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.screen_upload_status_view_layout,parent,false);
        }
        ScreenAdStatus screenAdStatus = getItem(position);
        if(screenAdStatus!=null)
        {
            TextView screenTitle = listItemView.findViewById(R.id.screenTitlesHavingContent);
            screenTitle.setText(screenAdStatus.getScreenTitle());

            TextView adStatus = listItemView.findViewById(R.id.pendingStatus);
            boolean isAdvertised = screenAdStatus.isAdvertisedStatus();
            if(isAdvertised)
            {
                adStatus.setText("Running");
                adStatus.setBackgroundResource(R.color.custGreen);
            }
            else
            {
                adStatus.setText("Pending");
                adStatus.setBackgroundResource(R.color.custOrange);
            }
        }
        return listItemView;
    }
}
