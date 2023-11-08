package com.spaceclub.global;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class S3ImageUploader {

    private static final String DOT = ".";

    private final AmazonS3Client amazonS3Client;

    @Value("${s3.folder.name.event-poster}")
    private String eventPosterFolder;

    @Value("${s3.folder.name.club-logo}")
    private String clubLogoFolder;

    public String uploadPosterImage(MultipartFile posterImage) throws IOException {
        String fileName = createFileName(posterImage.getOriginalFilename());

        ObjectMetadata objectMetaData = new ObjectMetadata();

        objectMetaData.setContentType(posterImage.getContentType());
        objectMetaData.setContentLength(posterImage.getSize());

        amazonS3Client.putObject(
                new PutObjectRequest(eventPosterFolder, fileName, posterImage.getInputStream(), objectMetaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        return fileName;
    }

    public String uploadClubLogoImage(MultipartFile logoImage) throws IOException {
        String fileName = createFileName(logoImage.getOriginalFilename());

        ObjectMetadata objectMetaData = new ObjectMetadata();

        objectMetaData.setContentType(logoImage.getContentType());
        objectMetaData.setContentLength(logoImage.getSize());

        amazonS3Client.putObject(
                new PutObjectRequest(clubLogoFolder, fileName, logoImage.getInputStream(), objectMetaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        return fileName;
    }

    private String createFileName(String originalName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        int lastDot = originalName.lastIndexOf(DOT);
        String fileName = originalName.substring(0, lastDot);
        String fileExtension = originalName.substring(lastDot);

        return fileName + "_" + timestamp + fileExtension;
    }

}
