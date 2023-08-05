package com.example.diplom.controller;


import com.example.diplom.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Map;

@EnableWebMvc
@Transactional
@RestController
@RequestMapping("/")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/file")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestHeader("auth-token") String token) {
        return fileService.uploadFile(file, token);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String token, @RequestParam("filename") String
            filename) {
        return fileService.deleteFile(token, filename);
    }

    @GetMapping("/file")
    public ResponseEntity<?> downLoadFile(@RequestHeader("auth-token") String token, @RequestParam("filename") String filename) {
        return fileService.downLoadFile(token, filename);
    }

    @PutMapping("/file")
    public ResponseEntity<?> reNameFile(@RequestHeader("auth-token") String token
            , @RequestParam("filename") String filename
            , @RequestBody Map<String, String> fileNameRequest) {

        return fileService.reNameFile(token, filename, fileNameRequest);

    }

    @GetMapping("/list")
    public ResponseEntity<?> listOfFiles(@RequestHeader("auth-token") String token, @RequestParam("limit") int limit) {

        return fileService.listOfFiles(token, limit);

    }

}
