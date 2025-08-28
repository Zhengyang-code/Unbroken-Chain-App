package com.example.unbrokenchainapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unbrokenchainapp.R;
import com.example.unbrokenchainapp.database.ChainDatabase;
import com.example.unbrokenchainapp.models.Chain;
import com.example.unbrokenchainapp.models.ChainEntry;
import com.example.unbrokenchainapp.utils.StreakCalculator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SummaryGraphActivity extends AppCompatActivity {

    private ChainDatabase database;
    private Spinner chainSpinner;
    private LinearLayout graphContainer;
    private List<Chain> chains;
    private Chain selectedChain;
    private TextView legendText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_graph);

        database = new ChainDatabase(this);
        chains = new ArrayList<>();

        chainSpinner = findViewById(R.id.chain_spinner);
        graphContainer = findViewById(R.id.graph_container);
        legendText = findViewById(R.id.legend_text);

        legendText.setText("Green: Total Completed | Orange: Longest Streak");

        loadChains();

        chainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < chains.size()) {
                    selectedChain = chains.get(position);
                    loadGraphData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedChain = null;
                graphContainer.removeAllViews();
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
            loadGraphData();
        }
    }

    private void loadGraphData() {
        if (selectedChain == null) {
            graphContainer.removeAllViews();
            return;
        }

        graphContainer.removeAllViews();

        Calendar currentDate = Calendar.getInstance();
        List<MonthStats> monthlyStats = new ArrayList<>();

        // Get data for last 12 months
        for (int i = -11; i <= 0; i++) {
            Calendar monthDate = (Calendar) currentDate.clone();
            monthDate.add(Calendar.MONTH, i);
            
            int year = monthDate.get(Calendar.YEAR);
            int month = monthDate.get(Calendar.MONTH) + 1;
            
            List<ChainEntry> entries = database.getEntriesForMonth(selectedChain.getId(), year, month);
            
            int totalCompleted = 0;
            for (ChainEntry entry : entries) {
                if (entry.isCompleted()) {
                    totalCompleted++;
                }
            }
            
            int longestStreak = StreakCalculator.calculateMonthlyStreak(entries, year, month);
            
            monthlyStats.add(new MonthStats(year, month, totalCompleted, longestStreak));
        }

        createBarGraph(monthlyStats);
    }

    private static class MonthStats {
        int year;
        int month;
        int totalCompleted;
        int longestStreak;

        MonthStats(int year, int month, int totalCompleted, int longestStreak) {
            this.year = year;
            this.month = month;
            this.totalCompleted = totalCompleted;
            this.longestStreak = longestStreak;
        }
    }


    private void createBarGraph(List<MonthStats> stats) {
        int maxValue = 1;
        for (MonthStats stat : stats) {
            maxValue = Math.max(maxValue, Math.max(stat.totalCompleted, stat.longestStreak));
        }

        int maxBarWidth = dpToPx(200); // max height per bar

        for (MonthStats stat : stats) {
            // one row container
            LinearLayout monthColumn = new LinearLayout(this);
            monthColumn.setOrientation(LinearLayout.VERTICAL);
            monthColumn.setPadding(8, 8, 8, 8);

            // green bar
            View totalBar = new View(this);
            int totalWidth = (int) ((float) stat.totalCompleted / maxValue * maxBarWidth);
            totalBar.setBackgroundColor(Color.parseColor("#4CAF50"));
            LinearLayout.LayoutParams totalParams = new LinearLayout.LayoutParams(totalWidth, dpToPx(20));
            totalParams.setMargins(0, 0, 0, 4);
            totalBar.setLayoutParams(totalParams);

            // orange bar
            View streakBar = new View(this);
            int streakWidth = (int) ((float) stat.longestStreak / maxValue * maxBarWidth);
            streakBar.setBackgroundColor(Color.parseColor("#FF9800"));
            LinearLayout.LayoutParams streakParams = new LinearLayout.LayoutParams(streakWidth, dpToPx(20));
            streakParams.setMargins(0, 0, 0, 8);
            streakBar.setLayoutParams(streakParams);

            // month data tag
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM yy", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.set(stat.year, stat.month - 1, 1);
            TextView label = new TextView(this);
            label.setText(monthFormat.format(cal.getTime()) + "  " + stat.totalCompleted + "/" + stat.longestStreak);
            label.setTextSize(12);
            label.setPadding(0, 0, 0, 4);

            // assemble
            monthColumn.addView(totalBar);
            monthColumn.addView(streakBar);
            monthColumn.addView(label);

            // to main container
            graphContainer.addView(monthColumn);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
    }
} 