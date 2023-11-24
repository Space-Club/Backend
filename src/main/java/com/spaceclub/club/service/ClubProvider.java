package com.spaceclub.club.service;

import com.spaceclub.club.service.vo.ClubInfo;

import java.util.List;

public interface ClubProvider {

    List<ClubInfo> getClubs(Long userId);

    int registeredClubCount(Long userId);

}
