package com.securevault.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {
    private List<?> items;
    private String masterKey;
    private boolean isPasswordList;

    public PasswordAdapter(List<?> items, String masterKey) {
        this.items = items;
        this.masterKey = masterKey;
        this.isPasswordList = !items.isEmpty() && items.get(0) instanceof PasswordEntry;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (isPasswordList) {
            PasswordEntry entry = (PasswordEntry) items.get(position);
            holder.title.setText(entry.getSiteName());
            holder.subtitle.setText(entry.getUsername());
        } else {
            SecureNote note = (SecureNote) items.get(position);
            holder.title.setText(note.getTitle());
            String decryptedContent = CryptoManager.decrypt(note.getEncryptedContent(), masterKey);
            String preview = decryptedContent != null && decryptedContent.length() > 50
                ? decryptedContent.substring(0, 50) + "..."
                : decryptedContent;
            holder.subtitle.setText(preview);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            subtitle = itemView.findViewById(android.R.id.text2);
        }
    }
}
