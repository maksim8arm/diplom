package com.example.diplom.controller;

import com.example.diplom.dto.FileDTO;
import com.example.diplom.dto.LoginPass;
import com.example.diplom.model.FileEntity;
import com.example.diplom.model.Users;
import com.example.diplom.repository.FileEntityRepository;
import com.example.diplom.repository.UsersRepository;
import com.example.diplom.service.FileService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.*;
import java.util.stream.Collectors;

@EnableWebMvc
@Transactional
@RestController
@RequestMapping("/")
public class Controller {

    private final FileService fileService;
    private final UsersRepository usersRepository;
    private final FileEntityRepository fileEntityRepository;


    public Controller(FileService fileService, UsersRepository usersRepository, FileEntityRepository fileEntityRepository) {
        this.fileService = fileService;
        this.usersRepository = usersRepository;
        this.fileEntityRepository = fileEntityRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginPass loginPass) {
        Users user = usersRepository.findByEmailAndPassword(loginPass.getLogin(), loginPass.getPassword());

        if (user != null) {
            Map<String, String> map = new LinkedHashMap<>();
            String token = user.getEmail() + "_" + new Date();
            user.setToken(token);
            map.put("auth-token", token);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            Map<String, String> errorAnswer = new LinkedHashMap<>();
            errorAnswer.put("message", "Bad Request");
            errorAnswer.put("id", "0");
            return new ResponseEntity<>(errorAnswer, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
        public ResponseEntity<?> loginOut(@RequestHeader("auth-token") String token) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/file")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
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
            fileService.save(file, user.get().getEmail());
            System.out.println(fileEntityRepository.findAll());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "error input data");
            resp.put("id", 0);
            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/file")
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
    public ResponseEntity<?> downLoadFile(@RequestHeader("auth-token") String
                                                  token, @RequestParam("filename") String filename) {

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
                FileEntity  dataFile = fileEntityRepository.findFileByFilenameAndMailUser(filename, mailUser);
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
                fileEntityRepository.renameFile(newFileName, user.get().getEmail());
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

        Optional<List<FileEntity>> mapList = Optional.of(fileEntityRepository.findAll());

        Optional<Users> user = usersRepository.findUserByToken(token.replace("Bearer ", ""));
        if (user.isEmpty()) {
            Map<String, Object> errorAnswer = new LinkedHashMap<>();
            errorAnswer.put("message", "Unauthorized error");
            errorAnswer.put("id", 0);
            return new ResponseEntity<>(errorAnswer, HttpStatus.UNAUTHORIZED);
        } else {
            try {
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