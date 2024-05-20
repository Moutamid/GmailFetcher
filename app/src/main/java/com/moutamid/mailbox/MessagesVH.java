package com.moutamid.mailbox;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.mailbox.databinding.MessageLayoutBinding;

public class MessagesVH extends RecyclerView.ViewHolder {

    public TextView sender, time, message;

    public MessagesVH(@NonNull MessageLayoutBinding itemView) {
        super(itemView.getRoot());
        sender = itemView.senderText;
        time = itemView.timeText;
        message = itemView.messageText;
    }
}
