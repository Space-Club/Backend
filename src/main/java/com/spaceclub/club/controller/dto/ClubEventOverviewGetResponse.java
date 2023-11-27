package com.spaceclub.club.controller.dto;

import com.spaceclub.event.service.vo.ClubEventOverviewGetInfo;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
public record ClubEventOverviewGetResponse(Long id,
                                           EventInfoResponse eventInfo,
                                           ClubInfoResponse clubInfo,
                                           ManagerInfoResponse managerInfo) {

    public static ClubEventOverviewGetResponse from(ClubEventOverviewGetInfo clubEventOverviewGetInfo) {
        return new ClubEventOverviewGetResponse(
                clubEventOverviewGetInfo.id(),
                new EventInfoResponse(
                        clubEventOverviewGetInfo.title(),
                        clubEventOverviewGetInfo.posterImageUrl(),
                        clubEventOverviewGetInfo.location(),
                        clubEventOverviewGetInfo.startDate(),
                        clubEventOverviewGetInfo.startTime(),
                        clubEventOverviewGetInfo.endDate(),
                        clubEventOverviewGetInfo.endTime(),
                        clubEventOverviewGetInfo.openStatus(),
                        isEventEnded(clubEventOverviewGetInfo)
                ),
                new ClubInfoResponse(
                        clubEventOverviewGetInfo.clubName(),
                        clubEventOverviewGetInfo.clubLogoImageUrl()
                ),
                new ManagerInfoResponse(
                        clubEventOverviewGetInfo.managerName(),
                        clubEventOverviewGetInfo.managerProfileImageUrl()
                )
        );
    }

    private static boolean isEventEnded(ClubEventOverviewGetInfo clubEventOverviewGetInfo) {
        if (clubEventOverviewGetInfo.endDate() == null) { // 공연, 홍보 -> 시작만 받는다
            return LocalDateTime.now().isAfter(clubEventOverviewGetInfo.startDate().atTime(clubEventOverviewGetInfo.startTime()));
        }

        // 모집공고, 클럽일정 -> endDateTime 기준으로
        return LocalDateTime.now().isAfter(clubEventOverviewGetInfo.endDate().atTime(clubEventOverviewGetInfo.endTime()));
    }

    private record EventInfoResponse(
            String title,
            String posterImageUrl,
            String location,
            LocalDate startDate,
            LocalTime startTime,
            LocalDate endDate,
            LocalTime endTime,
            String openStatus,
            boolean isEnded
    ) {

    }

    private record ClubInfoResponse(
            String name,
            String logoImageUrl
    ) {

    }

    private record ManagerInfoResponse(
            String name,
            String profileImageUrl
    ) {

    }

}
