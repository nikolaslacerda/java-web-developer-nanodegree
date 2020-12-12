package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    private final FileService filesService;
    private final EncryptionService encryptionService;

    private List<File> files;

    public HomeController(FileService filesService, EncryptionService encryptionService) {
        this.filesService = filesService;
        this.encryptionService = encryptionService;
    }

    @PostConstruct
    public void postConstruct() {
        files = new ArrayList<>();
    }

    @GetMapping
    public String getHomeView(Authentication auth, Model model) {

        User user = (User) auth.getDetails();

        files = filesService.getFilesByUserId(user.getUserId());
        model.addAttribute("activeTab", "files");
        setLists(model);
        return "home";
    }

    @PostMapping("/file-upload")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile fileUpload, Authentication auth, Model model) {

        User user = (User) auth.getDetails();
        model.addAttribute("activeTab", "files");

        if (fileUpload.getOriginalFilename().isEmpty()) {
            model.addAttribute("uploadError", "Please select a file to upload");
            setLists(model);
            return "home";
        }

        if (!filesService.isFilenameAvailable(fileUpload.getOriginalFilename(), user.getUserId())) {
            model.addAttribute("uploadError", "File with the same filename already exists!");
            setLists(model);
            return "home";
        }

        File file = new File();

        try {
            file.setFileData(fileUpload.getBytes());
            file.setContentType(fileUpload.getContentType());
            file.setFileName(fileUpload.getOriginalFilename());
            file.setFileSize(fileUpload.getSize() + "");


            file.setUserId(user.getUserId());

            filesService.saveFile(file);

            // reload files list
            files = filesService.getFilesByUserId(user.getUserId());
            model.addAttribute("filesMessage", "File uploaded successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("uploadError", e.getMessage());
        }

        setLists(model);

        return "home";
    }

    @GetMapping("/file-view")
    public void viewFile(@RequestParam("fileId") Integer fileId, HttpServletResponse response) {

        File file = filesService.getFileByFileId(fileId);

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
        try {
            filesService.deleteFile(fileId);

            User user = (User) auth.getDetails();

            files = filesService.getFilesByUserId(user.getUserId());
            model.addAttribute("activeTab", "files");
            model.addAttribute("filesMessage", "File deleted!");

        } catch (Exception e) {
            model.addAttribute("fileError", e.getMessage());
        }

        setLists(model);

        return "home";
    }


    private void setLists(Model model) {
        model.addAttribute("files", files);
    }
}
