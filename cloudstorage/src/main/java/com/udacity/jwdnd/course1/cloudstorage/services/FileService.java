package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {

    private final FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public boolean isFilenameAvailable(String fileName, Integer userId) {
        return fileMapper.getFileByFileName(fileName, userId) == null;
    }

    public void saveFile(File file) {
        Integer fileId = fileMapper.saveFile(file);
    }

    public List<File> getFilesByUserId(Integer userId) {
        return fileMapper.getFilesByUserId(userId);
    }

    public File getFileByFileId(Integer fileId) {
        return fileMapper.getFileByFileId(fileId);
    }

    public Integer deleteFile(Integer fileId, Integer userId) {
        return fileMapper.deleteFile(fileId, userId);
    }
}

