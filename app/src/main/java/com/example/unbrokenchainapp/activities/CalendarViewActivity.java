package com.example.unbrokenchainapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.unbrokenchainapp.R;
import com.example.unbrokenchainapp.database.ChainDatabase;
import com.example.unbrokenchainapp.fragments.SummaryFragment;
import com.example.unbrokenchainapp.models.Chain;
import com.example.unbrokenchainapp.models.ChainEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarViewActivity extends AppCompatActivity {

    private ChainDatabase database;
    private Spinner chainSpinner;
    private TextView monthYearText;
    private GridLayout calendarGrid;
    private ImageButton previousMonthButton;
    private ImageButton nextMonthButton;
    private SummaryFragment summaryFragment;

    private Calendar currentMonth;
    private List<Chain> chains;
    private Chain selectedChain;
    private List<ChainEntry> currentMonthEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        database = new ChainDatabase(this);
        currentMonth = Calendar.getInstance();
        chains = new ArrayList<>();
        currentMonthEntries = new ArrayList<>();

        initializeViews();
        loadChains();
        setupCalendar();
        setupSummaryFragment();

        // Check if a specific chain was passed
        Intent intent = getIntent();
        if (intent.hasExtra("chain_id")) {
            long chainId = intent.getLongExtra("chain_id", -1);
            selectChainById(chainId);
        }
    }

    private void initializeViews() {
        chainSpinner = findViewById(R.id.chain_spinner);
        monthYearText = findViewById(R.id.month_year_text);
        calendarGrid = findViewById(R.id.calendar_grid);
        previousMonthButton = findViewById(R.id.btn_previous_month);
        nextMonthButton = findViewById(R.id.btn_next_month);

        previousMonthButton.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        nextMonthButton.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        chainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < chains.size()) {
                    selectedChain = chains.get(position);
                    loadMonthEntries();
                    updateCalendar();
                    updateSummary();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedChain = null;
                currentMonthEntries.clear();
                updateCalendar();
                updateSummary();
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
            loadMonthEntries();
        }
    }

    private void selectChainById(long chainId) {
        for (int i = 0; i < chains.size(); i++) {
            if (chains.get(i).getId() == chainId) {
                chainSpinner.setSelection(i);
                break;
            }
        }
    }

    private void loadMonthEntries() {
        if (selectedChain == null) {
            currentMonthEntries.clear();
            return;
        }

        int year = currentMonth.get(Calendar.YEAR);
        int month = currentMonth.get(Calendar.MONTH) + 1;
        currentMonthEntries = database.getEntriesForMonth(selectedChain.getId(), year, month);
    }

    private void setupCalendar() {
        updateCalendar();
    }

    private void updateCalendar() {
        calendarGrid.removeAllViews();
        
        // Update month/year text
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        monthYearText.setText(monthFormat.format(currentMonth.getTime()));

        // Add day headers
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayName : dayNames) {
            TextView dayHeader = new TextView(this);
            dayHeader.setText(dayName);
            dayHeader.setGravity(Gravity.CENTER);
            dayHeader.setTextSize(12);
            dayHeader.setTextColor(Color.GRAY);
            dayHeader.setPadding(8, 8, 8, 8);
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            dayHeader.setLayoutParams(params);
            
            calendarGrid.addView(dayHeader);
        }

        // Get calendar info
        Calendar calendar = (Calendar) currentMonth.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add empty cells for days before the first day of the month
        for (int i = 0; i < firstDayOfWeek; i++) {
            TextView emptyDay = new TextView(this);
            emptyDay.setText("");
            emptyDay.setBackgroundColor(Color.TRANSPARENT);
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            emptyDay.setLayoutParams(params);
            
            calendarGrid.addView(emptyDay);
        }

        // Add day cells
        for (int day = 1; day <= daysInMonth; day++) {
            TextView dayCell = new TextView(this);
            dayCell.setText(String.valueOf(day));
            dayCell.setGravity(Gravity.CENTER);
            dayCell.setTextSize(14);
            dayCell.setPadding(8, 8, 8, 8);
            dayCell.setBackgroundColor(Color.WHITE);
            
            // Check if this day is completed
            boolean isCompleted = isDayCompleted(day);
            if (isCompleted) {
                dayCell.setBackgroundColor(Color.parseColor("#4CAF50"));
                dayCell.setTextColor(Color.WHITE);
            }

            // Check if this is today
            Calendar today = Calendar.getInstance();
            if (currentMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                currentMonth.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                day == today.get(Calendar.DAY_OF_MONTH)) {
                dayCell.setBackgroundColor(Color.parseColor("#2196F3"));
                dayCell.setTextColor(Color.WHITE);
            }

            final int finalDay = day;
            dayCell.setOnClickListener(v -> {
                if (selectedChain != null) {
                    toggleDay(finalDay);
                } else {
                    Toast.makeText(this, "Please select a chain first", Toast.LENGTH_SHORT).show();
                }
            });
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            dayCell.setLayoutParams(params);
            
            calendarGrid.addView(dayCell);
        }
    }

    private boolean isDayCompleted(int day) {
        if (selectedChain == null || currentMonthEntries == null) {
            return false;
        }

        Calendar targetDate = (Calendar) currentMonth.clone();
        targetDate.set(Calendar.DAY_OF_MONTH, day);

        for (ChainEntry entry : currentMonthEntries) {
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

    private void toggleDay(int day) {
        Calendar targetDate = (Calendar) currentMonth.clone();
        targetDate.set(Calendar.DAY_OF_MONTH, day);
        
        database.toggleEntry(selectedChain.getId(), targetDate.getTime());
        loadMonthEntries();
        updateCalendar();
        updateSummary();
    }

    private void setupSummaryFragment() {
        summaryFragment = new SummaryFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.summary_fragment_container, summaryFragment);
        transaction.commit();
    }

    private void updateSummary() {
        if (summaryFragment != null) {
            summaryFragment.updateSummary(currentMonthEntries);
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
