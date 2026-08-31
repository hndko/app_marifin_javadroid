package com.example.app_marifin_javadroid.presentation.onboarding;

public class OnboardingSlide {

    private final String title;
    private final String description;
    private final int iconRes;

    public OnboardingSlide(String title, String description, int iconRes) {
        this.title = title;
        this.description = description;
        this.iconRes = iconRes;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getIconRes() { return iconRes; }
}
