package com.example.diplom.service;

import com.example.diplom.dto.FileDTO;
import com.example.diplom.model.FileEntity;
import com.example.diplom.model.Users;
import com.example.diplom.repository.FileEntityRepository;
import com.example.diplom.repository.UsersRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class FileService {

    private final FileEntityRepository fileEntityRepository;
    private final UsersRepository usersRepository;

    public FileService(FileEntityRepository fileEntityRepository, UsersRepository usersRepository) {
        this.fileEntityRepository = fileEntityRepository;
        this.usersRepository = usersRepository;
    }

    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestHeader("auth-token") String token) {

        token = token.replace("Bearer ", "");
        Optional<Users> user = usersRepository.findUserByToken(token);

        if (user.isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Unauthorized error");
            resp.put("id", 0);
            return new ResponseEntity<>(resp, HttpStatus.UNAUTHORIZED);
        }
        try {
            saveFile(file, user.get().getEmail());
            System.out.println(fileEntityRepository.findAll());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "error input data");
            resp.put("id", 0);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
    }

    public void saveFile(MultipartFile file, String mail) throws IOException {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFilename(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
        fileEntity.setDataOfChange(new Date());
        fileEntity.setData(file.getBytes());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setSize(file.getSize());
        fileEntity.setMailUser(mail);
        fileEntityRepository.save(fileEntity);
    }

    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String token, @RequestParam("filename") String
            filename) {
        Optional<Users> user = usersRepository.findUserByToken(token.replace("Bearer ", ""));
        if (user.isEmpty()) {
            Map<String, Object> errorAnswer = new LinkedHashMap<>();
            errorAnswer.put("message", "Unauthorized error");
            errorAnswer.put("id", 0);
            return new ResponseEntity<>(errorAnswer, HttpStatus.UNAUTHORIZED);
        } else if (filename.isEmpty()) {
            Map<String, Object> errorAnswer = new HashMap<>();
            errorAnswer.put("message", "error input data");
            errorAnswer.put("id", 0);
            return new ResponseEntity<>(errorAnswer, HttpStatus.BAD_REQUEST);
        } else {
            try {
                fileEntityRepository.deleteFileEntitiesByFilenameAndMailUser(filename, user.get().getEmail());
                System.out.println(fileEntityRepository.findAll());
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                Map<String, Object> errorAnswer = new HashMap<>();
                errorAnswer.put("message", "error delete file");
                errorAnswer.put("id", 0);
                return new ResponseEntity<>(errorAnswer, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping("/file")
    public ResponseEntity<?> downLoadFile(@RequestHeader("auth-token") String token, @RequestParam("filename") String filename) {

        Optional<Users> user = usersRepository.findUserByToken(token.replace("Bearer ", ""));
        if (user.isEmpty()) {
            Map<String, Object> errorAnswer = new LinkedHashMap<>();
            errorAnswer.put("message", "Unauthorized error");
            errorAnswer.put("id", 0);
            return new ResponseEntity<>(errorAnswer, HttpStatus.UNAUTHORIZED);
        } else if (filename.isEmpty()) {
            Map<String, Object> errorAnswer = new HashMap<>();
            errorAnswer.put("message", "error input data");
            errorAnswer.put("id", 0);
            return new ResponseEntity<>(errorAnswer, HttpStatus.BAD_REQUEST);
        } else {
            try {
                String mailUser = user.get().getEmail();
                System.out.println(mailUser);
                System.out.println(fileEntityRepository.findAll());
                FileEntity dataFile = fileEntityRepository.findFileByFilenameAndMailUser(filename, mailUser);
                System.out.println(dataFile);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(dataFile.getFileType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dataFile.getFilename() + "\"")
                        .body(new ByteArrayResource(dataFile.getData()));
            } catch (Exception e) {
                Map<String, Object> errorAnswer = new HashMap<>();
                errorAnswer.put("message", "error upload file");
                errorAnswer.put("id", 0);
                return new ResponseEntity<>(errorAnswer, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PutMapping("/file")
    public ResponseEntity<?> reNameFile(@RequestHeader("auth-token") String token
            , @RequestParam("filename") String filename
            , @RequestBody Map<String, String> fileNameRequest) {

        Optional<Users> user = usersRepository.findUserByToken(token.replace("Bearer ", ""));
        if (user.isEmpty()) {
            Map<String, Object> errorAnswer = new LinkedHashMap<>();
            errorAnswer.put("message", "Unauthorized error");
            errorAnswer.put("id", 0);
            return new ResponseEntity<>(errorAnswer, HttpStatus.UNAUTHORIZED);
        } else if (fileNameRequest.isEmpty()) {
            Map<String, Object> errorAnswer = new HashMap<>();
            errorAnswer.put("message", "error input data");
            errorAnswer.put("id", 0);
            return new ResponseEntity<>(errorAnswer, HttpStatus.BAD_REQUEST);
        } else {
            try {
                String newFileName = fileNameRequest.get("filename");
                fileEntityRepository.renameFile(newFileName, user.get().getEmail(), filename);
                System.out.println(fileEntityRepository.findAll());
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                Map<String, Object> errorAnswer = new HashMap<>();
                errorAnswer.put("message", "error upload file");
                errorAnswer.put("id", 0);
                return new ResponseEntity<>(errorAnswer, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listOfFiles(@RequestHeader("auth-token") String token, @RequestParam("limit") int limit) {

        Optional<Users> user = usersRepository.findUserByToken(token.replace("Bearer ", ""));

        if (user.isEmpty()) {
            Map<String, Object> errorAnswer = new LinkedHashMap<>();
            errorAnswer.put("message", "Unauthorized error");
            errorAnswer.put("id", 0);
            return new ResponseEntity<>(errorAnswer, HttpStatus.UNAUTHORIZED);
        } else {
            try {
                Optional<List<FileEntity>> mapList = Optional.of(fileEntityRepository.findAllByMailUser(user.get().getEmail()));
                return new ResponseEntity<>(mapList.get().stream().map(fr -> new FileDTO(fr.getFilename(), fr.getSize(), fr.getDataOfChange()))
                        .limit(limit)
                        .collect(Collectors.toList()), HttpStatus.OK);
            } catch (Exception e) {
                Map<String, Object> errorAnswer = new HashMap<>();
                errorAnswer.put("message", "error getting file list");
                errorAnswer.put("id", 0);
                return new ResponseEntity<>(errorAnswer, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
