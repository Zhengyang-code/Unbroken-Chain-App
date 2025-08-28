package com.example.unbrokenchainapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_graph);

        database = new ChainDatabase(this);
        chains = new ArrayList<>();

        chainSpinner = findViewById(R.id.chain_spinner);
        graphContainer = findViewById(R.id.graph_container);

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
        // Add legend
        TextView legendText = new TextView(this);
        legendText.setText("Green: Total Completed | Orange: Longest Streak");
        legendText.setTextSize(12);
        legendText.setTextColor(Color.GRAY);
        legendText.setPadding(0, 0, 0, 16);
        graphContainer.addView(legendText);

        // Find max values for scaling
        int maxTotal = 0;
        int maxStreak = 0;
        for (MonthStats stat : stats) {
            maxTotal = Math.max(maxTotal, stat.totalCompleted);
            maxStreak = Math.max(maxStreak, stat.longestStreak);
        }

        int maxValue = Math.max(maxTotal, maxStreak);
        if (maxValue == 0) maxValue = 1; // Avoid division by zero

        // Create graph bars
        for (MonthStats stat : stats) {
            LinearLayout monthContainer = new LinearLayout(this);
            monthContainer.setOrientation(LinearLayout.VERTICAL);
            monthContainer.setPadding(8, 4, 8, 4);

            // Month label
            TextView monthLabel = new TextView(this);
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.set(stat.year, stat.month - 1, 1);
            monthLabel.setText(monthFormat.format(cal.getTime()));
            monthLabel.setTextSize(10);
            monthLabel.setTextColor(Color.BLACK);
            monthLabel.setGravity(android.view.Gravity.CENTER);
            monthContainer.addView(monthLabel);

            // Graph bars container
            LinearLayout barsContainer = new LinearLayout(this);
            barsContainer.setOrientation(LinearLayout.HORIZONTAL);
            barsContainer.setPadding(0, 4, 0, 0);

            // Total completed bar
            View totalBar = new View(this);
            int totalHeight = (int) ((float) stat.totalCompleted / maxValue * 100);
            totalBar.setBackgroundColor(Color.parseColor("#4CAF50"));
            LinearLayout.LayoutParams totalParams = new LinearLayout.LayoutParams(
                0, totalHeight, 1
            );
            totalParams.setMargins(2, 0, 2, 0);
            totalBar.setLayoutParams(totalParams);
            barsContainer.addView(totalBar);

            // Longest streak bar
            View streakBar = new View(this);
            int streakHeight = (int) ((float) stat.longestStreak / maxValue * 100);
            streakBar.setBackgroundColor(Color.parseColor("#FF9800"));
            LinearLayout.LayoutParams streakParams = new LinearLayout.LayoutParams(
                0, streakHeight, 1
            );
            streakParams.setMargins(2, 0, 2, 0);
            streakBar.setLayoutParams(streakParams);
            barsContainer.addView(streakBar);

            monthContainer.addView(barsContainer);

            // Values text
            TextView valuesText = new TextView(this);
            valuesText.setText(stat.totalCompleted + "/" + stat.longestStreak);
            valuesText.setTextSize(8);
            valuesText.setTextColor(Color.GRAY);
            valuesText.setGravity(android.view.Gravity.CENTER);
            monthContainer.addView(valuesText);

            // Add to main container
            LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
            );
            monthContainer.setLayoutParams(containerParams);
            graphContainer.addView(monthContainer);
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