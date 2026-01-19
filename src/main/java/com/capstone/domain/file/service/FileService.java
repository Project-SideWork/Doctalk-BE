package com.capstone.domain.file.service;

import com.capstone.domain.file.common.FileMagicType;
import com.capstone.domain.file.common.FileMessages;
import com.capstone.domain.file.common.FileTypes;
import com.capstone.domain.file.dto.FileResponse;
import com.capstone.domain.file.exception.InvalidFileException;
import com.capstone.domain.task.entity.Task;
import com.capstone.domain.task.entity.Version;
import com.capstone.domain.task.repository.TaskRepository;
import com.capstone.domain.task.service.TaskService;
import com.capstone.domain.task.util.VersionUtil;
import com.capstone.global.response.exception.GlobalException;
import com.capstone.global.response.status.ErrorStatus;
import com.capstone.global.security.CustomUserDetails;
import com.mongodb.client.gridfs.model.GridFSFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final GridFsTemplate gridFsTemplate;
    private final TaskRepository taskRepository;

    public FileResponse upload(CustomUserDetails customUserDetails,MultipartFile file) throws IOException {

        if (customUserDetails == null) {
            throw new GlobalException(ErrorStatus.USER_NOT_FOUND);
        }

        if (file.isEmpty()) {
            throw new GlobalException(ErrorStatus.FILE_EMPTY);
        }

        String contentType = file.getContentType();
        if (!FileTypes.SUPPORTED_TYPES(contentType)) {
            throw new GlobalException(ErrorStatus.FILE_NOT_SUPPORTED);
        }
        log.info("<UNK> <UNK> <UNK> <UNK> <UNK> <UNK> <UNK> <UNK> <UNK>");
        validateFileSignature(file, contentType);
        log.info("<UNK> <UNK> <UNK> <UNK> <UNK> <UNK> <UNK> <UNK> <UNK>22222222222");
        // GridFS에 파일 저장
        ObjectId objectId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                contentType
        );

        return FileResponse.from(objectId.toHexString(),file.getOriginalFilename());
    }

    private void validateFileSignature(MultipartFile file, String contentType) throws IOException
    {

        if ("text/plain".equals(contentType)) {
            return;
        }

        byte[] header = new byte[4];
        try (InputStream inputStream = file.getInputStream()) {
            int bytesRead = inputStream.read(header);
            if (bytesRead < 4) {
                throw new GlobalException(ErrorStatus.FILE_NOT_SUPPORTED);
            }
        }

        FileMagicType detectedType = FileMagicType.detect(header);
        if (detectedType == null || !detectedType.matchesContentType(contentType)) {
            throw new GlobalException(ErrorStatus.FILE_NOT_SUPPORTED);
        }
    }

    public ResponseEntity<Resource> download(CustomUserDetails customUserDetails,String fileId) {
        if (customUserDetails == null) {
            throw new GlobalException(ErrorStatus.USER_NOT_FOUND);
        }
        GridFSFile file = gridFsTemplate.findOne(
                new Query(Criteria.where("_id").is(fileId))
        );


        GridFsResource resource = gridFsTemplate.getResource(file);

        if (!resource.exists()){
            throw new GlobalException(ErrorStatus.FILE_NOT_FOUND);
        }

        String contentType = file.getMetadata() != null && file.getMetadata().containsKey("_contentType")
                ? file.getMetadata().get("_contentType").toString()
                : "application/octet-stream";

        String filename = file.getFilename();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    public String delete(CustomUserDetails customUserDetails,String fileId) {
        if (customUserDetails == null) {
            throw new GlobalException(ErrorStatus.USER_NOT_FOUND);
        }
        GridFSFile file = gridFsTemplate.findOne(
                new Query(Criteria.where("_id").is(fileId))
        );
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(fileId)));
        return file.getFilename();
    }

    public ResponseEntity<Resource> getFile(CustomUserDetails customUserDetails,String fileId) {
        if (customUserDetails == null) {
            throw new GlobalException(ErrorStatus.USER_NOT_FOUND);
        }
        GridFSFile gridFsFile = gridFsTemplate.findOne(
                new Query(Criteria.where("_id").is(new ObjectId(fileId)))
        );

        if (gridFsFile == null) {
            throw new GlobalException(ErrorStatus.FILE_NOT_FOUND);
        }

        GridFsResource resource = gridFsTemplate.getResource(gridFsFile);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(gridFsFile.getMetadata().get("_contentType").toString()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + gridFsFile.getFilename() + "\"")
                .body(resource);
    }


}
