package com.moutamid.mailbox;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.moutamid.mailbox.databinding.ActivityInboxBinding;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InboxActivity extends AppCompatActivity {

    ActivityInboxBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInboxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Account Name String received from previous activity
        String accName = getIntent().getStringExtra("AccountName");
        // This is the Important way to initialise the GoogleCredential using the Account name retrieved from Sign-in!!!
        // (Use this while initialising the service)
        // Pass this credential to the Async Task created.
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(GmailScopes.GMAIL_READONLY));
        credential.setSelectedAccountName(accName);
        // Using Async Task to Fetch the emails from the background thread
        // TIP - fetching all the emails take a lot of time, so store all the fetched emails into your SQL database and just retrieve the new emails from next time
        FetchEmailsTask task = new FetchEmailsTask(credential);
        task.execute();
    }

    private void showMails(List<Message> list) {
        // this function uses the email list to show using RecyclerView
        MessagesAdapter adapter = new MessagesAdapter(list);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);
        binding.progressBar.setVisibility(View.GONE);
    }

    private class FetchEmailsTask extends AsyncTask<Void, Void, ArrayList<Message>> {

        GoogleAccountCredential initializer;

        public FetchEmailsTask(GoogleAccountCredential init) {
            this.initializer = init;
        }

        @Override
        protected ArrayList<Message> doInBackground(Void... params) {
            ArrayList<Message> result = new ArrayList<>();
            try {
                // Using GMAIL Service to fetch all the emails...
                final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                Gmail service = new Gmail.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), initializer).setApplicationName("Mailbox").build();
                String user = "me";
                ListMessagesResponse listResponse = service.users().messages().list(user).execute();
                List<Message> messages = listResponse.getMessages();

                if (messages.isEmpty()) {
                    Log.d("Info", "No messages found.");
                } else {
                    for (Message message : messages) {
                        Message fullMessage = service.users().messages().get(user, message.getId()).execute();
                        result.add(fullMessage);
                        Log.d("Mail>>>", fullMessage.getSnippet());
                    }
                }
            } catch (IOException | GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Message> messages) {
            super.onPostExecute(messages);
            InboxActivity.this.showMails(messages);
        }
    }

}

