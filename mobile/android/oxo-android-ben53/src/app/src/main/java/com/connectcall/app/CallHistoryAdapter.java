package com.connectcall.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.CallHistoryViewHolder> {
    private List<CallRecord> callHistory;
    private SimpleDateFormat dateFormat;

    public CallHistoryAdapter(List<CallRecord> callHistory) {
        this.callHistory = callHistory;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public CallHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_history, parent, false);
        return new CallHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallHistoryViewHolder holder, int position) {
        CallRecord record = callHistory.get(position);
        holder.contactName.setText(record.getName());
        holder.contactPhone.setText(record.getPhone());
        holder.callTime.setText(dateFormat.format(new Date(record.getTimestamp())));
        holder.callType.setText(record.getType());
    }

    @Override
    public int getItemCount() {
        return callHistory.size();
    }

    static class CallHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        TextView contactPhone;
        TextView callTime;
        TextView callType;

        CallHistoryViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.historyContactName);
            contactPhone = itemView.findViewById(R.id.historyContactPhone);
            callTime = itemView.findViewById(R.id.historyCallTime);
            callType = itemView.findViewById(R.id.historyCallType);
        }
    }
}
