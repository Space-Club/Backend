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

    public static final String SLASH = "/";

    private final AmazonS3Client amazonS3Client;

    @Value("${s3.bucket.name}")
    private String s3BucketName;

    @Value("${s3.bucket.club-name}")
    private String clubS3BucketName;

    public String uploadImage(MultipartFile poster) throws IOException {
        String newFileName = createFileName(poster.getOriginalFilename());

        ObjectMetadata objectMetaData = new ObjectMetadata();

        objectMetaData.setContentType(poster.getContentType());
        objectMetaData.setContentLength(poster.getSize());

        amazonS3Client.putObject(
                new PutObjectRequest(s3BucketName, newFileName, poster.getInputStream(), objectMetaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        return getSavedFileName(poster);
    }

    public String uploadClubThumbnail(MultipartFile thumbnail) throws IOException {
        String newFileName = createFileName(thumbnail.getOriginalFilename());

        ObjectMetadata objectMetaData = new ObjectMetadata();

        objectMetaData.setContentType(thumbnail.getContentType());
        objectMetaData.setContentLength(thumbnail.getSize());

        amazonS3Client.putObject(
                new PutObjectRequest(clubS3BucketName, newFileName, thumbnail.getInputStream(), objectMetaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        return getSavedFileName(thumbnail);
    }

    private String getSavedFileName(MultipartFile poster) {
        String fullImageUrl = amazonS3Client.getUrl(s3BucketName, poster.getOriginalFilename()).toString();
        String[] parts = fullImageUrl.split(SLASH);

        return parts[parts.length - 1];
    }


    private String createFileName(String originalName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        int lastDot = originalName.lastIndexOf(DOT);
        String fileName = originalName.substring(0, lastDot);
        String fileExtension = originalName.substring(lastDot);

        return fileName + "_" + timestamp + fileExtension;
    }

}
