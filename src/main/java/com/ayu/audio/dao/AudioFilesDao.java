package com.ayu.audio.dao;

import com.ayu.audio.domain.FileMetadata;
import com.ayu.audio.domain.StoredFileDetails;
import com.ayu.audio.domain.UserFileDetails;
import com.ayu.audio.exception.FileNotAvailableException;
import com.ayu.audio.repository.AudioFileUploadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AudioFilesDao {
    private AudioFileUploadRepository audioFileUploadRepository;

    public AudioFilesDao(AudioFileUploadRepository audioFileUploadRepository) {
        this.audioFileUploadRepository = audioFileUploadRepository;
    }


    public boolean insertUploadedFile(MultipartFile file, String speakerName, Timestamp timestamp) throws IOException {
        FileMetadata fileMetadata = new FileMetadata();
        UserFileDetails userFileDetails = new UserFileDetails();
        userFileDetails.setFileName(file.getOriginalFilename());
        fileMetadata.setSpeakerName(speakerName);
        fileMetadata.setTimeStamp(timestamp);
        userFileDetails.setFileMetadata(fileMetadata);
        userFileDetails.setFileContent(file.getBytes());
        fileMetadata.setUserFileDetails(userFileDetails);
        return audioFileUploadRepository.saveAndFlush(userFileDetails) != null;
    }

    public List<UserFileDetails> getFileContentByFileName(String fileName) {
        return audioFileUploadRepository.findAllByFileName(fileName);
    }

    public List<StoredFileDetails> getFileMetadataByFileName(String fileName) {

        return audioFileUploadRepository.findAllByFileName(fileName)
                .stream()
                .map(
                    userFileDetail -> new StoredFileDetails(
                            userFileDetail.getFileMetadata().getTimeStamp(),
                            userFileDetail.getFileName(),
                            userFileDetail.getFileMetadata().getSpeakerName(),
                            userFileDetail.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteFileByFileName(String fileName) {
        return audioFileUploadRepository.deleteByFileName(fileName) != null;
    }

    public UserFileDetails getFileContentByFileId(String fileId) throws FileNotAvailableException {
        return audioFileUploadRepository.findById(Long.valueOf(fileId))
                .orElseThrow(() -> new FileNotAvailableException(String.format("The requested file with ID : %s has not been uploaded yet.", fileId)));
    }
}
