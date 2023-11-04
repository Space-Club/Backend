package com.spaceclub.club.domain;

public enum ClubUserRole {

    MANAGER("MANAGER", 1),
    MEMBER("MEMBER", 2);

    private final String role;

    private final int sortPriority;

    ClubUserRole(String role, int sortPriority) {
        this.role = role;
        this.sortPriority = sortPriority;
    }

    public int getSortPriority() {
        return sortPriority;
    }

}
