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
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    private final AmazonS3Client amazonS3Client;

    @Value("${s3.folder.name.event-poster}")
    private String eventPosterFolder;

    @Value("${s3.folder.name.club-logo}")
    private String clubLogoFolder;

    @Value("${s3.folder.name.user-profile-image}")
    private String userProfileImageFolder;

    public String uploadPosterImage(MultipartFile posterImage) {
        return upload(posterImage, eventPosterFolder);
    }

    public String uploadClubLogoImage(MultipartFile logoImage) {
        return upload(logoImage, clubLogoFolder);
    }

    public String uploadUserProfileImage(MultipartFile userImage) {
        return upload(userImage, userProfileImageFolder);
    }

    private String upload(MultipartFile userImage, String userProfileImageFolder) {
        String originalFilename = userImage.getOriginalFilename();
        if (originalFilename == null) throw new IllegalArgumentException("파일 이름이 존재하지 않습니다.");

        String fileName = createFileName(originalFilename);
        ObjectMetadata objectMetaData = new ObjectMetadata();

        objectMetaData.setContentType(userImage.getContentType());
        objectMetaData.setContentLength(userImage.getSize());

        try {
            amazonS3Client.putObject(
                    new PutObjectRequest(userProfileImageFolder, fileName, userImage.getInputStream(), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new IllegalStateException("파일 업로드에 실패했습니다.");
        }

        return fileName;
    }

    private String createFileName(String originalName) {
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());

        int lastDot = originalName.lastIndexOf(DOT);
        String fileName = originalName.substring(0, lastDot);
        String fileExtension = originalName.substring(lastDot);

        return fileName + "_" + timestamp + fileExtension;
    }

}
