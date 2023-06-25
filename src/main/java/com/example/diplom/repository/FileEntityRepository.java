package com.example.diplom.repository;

import com.example.diplom.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FileEntityRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findFileByFilename(String fileName);

    @Transactional
    FileEntity findFileEntitiesByMailUser(String mailUser);

    @Query(value = ("SELECT * FROM files WHERE filename = ?1 AND mail_user = ?2"), nativeQuery = true)
    FileEntity findFileByFilenameAndMailUser(String filename, String mail);

    @Modifying
    @Transactional
    void deleteFileEntitiesByFilenameAndMailUser(String filename, String mail);

    @Modifying
    @Transactional
    @Query(value = ("UPDATE files SET filename = ?1 WHERE mail_user =?2"), nativeQuery = true)
    void renameFile(String newFileName, String mail);


}
