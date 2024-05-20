package com.moutamid.mailbox;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.mailbox.databinding.MessageLayoutBinding;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesVH> {
    private final List<Message> list;

    public MessagesAdapter(List<Message> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MessagesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessagesVH(MessageLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesVH holder, int position) {
        holder.message.setText(list.get(position).getSnippet());
        List<MessagePartHeader> headers = list.get(position).getPayload().getHeaders();
        String sender = null;
        String date = null;
        for (MessagePartHeader header : headers) {
            if (header.getName().equals("From")) {
                sender = header.getValue();
            } else if (header.getName().equals("Date")) {
                date = header.getValue();
            }
        }
        holder.sender.setText(sender);
        holder.time.setText(date);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

