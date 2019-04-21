package com.ayu.audio.service;

import com.ayu.audio.dao.AudioFilesDao;
import com.ayu.audio.domain.StoredFileDetails;
import com.ayu.audio.domain.UserFileDetails;
import com.ayu.audio.dto.AudioFileDownloadDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AudioFileDownloadService {
    private final AudioFilesDao audioFilesDao;

    public AudioFileDownloadService(AudioFilesDao audioFilesDao) {
        this.audioFilesDao = audioFilesDao;
    }

    public AudioFileDownloadDto downloadFile(String fileName, String speakerName) {
        AudioFileDownloadDto audioFileDownloadDto = null;
        List<UserFileDetails> fileDetailsList = audioFilesDao.getFileContentByFileNameOrSpeakerName(fileName, speakerName);
        if (fileDetailsList.size() == 1) {
            audioFileDownloadDto = AudioFileDownloadDto.builder().fileContent(fileDetailsList.get(0).getFileContent()).build();
        } else if (fileDetailsList.size() > 1) {
            List<StoredFileDetails> list = new ArrayList<>();

            fileDetailsList.stream().forEach(
                    fileDetails ->
                            list.add(new StoredFileDetails(
                                    fileDetails.getFileMetadata().getTimeStamp(),
                                    fileDetails.getFileName(),
                                    fileDetails.getFileMetadata().getSpeakerName()))
            )
            ;
            audioFileDownloadDto = AudioFileDownloadDto.builder().storedFiles(list).build();
        }
        return audioFileDownloadDto;
    }
}