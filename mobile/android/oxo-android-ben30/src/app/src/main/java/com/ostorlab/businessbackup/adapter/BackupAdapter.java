package com.ostorlab.businessbackup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.ostorlab.businessbackup.R;
import com.ostorlab.businessbackup.model.BackupRecord;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying backup records in RecyclerView
 */
public class BackupAdapter extends RecyclerView.Adapter<BackupAdapter.BackupViewHolder> {
    
    private List<BackupRecord> backupRecords = new ArrayList<>();
    private OnBackupActionListener onBackupActionListener;

    public interface OnBackupActionListener {
        void onRestoreBackup(BackupRecord backupRecord);
    }

    public void setBackupRecords(List<BackupRecord> backupRecords) {
        this.backupRecords = backupRecords;
        notifyDataSetChanged();
    }

    public void setOnBackupActionListener(OnBackupActionListener listener) {
        this.onBackupActionListener = listener;
    }

    @NonNull
    @Override
    public BackupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_backup, parent, false);
        return new BackupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BackupViewHolder holder, int position) {
        BackupRecord backupRecord = backupRecords.get(position);
        holder.bind(backupRecord);
    }

    @Override
    public int getItemCount() {
        return backupRecords.size();
    }

    class BackupViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBackupName;
        private TextView tvBackupDate;
        private TextView tvBackupSize;
        private MaterialButton btnRestoreBackup;

        public BackupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBackupName = itemView.findViewById(R.id.tvBackupName);
            tvBackupDate = itemView.findViewById(R.id.tvBackupDate);
            tvBackupSize = itemView.findViewById(R.id.tvBackupSize);
            btnRestoreBackup = itemView.findViewById(R.id.btnRestoreBackup);

            btnRestoreBackup.setOnClickListener(v -> {
                if (onBackupActionListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onBackupActionListener.onRestoreBackup(backupRecords.get(getAdapterPosition()));
                }
            });
        }

        public void bind(BackupRecord backupRecord) {
            tvBackupName.setText(backupRecord.getName());
            
            // Format the timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy - h:mm a", Locale.getDefault());
            String formattedDate = sdf.format(new Date(backupRecord.getTimestamp()));
            tvBackupDate.setText(formattedDate);
            
            tvBackupSize.setText(backupRecord.getFormattedSize());
        }
    }
}
