package com.example.diplom.model;


import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "files")
public class FileEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String filename;
    private Date dataOfChange;
    private Long size;
    private String mailUser;
    private String fileType;
    @Lob
    private byte[] data;

        public FileEntity() {
    }

    public FileEntity(String filename, Date dataOfChange, Long size, String mailUser, String fileType) {
        this.filename = filename;
        this.dataOfChange = dataOfChange;
        this.size = size;
        this.mailUser = mailUser;
        this.fileType = fileType;
    }

    public FileEntity(String filename, Date dataOfChange, Long size, String mailUser, String fileType, byte[] data) {
        this.filename = filename;
        this.dataOfChange = dataOfChange;
        this.size = size;
        this.mailUser = mailUser;
        this.fileType = fileType;
        this.data = data;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getDataOfChange() {
        return dataOfChange;
    }

    public void setDataOfChange(Date dataOfChange) {
        this.dataOfChange = dataOfChange;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getMailUser() {
        return mailUser;
    }

    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FileEntity{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", dataOfChange=" + dataOfChange +
                ", size=" + size +
                ", mailUser='" + mailUser + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}



