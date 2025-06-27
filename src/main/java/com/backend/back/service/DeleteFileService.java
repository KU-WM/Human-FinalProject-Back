package com.backend.back.service;

import com.backend.back.dto.AudioDTO;
import com.backend.back.mapper.AudioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class DeleteFileService {

    @Autowired
    private AudioMapper audioMapper;


    public void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("File Delete Success");
            }
            else {
                System.out.println("File Delete Failed");
            }
        }
        else {
            System.out.println("File is not Exist");
        }
    }

    public void deleteAudioFiles(int id) {
        List<AudioDTO> audios = audioMapper.getImageAudios(id);

        for(AudioDTO audio : audios) {
            audioMapper.deleteAudio(audio.getId());
            deleteFile(audio.getSavePath());
        }
    }

    public void deleteTempAudioFiles(int id) {
        List<AudioDTO> audios = audioMapper.getTempImageAudios(id);

        for(AudioDTO audio : audios) {
            audioMapper.deleteTempAudioFromId(audio.getId());
            deleteFile(audio.getSavePath());
        }
    }
}
