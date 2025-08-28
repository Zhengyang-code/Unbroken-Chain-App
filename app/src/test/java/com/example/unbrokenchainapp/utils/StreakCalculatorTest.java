package com.example.unbrokenchainapp.utils;

import com.example.unbrokenchainapp.models.ChainEntry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class StreakCalculatorTest {

    // Test: Three consecutive check-in days should return "3 consecutive days"
    @Test
    public void testCalculateStreak_withConsecutiveDays() {
        List<ChainEntry> entries = new ArrayList<>();
        entries.add(new ChainEntry("2025-08-22"));
        entries.add(new ChainEntry("2025-08-23"));
        entries.add(new ChainEntry("2025-08-24"));

        int streak = StreakCalculator.calculateStreak(entries);
        assertEquals(3, streak);
    }

    // Test: Check-in records with gaps in between should not be counted towards consecutive days
    @Test
    public void testCalculateStreak_withGapsInDates() {
        List<ChainEntry> entries = new ArrayList<>();
        entries.add(new ChainEntry("2025-08-22"));
        entries.add(new ChainEntry("2025-08-24")); // Missed the 23rd

        int streak = StreakCalculator.calculateStreak(entries);
        assertEquals(1, streak); // Should only count as 1 day, not consecutive
    }

    // Test: An empty check-in record list should return 0
    @Test
    public void testCalculateStreak_emptyList() {
        List<ChainEntry> entries = new ArrayList<>();
        int streak = StreakCalculator.calculateStreak(entries);
        assertEquals(0, streak);
    }

    // Test: A single check-in record should return 1
    @Test
    public void testCalculateStreak_singleEntry() {
        List<ChainEntry> entries = new ArrayList<>();
        entries.add(new ChainEntry("2025-08-24"));
        int streak = StreakCalculator.calculateStreak(entries);
        assertEquals(1, streak);
    }
}

