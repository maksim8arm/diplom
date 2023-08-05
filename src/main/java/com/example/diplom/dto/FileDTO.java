package com.example.diplom.dto;

import java.util.Date;

public class FileDTO {

    private String filename;
    private Long size;
    private Date dataOfChange;

    public FileDTO() {
    }

    public FileDTO(String filename, Long size, Date dataOfChange) {
        this.filename = filename;
        this.size = size;
        this.dataOfChange = dataOfChange;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getDataOfChange() {
        return dataOfChange;
    }

    public void setDataOfChange(Date dataOfChange) {
        this.dataOfChange = dataOfChange;
    }
}
