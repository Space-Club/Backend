package com.spaceclub.event.service;

import com.spaceclub.event.domain.Event;
import com.spaceclub.global.config.s3.S3Properties;
import com.spaceclub.global.s3.S3Folder;
import com.spaceclub.global.s3.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.spaceclub.event.EventExceptionMessage.POSTER_IMAGE_NOT_NULL;
import static com.spaceclub.event.domain.EventCategory.CLUB;

@Service
@RequiredArgsConstructor
public class EventImageService {

    private final S3ImageUploader imageUploader;

    private final S3Properties s3Properties;

    public Event uploadImage(Event event, MultipartFile posterImage) {
        boolean isPosterImageNotNull = event.getCategory() != CLUB && posterImage == null;
        if (isPosterImageNotNull) throw new IllegalStateException(POSTER_IMAGE_NOT_NULL.toString());

        if (posterImage != null) {
            String posterImageName = imageUploader.upload(posterImage, S3Folder.EVENT_POSTER);
            event = event.registerPosterImage(posterImageName);
        }

        return event;
    }

    public String getUrl() {
        return s3Properties.url();
    }

    public Event processPosterImage(Event event, Event existEvent, MultipartFile posterImage) {
        String posterImageName = getPosterImageName(existEvent, posterImage);
        int participants = existEvent.getParticipants();

        Event updatedEvent = event.registerPosterImage(posterImageName);
        updatedEvent = updatedEvent.registerParticipants(participants);

        return existEvent.update(updatedEvent);
    }

    private String getPosterImageName(Event existEvent, MultipartFile posterImage) {
        if (posterImage == null) {
            return existEvent.getPosterImageName();
        }

        return imageUploader.upload(posterImage, S3Folder.EVENT_POSTER);
    }

}
