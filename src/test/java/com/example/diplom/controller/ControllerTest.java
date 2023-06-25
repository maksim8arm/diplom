package com.example.diplom.controller;

import com.example.diplom.model.FileEntity;
import com.example.diplom.model.Users;
import com.example.diplom.repository.FileEntityRepository;
import com.example.diplom.repository.UsersRepository;
import com.example.diplom.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = {ControllerTest.Initializer.class})
class ControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FileService fileService;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private FileEntityRepository fileEntityRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @InjectMocks
    private final Controller controller = new Controller(
            fileService, usersRepository, fileEntityRepository
    );

    @Container
    static PostgreSQLContainer<?> postConteiner = (PostgreSQLContainer<?>) new PostgreSQLContainer<>("postgres:15.2")
            .withDatabaseName("mydb")
            .withUsername("myuser")
            .withPassword("mypass")
            .withInitScript("db.sql");

    @BeforeEach
    void init() {
        postConteiner.start();
    }

    @AfterEach
    void endInit() {
        usersRepository.deleteAll();
        fileEntityRepository.deleteAll();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postConteiner.getJdbcUrl(),
                    "spring.datasource.username=" + postConteiner.getUsername(),
                    "spring.datasource.password=" + postConteiner.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }


    @Test
    void login_FoundUser() throws Exception {

        Users user = new Users("email", "123", null);
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        Map<String, String> map1 = new HashMap<>();
        map1.put("login", "email");
        map1.put("password", "123");

        mockMvc
                .perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(map1)))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void login_UserNotFoundUser() throws Exception {
        Users user = new Users("email", "123", null);
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        Map<String, String> map1 = new HashMap<>();
        map1.put("login", "email");
        map1.put("password", "1234");

        mockMvc
                .perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(map1)))
                .andDo(print())
                .andExpect(status().is4xxClientError());

    }


    @Test
    void loginOut() throws Exception {

        mockMvc
                .perform(post("/logout")
                        .header("auth-token", "token"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void upload() throws Exception {
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token")).andDo(print()).andExpect(status().isOk());

    }

    @Test
    void upload_ErrorInputData() throws Exception {
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").header("auth-token", "token")).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    void upload_UnauthorizedError() throws Exception {
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "mistakeToken")).andDo(print()).andExpect(status().is4xxClientError());
    }


    @Test
    void deleteFile() throws Exception {
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//        file upload
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token")).andDo(print()).andExpect(status().isOk());
        String req = Objects.requireNonNull(fileEntityRepository.findFileEntitiesByMailUser("email")).getFilename();
        Assert.assertEquals("file.txt", req);
//          file delete
        mockMvc.perform(delete("/file").header("auth-token", "token").param("filename", "file.txt")).andDo(print()).andExpect(status().isOk());
//          no file in BD
        Optional<FileEntity> reqFile = fileEntityRepository.findFileByFilename(sampleFile.getName());
        Assert.assertEquals(Optional.empty(), reqFile);
    }

    @Test
    void deleteFile_ErrorInputData() throws Exception {
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").header("auth-token", "token")).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    void deleteFile_UnauthorizedError() throws Exception {
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token1")).andDo(print()).andExpect(status().is4xxClientError());
    }

//    @Test500
//    void deleteFile_ErrorDeletFile() throws Exception {
//        Users user = new Users("email", "123", "token");
//        Users user2 = new Users("email2", "1234", null);
//        usersRepository.save(user);
//        usersRepository.save(user2);
//
//        String text = "test";
//        MockMultipartFile sampleFile = new MockMultipartFile(
//                "file",
//                "file.txt",
//                "text/plain",
//                text.getBytes());
//
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
////        file upload
//        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token")).andDo(print()).andExpect(status().isOk());
//        String req = Objects.requireNonNull(fileEntityRepository.findFileEntitiesByMailUser("email").orElse(null)).getFilename();
//        Assert.assertEquals("file.txt", req);
//        System.out.println(req);
////          file delete
//        mockMvc.perform(delete("/file").header("auth-token", "token").param("filename", "file1.txt")).andDo(print()).andExpect(status().is4xxClientError());
//
//    }

    @Test
    void downLoadFile() throws Exception {
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token")).andDo(print()).andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/file").header("auth-token", "token").param("filename", "file.txt")).andDo(print()).andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println(content);

        Assert.assertEquals("test", content);
    }

    @Test
    void downLoadFile_ErrorInputData() throws Exception {
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token")).andDo(print()).andExpect(status().isOk());

        mockMvc.perform(get("/file").header("auth-token", "token")).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    void downLoadFile_UnauthorizedError() throws Exception {
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token")).andDo(print()).andExpect(status().isOk());

        mockMvc.perform(get("/file").header("auth-token", "token1").param("filename", "file.txt")).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    void downLoadFile_ErrorUploadFile() throws Exception {
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token")).andDo(print()).andExpect(status().isOk());

        mockMvc.perform(get("/file").header("auth-token", "token").param("filename", "file1.txt")).andDo(print()).andExpect(status().is5xxServerError());

    }

    @Test
    void reNameFile() throws Exception{
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token")).andDo(print()).andExpect(status().isOk());

        Map<String, String> map1 = new HashMap<>();
        map1.put("name", "string.txt");

        mockMvc.perform(put("/file").header("auth-token", "token").param("filename", "file.txt").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsBytes(map1))).andDo(print()).andExpect(status().isOk());
    }


    @Test
    void reNameFile_ErrorInputData() throws Exception{
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token")).andDo(print()).andExpect(status().isOk());

        Map<String, String> map1 = new HashMap<>();
        map1.put("name", "string.txt");

        mockMvc.perform(put("/file").header("auth-token", "token").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsBytes(map1))).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    void reNameFile_UnauthorizedError() throws Exception{
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token")).andDo(print()).andExpect(status().isOk());

        Map<String, String> map1 = new HashMap<>();
        map1.put("name", "string.txt");

        mockMvc.perform(put("/file").header("auth-token", "token1").param("filename", "file.txt").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsBytes(map1))).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    void listOfFiles() throws Exception{
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token")).andDo(print()).andExpect(status().isOk());

        mockMvc.perform(get("/list").header("auth-token", "token").param ("limit", "1")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void listOfFiles_ErrorInputData() throws Exception{
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token")).andDo(print()).andExpect(status().isOk());

        mockMvc.perform(get("/list").header("auth-token", "token")).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    void listOfFiles_UnauthorizedError() throws Exception{
        Users user = new Users("email", "123", "token");
        Users user2 = new Users("email2", "1234", null);
        usersRepository.save(user);
        usersRepository.save(user2);

        String text = "test";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                "file.txt",
                "text/plain",
                text.getBytes());

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file").file(sampleFile).header("auth-token", "token")).andDo(print()).andExpect(status().isOk());

        mockMvc.perform(get("/list").header("auth-token", "token1").param ("limit", "1")).andDo(print()).andExpect(status().is4xxClientError());
    }

}