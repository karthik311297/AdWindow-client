package com.example.adwindow.adwindow_client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.adwindow.adwindow_client.R;
import com.example.adwindow.adwindow_client.adapter.model.ScreenAdStatus;
import com.example.adwindow.adwindow_client.model.Content;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContentRecyclerAdapter extends FirebaseRecyclerAdapter <Content, ContentRecyclerAdapter.ContentViewHolder>{
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private Context context;
    private ProgressBar progressBar;
    private TextView emptyAdapterText;

    public ContentRecyclerAdapter(Context context, @NonNull FirebaseRecyclerOptions<Content> options, ProgressBar progressBar, TextView emptyAdapterText) {
        super(options);
        this.context = context;
        this.progressBar =  progressBar;
        this.emptyAdapterText = emptyAdapterText;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_view_layout,parent,false);
        return new ContentRecyclerAdapter.ContentViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ContentViewHolder holder, int position, @NonNull Content model)
    {
        holder.textView.setText(model.getContentName());

        List<ScreenAdStatus> screenAdStatusList = new ArrayList<>();

        for(String title : model.getAdvertisementsStatus().keySet())
        {
            screenAdStatusList.add(new ScreenAdStatus(title, model.getAdvertisementsStatus().get(title)));
        }

        holder.listView.setAdapter(new ScreenAdStatusAdapter(context, screenAdStatusList));
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder{


        private TextView textView;
        private ListView listView;
        private ImageView imageView;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.adNameFromDB);

            listView = itemView.findViewById(R.id.screensIn);

            imageView = itemView.findViewById(R.id.downloadButton);
        }
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        progressBar.setVisibility(View.GONE);
        emptyAdapterText.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
