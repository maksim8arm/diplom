package com.example.diplom.service;

import com.example.diplom.model.FileEntity;
import com.example.diplom.repository.FileEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Transactional
@Service
public class FileService {

    private final FileEntityRepository fileEntityRepository;

    @Autowired
    public FileService(FileEntityRepository fileEntityRepository){
        this.fileEntityRepository = fileEntityRepository;
    }

    public void save (MultipartFile file, String mail) throws IOException{
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFilename(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
        fileEntity.setDataOfChange(new Date());
        fileEntity.setData(file.getBytes());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setSize(file.getSize());
        fileEntity.setMailUser(mail);

        fileEntityRepository.save(fileEntity);
    }
}
