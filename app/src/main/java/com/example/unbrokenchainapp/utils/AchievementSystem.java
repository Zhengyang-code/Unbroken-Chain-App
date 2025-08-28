package com.example.unbrokenchainapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.unbrokenchainapp.models.ChainEntry;

import java.util.List;

public class AchievementSystem {
    
    private static final String PREFS_NAME = "AchievementPrefs";
    private Context context;
    private SharedPreferences prefs;
    
    public AchievementSystem(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public void checkAchievements(List<ChainEntry> entries, String chainName) {
        int totalCompleted = 0;
        int currentStreak = StreakCalculator.calculateCurrentStreak(entries);
        int longestStreak = StreakCalculator.calculateLongestStreak(entries);
        
        for (ChainEntry entry : entries) {
            if (entry.isCompleted()) {
                totalCompleted++;
            }
        }
        
        // First Day Achievement
        if (totalCompleted == 1 && !isAchievementUnlocked("first_day")) {
            unlockAchievement("first_day", "🎉 First Step!", "You completed your first day!");
        }
        
        // Week Warrior Achievement
        if (currentStreak >= 7 && !isAchievementUnlocked("week_warrior")) {
            unlockAchievement("week_warrior", "🔥 Week Warrior!", "You maintained a 7-day streak!");
        }
        
        // Month Master Achievement
        if (currentStreak >= 30 && !isAchievementUnlocked("month_master")) {
            unlockAchievement("month_master", "👑 Month Master!", "You maintained a 30-day streak!");
        }
        
        // Chain Breaker Achievement
        if (longestStreak >= 50 && !isAchievementUnlocked("chain_breaker")) {
            unlockAchievement("chain_breaker", "💪 Chain Breaker!", "You achieved a 50-day streak!");
        }
        
        // Hundred Club Achievement
        if (totalCompleted >= 100 && !isAchievementUnlocked("hundred_club")) {
            unlockAchievement("hundred_club", "💯 Hundred Club!", "You completed 100 days total!");
        }
    }
    
    private boolean isAchievementUnlocked(String achievementId) {
        return prefs.getBoolean(achievementId, false);
    }
    
    private void unlockAchievement(String achievementId, String title, String message) {
        prefs.edit().putBoolean(achievementId, true).apply();
        Toast.makeText(context, title + "\n" + message, Toast.LENGTH_LONG).show();
    }
    
    public int getUnlockedAchievementsCount() {
        int count = 0;
        String[] achievements = {"first_day", "week_warrior", "month_master", "chain_breaker", "hundred_club"};
        for (String achievement : achievements) {
            if (isAchievementUnlocked(achievement)) {
                count++;
            }
        }
        return count;
    }
} 