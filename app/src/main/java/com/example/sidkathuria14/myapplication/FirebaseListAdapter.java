package com.example.sidkathuria14.myapplication;

import android.text.format.DateFormat;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by sidkathuria14 on 17/8/18.
 */

public class FirebaseListAdapter  {


    ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

    adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
    R.layout.message, FirebaseDatabase.getInstance().getReference()) {
        @Override
        protected void populateView(View v, ChatMessage model, int position) {
            // Get references to the views of message.xml
            TextView messageText = (TextView)v.findViewById(R.id.message_text);
            TextView messageUser = (TextView)v.findViewById(R.id.message_user);
            TextView messageTime = (TextView)v.findViewById(R.id.message_time);

            // Set their text
            messageText.setText(model.getMessageText());
            messageUser.setText(model.getMessageUser());

            // Format the date before showing it
            messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                    model.getMessageTime()));
        }
    };

listOfMessages.setAdapter(adapter);
}
