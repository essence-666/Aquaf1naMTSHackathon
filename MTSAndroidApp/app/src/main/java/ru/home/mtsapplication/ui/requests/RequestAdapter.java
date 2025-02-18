package ru.home.mtsapplication.ui.requests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.home.mtsapplication.R;
import ru.home.mtsapplication.models.RequestModel;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<RequestModel> requests;

    public RequestAdapter(List<RequestModel> requests) {
        this.requests = requests;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView topicText, detailsText, commentsText, emailText;

        public ViewHolder(View itemView) {
            super(itemView);
            topicText = itemView.findViewById(R.id.text_topic);
            detailsText = itemView.findViewById(R.id.text_details);
            commentsText = itemView.findViewById(R.id.text_comments);
            emailText = itemView.findViewById(R.id.text_email);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestModel request = requests.get(position);
        holder.topicText.setText(request.getTopic());
        holder.detailsText.setText(request.getDetails());
        holder.commentsText.setText(request.getComments());
        holder.emailText.setText(request.getUserEmail());
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }


}

