package com.example.unbrokenchainapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unbrokenchainapp.R;
import com.example.unbrokenchainapp.database.ChainDatabase;
import com.example.unbrokenchainapp.models.Chain;
import com.example.unbrokenchainapp.models.ChainEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MonthsViewActivity extends AppCompatActivity {

    private ChainDatabase database;
    private Spinner chainSpinner;
    private RecyclerView monthsRecyclerView;
    private List<Chain> chains;
    private Chain selectedChain;
    private MonthsAdapter monthsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_months_view);

        database = new ChainDatabase(this);
        chains = new ArrayList<>();

        chainSpinner = findViewById(R.id.chain_spinner);
        monthsRecyclerView = findViewById(R.id.months_recycler_view);

        monthsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        monthsAdapter = new MonthsAdapter();
        monthsRecyclerView.setAdapter(monthsAdapter);

        loadChains();

        chainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < chains.size()) {
                    selectedChain = chains.get(position);
                    loadMonthsData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedChain = null;
                monthsAdapter.clearData();
            }
        });
    }

    private void loadChains() {
        chains = database.getAllChains();
        List<String> chainNames = new ArrayList<>();
        for (Chain chain : chains) {
            chainNames.add(chain.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, chainNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chainSpinner.setAdapter(adapter);

        if (!chains.isEmpty()) {
            selectedChain = chains.get(0);
            loadMonthsData();
        }
    }

    private void loadMonthsData() {
        if (selectedChain == null) {
            monthsAdapter.clearData();
            return;
        }

        List<MonthData> monthsData = new ArrayList<>();
        Calendar currentDate = Calendar.getInstance();
        
        // Show last 6 months and next 6 months
        for (int i = -6; i <= 6; i++) {
            Calendar monthDate = (Calendar) currentDate.clone();
            monthDate.add(Calendar.MONTH, i);
            
            int year = monthDate.get(Calendar.YEAR);
            int month = monthDate.get(Calendar.MONTH) + 1;
            
            List<ChainEntry> entries = database.getEntriesForMonth(selectedChain.getId(), year, month);
            monthsData.add(new MonthData(year, month, entries));
        }

        monthsAdapter.setData(monthsData);
    }

    private static class MonthData {
        int year;
        int month;
        List<ChainEntry> entries;

        MonthData(int year, int month, List<ChainEntry> entries) {
            this.year = year;
            this.month = month;
            this.entries = entries;
        }
    }

    private class MonthsAdapter extends RecyclerView.Adapter<MonthsAdapter.MonthViewHolder> {
        private List<MonthData> monthsData = new ArrayList<>();

        public void setData(List<MonthData> data) {
            this.monthsData = data;
            notifyDataSetChanged();
        }

        public void clearData() {
            this.monthsData.clear();
            notifyDataSetChanged();
        }

        @Override
        public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_month_view, parent, false);
            return new MonthViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MonthViewHolder holder, int position) {
            holder.bind(monthsData.get(position));
        }

        @Override
        public int getItemCount() {
            return monthsData.size();
        }

        class MonthViewHolder extends RecyclerView.ViewHolder {
            private TextView monthTitle;
            private LinearLayout daysContainer;

            MonthViewHolder(View itemView) {
                super(itemView);
                monthTitle = itemView.findViewById(R.id.month_title);
                daysContainer = itemView.findViewById(R.id.days_container);
            }

            void bind(MonthData monthData) {
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                Calendar cal = Calendar.getInstance();
                cal.set(monthData.year, monthData.month - 1, 1);
                monthTitle.setText(monthFormat.format(cal.getTime()));

                daysContainer.removeAllViews();
                
                // Create calendar for this month
                Calendar calendar = Calendar.getInstance();
                calendar.set(monthData.year, monthData.month - 1, 1);
                int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                for (int day = 1; day <= daysInMonth; day++) {
                    TextView dayView = new TextView(itemView.getContext());
                    dayView.setText(String.valueOf(day));
                    dayView.setGravity(Gravity.CENTER);
                    dayView.setTextSize(10);
                    dayView.setPadding(2, 2, 2, 2);
                    dayView.setBackgroundColor(Color.WHITE);
                    dayView.setMinWidth(20);
                    dayView.setMinHeight(20);

                    // Check if this day is completed
                    boolean isCompleted = isDayCompleted(day, monthData);
                    if (isCompleted) {
                        dayView.setBackgroundColor(Color.parseColor("#4CAF50"));
                        dayView.setTextColor(Color.WHITE);
                    }

                    // Check if this is today
                    Calendar today = Calendar.getInstance();
                    if (monthData.year == today.get(Calendar.YEAR) &&
                        monthData.month == (today.get(Calendar.MONTH) + 1) &&
                        day == today.get(Calendar.DAY_OF_MONTH)) {
                        dayView.setBackgroundColor(Color.parseColor("#2196F3"));
                        dayView.setTextColor(Color.WHITE);
                    }

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(1, 1, 1, 1);
                    dayView.setLayoutParams(params);

                    daysContainer.addView(dayView);
                }
            }

            private boolean isDayCompleted(int day, MonthData monthData) {
                if (monthData.entries == null) return false;

                Calendar targetDate = Calendar.getInstance();
                targetDate.set(Calendar.YEAR, monthData.year);
                targetDate.set(Calendar.MONTH, monthData.month - 1);
                targetDate.set(Calendar.DAY_OF_MONTH, day);

                for (ChainEntry entry : monthData.entries) {
                    Calendar entryDate = Calendar.getInstance();
                    entryDate.setTime(entry.getDate());
                    
                    if (entryDate.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                        entryDate.get(Calendar.MONTH) == targetDate.get(Calendar.MONTH) &&
                        entryDate.get(Calendar.DAY_OF_MONTH) == targetDate.get(Calendar.DAY_OF_MONTH)) {
                        return entry.isCompleted();
                    }
                }
                return false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
    }
} 