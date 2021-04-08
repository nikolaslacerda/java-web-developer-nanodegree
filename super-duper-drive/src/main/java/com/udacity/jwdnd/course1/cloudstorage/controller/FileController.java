package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/file-upload")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile fileUpload, Authentication auth, Model model) throws IOException {
        User user = (User) auth.getDetails();
        Integer userId = user.getUserId();

        if (fileUpload.getOriginalFilename().isEmpty()) {
            model.addAttribute("resultError", "Please select a file to upload");
            return "result";
        }

        if (!fileService.isFilenameAvailable(fileUpload.getOriginalFilename(), user.getUserId())) {
            model.addAttribute("resultError", "File with the same filename already exists!");
            return "result";
        }

        File file = new File();

        file.setFileData(fileUpload.getBytes());
        file.setContentType(fileUpload.getContentType());
        file.setFileName(fileUpload.getOriginalFilename());
        file.setFileSize(fileUpload.getSize() + "");


        file.setUserId(user.getUserId());

        fileService.saveFile(file);

        model.addAttribute("resultSuccess", true);
        model.addAttribute("files", this.fileService.getFilesByUserId(userId));

        return "result";
    }

    @GetMapping("/file-view")
    public void viewFile(@RequestParam("fileId") Integer fileId, HttpServletResponse response) {
        File file = fileService.getFileByFileId(fileId);

        if (file != null) {
            try {
                response.setContentType(file.getContentType());
                response.setContentLength(Integer.parseInt(file.getFileSize()));
                response.setHeader("Content-Disposition", "attachment; filename=" + file.getFileName());

                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(file.getFileData());

                response.flushBuffer();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/file-delete")
    public String deleteFile(@RequestParam("fileId") Integer fileId, Authentication auth, Model model) {
        User user = (User) auth.getDetails();
        Integer userId = user.getUserId();

        int result = this.fileService.deleteFile(fileId, userId);
        if (result > 0) {
            model.addAttribute("resultSuccess", true);
        } else {
            model.addAttribute("resultError", "File ID does not exist.");
        }
        return "result";
    }
}
