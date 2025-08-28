package com.example.unbrokenchainapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unbrokenchainapp.activities.ChainManagementActivity;
import com.example.unbrokenchainapp.activities.CalendarViewActivity;
import com.example.unbrokenchainapp.activities.MonthsViewActivity;
import com.example.unbrokenchainapp.activities.SummaryGraphActivity;
import com.example.unbrokenchainapp.utils.DatabaseTester;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity created successfully");

        try {
            // Run database tests
            DatabaseTester tester = new DatabaseTester(this);
            tester.runTests();
            tester.cleanup();
            
            setupButtonClickListeners();
            Log.d(TAG, "Button click listeners setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up button listeners", e);
            Toast.makeText(this, "Error initializing app", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupButtonClickListeners() {
        // Manage Chains button
        Button manageChainsButton = findViewById(R.id.btn_manage_chains);
        if (manageChainsButton != null) {
            manageChainsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MainActivity.this, ChainManagementActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "Navigating to ChainManagementActivity");
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to ChainManagementActivity", e);
                        Toast.makeText(MainActivity.this, "Error opening Chain Management", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Calendar View button
        Button calendarButton = findViewById(R.id.btn_calendar_view);
        if (calendarButton != null) {
            calendarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MainActivity.this, CalendarViewActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "Navigating to CalendarViewActivity");
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to CalendarViewActivity", e);
                        Toast.makeText(MainActivity.this, "Error opening Calendar View", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Months View button
        Button monthsButton = findViewById(R.id.btn_months_view);
        if (monthsButton != null) {
            monthsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MainActivity.this, MonthsViewActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "Navigating to MonthsViewActivity");
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to MonthsViewActivity", e);
                        Toast.makeText(MainActivity.this, "Error opening Months View", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Summary Graph button
        Button summaryButton = findViewById(R.id.btn_summary_graph);
        if (summaryButton != null) {
            summaryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MainActivity.this, SummaryGraphActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "Navigating to SummaryGraphActivity");
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to SummaryGraphActivity", e);
                        Toast.makeText(MainActivity.this, "Error opening Summary Graph", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity resumed");
    }
}
