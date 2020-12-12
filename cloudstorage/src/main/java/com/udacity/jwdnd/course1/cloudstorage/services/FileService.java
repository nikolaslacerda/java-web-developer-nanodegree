package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FilesMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {

    private final FilesMapper filesMapper;

    public FileService(FilesMapper filesMapper) {
        this.filesMapper = filesMapper;
    }

    public boolean isFilenameAvailable(String fileName, Integer userId) {
        return filesMapper.getFileByFileName(fileName, userId) == null;
    }

    public void saveFile(File file) {
        Integer fileId = filesMapper.saveFile(file);
    }

    public List<File> getFilesByUserId(Integer userId) {
        return filesMapper.getFilesByUserId(userId);
    }

    public File getFileByFileId(Integer fileId) {
        return filesMapper.getFileByFileId(fileId);
    }

    public void deleteFile(Integer fileId) {
        filesMapper.deleteFileByFileId(fileId);
    }
}

