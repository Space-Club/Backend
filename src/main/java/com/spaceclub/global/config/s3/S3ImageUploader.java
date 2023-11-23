package com.spaceclub.global.config.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.spaceclub.global.config.s3.properties.S3FolderProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class S3ImageUploader {

    private static final String DOT = ".";
    private static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    private final AmazonS3Client amazonS3Client;

    private final S3FolderProperties folderProperties;

    public String uploadPosterImage(MultipartFile posterImage) {
        return upload(posterImage, folderProperties.eventPoster());
    }

    public String uploadClubLogoImage(MultipartFile logoImage) {
        return upload(logoImage, folderProperties.clubLogo());
    }

    public String uploadUserProfileImage(MultipartFile userImage) {
        return upload(userImage, folderProperties.userProfileImage());
    }

    public String uploadClubCoverImage(MultipartFile coverImage) {
        return upload(coverImage, folderProperties.clubCover());
    }

    private String upload(MultipartFile image, String folder) {
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null) throw new IllegalArgumentException("파일 이름이 존재하지 않습니다.");

        String fileName = createFileName(originalFilename);
        ObjectMetadata objectMetaData = new ObjectMetadata();

        objectMetaData.setContentType(image.getContentType());
        objectMetaData.setContentLength(image.getSize());

        try {
            amazonS3Client.putObject(
                    new PutObjectRequest(folder, fileName, image.getInputStream(), objectMetaData)
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
