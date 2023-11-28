package com.spaceclub.global.config.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.spaceclub.global.config.s3.properties.S3FolderProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.spaceclub.global.exception.GlobalExceptionCode.FAIL_FILE_UPLOAD;
import static com.spaceclub.global.exception.GlobalExceptionCode.INVALID_FILE_EXTENSION;

@Component
@RequiredArgsConstructor
public class S3ImageUploader {

    private static final String DOT = ".";

    private static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    public static final String UNDERSCORE = "_";

    private static final List<String> validExtensions = List.of("jpeg", "jpg", "png");

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
        if (originalFilename == null) throw new IllegalArgumentException(FAIL_FILE_UPLOAD.toString());

        String fileName = createFileName(originalFilename);
        ObjectMetadata objectMetaData = new ObjectMetadata();

        objectMetaData.setContentType(image.getContentType());
        objectMetaData.setContentLength(image.getSize());

        try {
            amazonS3Client.putObject(
                    new PutObjectRequest(folder, fileName, image.getInputStream(), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new IllegalStateException(FAIL_FILE_UPLOAD.toString());
        }

        return fileName;
    }

    private String createFileName(String originalName) {
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());

        int lastDot = originalName.lastIndexOf(DOT);
        String fileName = originalName.substring(0, lastDot);
        String fileExtension = originalName.substring(lastDot + 1);
        validateFileExtension(fileExtension);

        return fileName + UNDERSCORE + timestamp + DOT + fileExtension;
    }

    private void validateFileExtension(String fileExtension) {
        boolean invalidExtension = validExtensions
                .stream()
                .noneMatch(extension -> extension.equalsIgnoreCase(fileExtension));

        if (invalidExtension) throw new MultipartException(INVALID_FILE_EXTENSION.toString());
    }

}
