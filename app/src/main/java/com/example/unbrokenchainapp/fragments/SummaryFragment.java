package com.example.unbrokenchainapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.unbrokenchainapp.R;
import com.example.unbrokenchainapp.models.ChainEntry;
import com.example.unbrokenchainapp.utils.StreakCalculator;

import java.util.List;

public class SummaryFragment extends Fragment {

    private TextView totalCountText;
    private TextView currentStreakText;
    private TextView longestStreakText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        
        totalCountText = view.findViewById(R.id.total_count_text);
        currentStreakText = view.findViewById(R.id.current_streak_text);
        longestStreakText = view.findViewById(R.id.longest_streak_text);
        
        return view;
    }

    public void updateSummary(List<ChainEntry> entries) {
        if (entries == null) {
            totalCountText.setText("0");
            currentStreakText.setText("0");
            longestStreakText.setText("0");
            return;
        }

        int totalCompleted = 0;
        for (ChainEntry entry : entries) {
            if (entry.isCompleted()) {
                totalCompleted++;
            }
        }

        int currentStreak = StreakCalculator.calculateCurrentStreak(entries);
        int longestStreak = StreakCalculator.calculateLongestStreak(entries);

        totalCountText.setText(String.valueOf(totalCompleted));
        currentStreakText.setText(String.valueOf(currentStreak));
        longestStreakText.setText(String.valueOf(longestStreak));
    }
} 