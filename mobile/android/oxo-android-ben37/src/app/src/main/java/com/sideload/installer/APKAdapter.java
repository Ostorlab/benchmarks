package com.sideload.installer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class APKAdapter extends RecyclerView.Adapter<APKAdapter.APKViewHolder> {
    private List<APKFile> apkFiles;
    private OnAPKClickListener listener;

    public interface OnAPKClickListener {
        void onAPKClick(APKFile apkFile);
    }

    public APKAdapter(List<APKFile> apkFiles, OnAPKClickListener listener) {
        this.apkFiles = apkFiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public APKViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apk, parent, false);
        return new APKViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull APKViewHolder holder, int position) {
        APKFile apkFile = apkFiles.get(position);
        holder.bind(apkFile);
    }

    @Override
    public int getItemCount() {
        return apkFiles.size();
    }

    class APKViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView pathTextView;
        private TextView sizeTextView;

        public APKViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.apkName);
            pathTextView = itemView.findViewById(R.id.apkPath);
            sizeTextView = itemView.findViewById(R.id.apkSize);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onAPKClick(apkFiles.get(position));
                }
            });
        }

        public void bind(APKFile apkFile) {
            nameTextView.setText(apkFile.getName());
            pathTextView.setText(apkFile.getPath());
            sizeTextView.setText(apkFile.getFormattedSize());
        }
    }
}
