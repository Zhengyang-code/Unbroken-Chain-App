package com.example.unbrokenchainapp.utils;

import java.util.Random;

public class MotivationalQuotes {
    
    private static final String[] QUOTES = {
        "🔥 Every day is a new opportunity to build your chain!",
        "💪 Consistency beats perfection every time.",
        "🌟 Small steps, big changes.",
        "🎯 Focus on progress, not perfection.",
        "🚀 You're building something amazing, one day at a time.",
        "💎 Your future self will thank you for today's effort.",
        "🌈 Every completed day is a victory!",
        "⚡ You have the power to change your habits.",
        "🎪 Life is a circus, make it spectacular!",
        "🌱 Growth happens outside your comfort zone.",
        "🎨 Paint your life with the colors of consistency.",
        "⚔️ You are the hero of your own story.",
        "🎭 Every day is a performance, make it count!",
        "🌊 Ride the wave of your momentum.",
        "🎪 Keep the show going, day after day!",
        "💫 You're creating magic with every choice.",
        "🎯 Target set, chain building in progress!",
        "🌟 Shine bright like your streak!",
        "🎪 Life's a stage, and you're the star!",
        "🔥 You're on fire! Keep it burning!"
    };
    
    private static final String[] STREAK_QUOTES = {
        "🔥 {streak} days strong! You're unstoppable!",
        "💪 {streak} days of pure determination!",
        "🌟 {streak} days of building your future!",
        "🎯 {streak} days of focused progress!",
        "🚀 {streak} days of momentum building!",
        "💎 {streak} days of consistent excellence!",
        "⚡ {streak} days of power and progress!",
        "🎪 {streak} days of spectacular consistency!",
        "🌈 {streak} days of colorful achievements!",
        "🌱 {streak} days of growing stronger!"
    };
    
    private static final String[] BREAK_QUOTES = {
        "💔 Chain broken, but you're not! Start again!",
        "🔄 Every setback is a setup for a comeback!",
        "🌟 New day, new opportunity to shine!",
        "🎯 Reset and refocus - you've got this!",
        "🚀 Time to rebuild stronger than ever!",
        "💪 Break the break - start your chain again!",
        "🎪 The show must go on - restart your performance!",
        "🌈 After the rain comes the rainbow - rebuild!",
        "⚡ Power up and start your engine again!",
        "🌱 Every restart is a chance to grow stronger!"
    };
    
    private static final Random random = new Random();
    
    public static String getRandomQuote() {
        return QUOTES[random.nextInt(QUOTES.length)];
    }
    
    public static String getStreakQuote(int streak) {
        String quote = STREAK_QUOTES[random.nextInt(STREAK_QUOTES.length)];
        return quote.replace("{streak}", String.valueOf(streak));
    }
    
    public static String getBreakQuote() {
        return BREAK_QUOTES[random.nextInt(BREAK_QUOTES.length)];
    }
    
    public static String getQuoteForStreak(int currentStreak, int previousStreak) {
        if (currentStreak > previousStreak) {
            return getStreakQuote(currentStreak);
        } else if (currentStreak < previousStreak) {
            return getBreakQuote();
        } else {
            return getRandomQuote();
        }
    }
} 