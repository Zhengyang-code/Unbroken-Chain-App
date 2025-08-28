package com.example.unbrokenchainapp.utils;

import com.example.unbrokenchainapp.models.ChainEntry;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StreakCalculator {

    public static int calculateCurrentStreak(List<ChainEntry> entries) {
        if (entries.isEmpty()) return 0;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();

        int streak = 0;
        Date currentDate = today;

        while (true) {
            boolean found = false;
            for (ChainEntry entry : entries) {
                if (isSameDay(entry.getDate(), currentDate) && entry.isCompleted()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                break;
            }

            streak++;
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            currentDate = calendar.getTime();
        }

        return streak;
    }

    public static int calculateLongestStreak(List<ChainEntry> entries) {
        if (entries.isEmpty()) return 0;

        int longestStreak = 0;
        int currentStreak = 0;

        // Sort entries by date
        entries.sort((e1, e2) -> e1.getDate().compareTo(e2.getDate()));

        for (ChainEntry entry : entries) {
            if (entry.isCompleted()) {
                currentStreak++;
                longestStreak = Math.max(longestStreak, currentStreak);
            } else {
                currentStreak = 0;
            }
        }

        return longestStreak;
    }

    public static int calculateMonthlyStreak(List<ChainEntry> entries, int year, int month) {
        if (entries.isEmpty()) return 0;

        int longestStreak = 0;
        int currentStreak = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            Date currentDate = calendar.getTime();

            boolean completed = false;
            for (ChainEntry entry : entries) {
                if (isSameDay(entry.getDate(), currentDate) && entry.isCompleted()) {
                    completed = true;
                    break;
                }
            }

            if (completed) {
                currentStreak++;
                longestStreak = Math.max(longestStreak, currentStreak);
            } else {
                currentStreak = 0;
            }
        }

        return longestStreak;
    }

    private static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
} 