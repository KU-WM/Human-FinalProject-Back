package com.backend.back.service;

import com.backend.back.dto.AudioDTO;
import com.backend.back.dto.ImageDTO;
import com.backend.back.mapper.AudioMapper;
import com.backend.back.mapper.ImageMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Temp2UserService {

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private AudioMapper audioMapper;

    public void tempToUser(int id, HttpServletRequest request) {
        String tempUserUUID = request.getAttribute("userId").toString();

        List<ImageDTO> tempImages = imageMapper.getTempUserImages(tempUserUUID);

        for(ImageDTO img : tempImages) {
            img.setCreateBy(id);
            int prevId = img.getId();
            imageMapper.saveImage(img);
            List<AudioDTO> tempAudios = audioMapper.getTempUserAudios(tempUserUUID, prevId);
            if (tempAudios != null) {
                for(AudioDTO aud : tempAudios) {
                    aud.setCreateFrom(img.getId());
                    aud.setCreateBy(id);
                    audioMapper.saveAudio(aud);
                    audioMapper.deleteTempAudio(aud.getTempUserUUID());
                }
            }
            imageMapper.deleteTempImage(img.getTempUserUUID());
        }

    }
}
