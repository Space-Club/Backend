package com.spaceclub.club.domain;

public enum ClubUserRole {

    MANAGER(1),
    MEMBER(2);
    private final int sortPriority;

    ClubUserRole(int sortPriority) {
        this.sortPriority = sortPriority;
    }

    public int getSortPriority() {
        return sortPriority;
    }

}
