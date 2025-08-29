package com.connectcall.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private List<Contact> contacts;
    private ContactsActivity activity;

    public ContactsAdapter(List<Contact> contacts, ContactsActivity activity) {
        this.contacts = contacts;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.contactName.setText(contact.getName());
        holder.contactPhone.setText(contact.getPhone());

        holder.videoCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startVideoCall(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        TextView contactPhone;
        Button videoCallButton;

        ContactViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactName);
            contactPhone = itemView.findViewById(R.id.contactPhone);
            videoCallButton = itemView.findViewById(R.id.videoCallButton);
        }
    }
}
