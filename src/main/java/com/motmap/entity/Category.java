package com.motmap.entity;

public enum Category {
    KOREAN("한식"),
    CHINESE("중식"),
    JAPANESE("일식"),
    WESTERN("양식"),
    CAFE("카페"),
    ETC("기타");
    
    private final String displayName;
    
    Category(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
