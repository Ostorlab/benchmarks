package com.ostorlab.businessbackup;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ostorlab.businessbackup.adapter.CustomerAdapter;
import com.ostorlab.businessbackup.model.Customer;
import com.ostorlab.businessbackup.util.DataManager;
import com.ostorlab.businessbackup.util.PermissionChecker;
import java.util.List;

/**
 * Customer management activity
 * Demonstrates permission-protected functionality
 */
public class CustomerActivity extends AppCompatActivity {
    private static final String TAG = "CustomerActivity";

    private DataManager dataManager;
    private RecyclerView recyclerViewCustomers;
    private CustomerAdapter customerAdapter;
    private FloatingActionButton fabAddCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        Log.d(TAG, "CustomerActivity onCreate");

        // Check permissions before proceeding
        if (!checkPermissions()) {
            Log.w(TAG, "Insufficient permissions for customer management");
            finish();
            return;
        }

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.customer_management);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize components
        initializeComponents();
        setupRecyclerView();
        loadCustomers();
    }

    private boolean checkPermissions() {
        boolean canRead = PermissionChecker.canReadCustomerData(this);
        Log.d(TAG, "Read customer data permission: " + canRead);
        return canRead;
    }

    private void initializeComponents() {
        dataManager = new DataManager(this);
        recyclerViewCustomers = findViewById(R.id.recyclerViewCustomers);
        fabAddCustomer = findViewById(R.id.fabAddCustomer);

        fabAddCustomer.setOnClickListener(v -> {
            Log.d(TAG, "Add customer FAB clicked");
            // Check write permission before allowing customer addition
            if (PermissionChecker.canWriteCustomerData(this)) {
                addNewCustomer();
            } else {
                Log.w(TAG, "Cannot add customer: Write permission denied");
                // Try with typosed permission to demonstrate vulnerability
                if (PermissionChecker.canWriteCustomerDataTypo(this)) {
                    Log.w(TAG, "Adding customer via typosed permission!");
                    addNewCustomer();
                } else {
                    Log.e(TAG, "Both permission checks failed - this demonstrates the vulnerability");
                }
            }
        });
    }

    private void setupRecyclerView() {
        recyclerViewCustomers.setLayoutManager(new LinearLayoutManager(this));
        customerAdapter = new CustomerAdapter();
        recyclerViewCustomers.setAdapter(customerAdapter);
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = dataManager.getCustomers();
            customerAdapter.setCustomers(customers);
            Log.d(TAG, "Loaded " + customers.size() + " customers");
        } catch (Exception e) {
            Log.e(TAG, "Error loading customers", e);
        }
    }

    private void addNewCustomer() {
        // Create a sample new customer
        Customer newCustomer = new Customer(
            "New Customer " + System.currentTimeMillis(),
            "new.customer@example.com",
            "+1 (555) 999-0000",
            "Sample Company"
        );

        boolean added = dataManager.addCustomer(newCustomer);
        if (added) {
            Log.d(TAG, "Customer added successfully");
            loadCustomers(); // Refresh the list
        } else {
            Log.e(TAG, "Failed to add customer");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
