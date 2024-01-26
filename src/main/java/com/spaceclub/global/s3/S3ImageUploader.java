package com.spaceclub.global.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.spaceclub.global.config.s3.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.spaceclub.global.exception.GlobalExceptionCode.FAIL_FILE_UPLOAD;
import static com.spaceclub.global.exception.GlobalExceptionCode.INVALID_FILE_EXTENSION;

@Component
@RequiredArgsConstructor
public class S3ImageUploader {

    private static final String DOT = ".";

    private static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    private final AmazonS3Client amazonS3Client;

    private final S3Properties s3Properties;

    public String upload(MultipartFile image, S3Folder folder) {
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null) throw new IllegalArgumentException(FAIL_FILE_UPLOAD.toString());

        String key = createKey(originalFilename, folder);
        String bucket = s3Properties.bucket();

        ObjectMetadata objectMetaData = new ObjectMetadata();

        objectMetaData.setContentType(image.getContentType());
        objectMetaData.setContentLength(image.getSize());

        try {
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, key, image.getInputStream(), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new IllegalStateException(FAIL_FILE_UPLOAD.toString());
        }

        return key;
    }

    private String createKey(String originalName, S3Folder folder) {
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());

        int lastDot = originalName.lastIndexOf(DOT);
        String fileName = originalName.substring(0, lastDot);
        String fileExtension = originalName.substring(lastDot + 1);
        validateFileExtension(fileExtension);

        return String.format("%s/%s_%s.%s", folder.getFolder(), fileName, timestamp, fileExtension);
    }

    private void validateFileExtension(String fileExtension) {
        boolean invalidExtension = s3Properties.validExtensions()
                .stream()
                .noneMatch(extension -> extension.equalsIgnoreCase(fileExtension));

        if (invalidExtension) throw new MultipartException(INVALID_FILE_EXTENSION.toString());
    }

    public String getBucketUrl() {
        return s3Properties.url();
    }

}
