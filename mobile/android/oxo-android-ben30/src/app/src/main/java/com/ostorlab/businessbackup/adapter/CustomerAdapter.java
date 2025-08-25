package com.ostorlab.businessbackup.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ostorlab.businessbackup.R;
import com.ostorlab.businessbackup.model.Customer;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying customer list in RecyclerView
 */
public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    
    private List<Customer> customers = new ArrayList<>();
    private OnCustomerClickListener onCustomerClickListener;

    public interface OnCustomerClickListener {
        void onCustomerClick(Customer customer);
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
        notifyDataSetChanged();
    }

    public void setOnCustomerClickListener(OnCustomerClickListener listener) {
        this.onCustomerClickListener = listener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.bind(customer);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    class CustomerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCustomerName;
        private TextView tvCustomerCompany;
        private TextView tvCustomerEmail;
        private TextView tvCustomerPhone;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerCompany = itemView.findViewById(R.id.tvCustomerCompany);
            tvCustomerEmail = itemView.findViewById(R.id.tvCustomerEmail);
            tvCustomerPhone = itemView.findViewById(R.id.tvCustomerPhone);

            itemView.setOnClickListener(v -> {
                if (onCustomerClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onCustomerClickListener.onCustomerClick(customers.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Customer customer) {
            tvCustomerName.setText(customer.getName());
            tvCustomerCompany.setText(customer.getCompany());
            tvCustomerEmail.setText(customer.getEmail());
            tvCustomerPhone.setText(customer.getPhone());
        }
    }
}
