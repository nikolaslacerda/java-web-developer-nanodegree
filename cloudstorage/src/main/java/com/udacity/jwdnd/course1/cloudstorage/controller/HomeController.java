package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
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
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    private final FileService filesService;
    private final NoteService notesService;
    private final CredentialService credentialsService;
    private final EncryptionService encryptionService;

    private List<File> files;
    private List<Note> notes;
    private List<Credential> credentials;

    public HomeController(FileService filesService, NoteService notesService, CredentialService credentialsService, EncryptionService encryptionService) {
        this.filesService = filesService;
        this.notesService = notesService;
        this.credentialsService = credentialsService;
        this.encryptionService = encryptionService;
    }

    @PostConstruct
    public void postConstruct() {
        files = new ArrayList<>();
        notes = new ArrayList<>();
        credentials = new ArrayList<>();
    }

    @GetMapping
    public String getHomeView(Authentication auth, Model model) {

        User user = (User) auth.getDetails();

        files = filesService.getFilesByUserId(user.getUserId());
        notes = notesService.getNotesByUserId(user.getUserId());
        credentials = credentialsService.getCredentialsByUserId(user.getUserId());
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

    @PostMapping("/note-save")
    public String saveNote(@RequestParam(required = false) Integer noteId, @RequestParam("noteTitle") String noteTitle,
                           @RequestParam("noteDescription") String noteDescription, Authentication auth, Model model) {

        try {
            User user = (User) auth.getDetails();

            Note note = new Note(noteId, noteTitle, noteDescription, user.getUserId());

            notesService.saveNote(note);

            notes = notesService.getNotesByUserId(user.getUserId());
            model.addAttribute("activeTab", "notes");
            if (noteId == null)
                model.addAttribute("notesMessage", "Note added successfully!");
            else
                model.addAttribute("notesMessage", "Note updated successfully!");
        } catch (Exception e) {
            model.addAttribute("notesError", e.getMessage());
        }
        setLists(model);

        return "home";
    }

    @PostMapping("/note-delete")
    public String deleteNote(@RequestParam("noteId") Integer noteId, Authentication auth, Model model) {
        try {
            notesService.deleteNote(noteId);

            User user = (User) auth.getDetails();

            notes = notesService.getNotesByUserId(user.getUserId());
            model.addAttribute("activeTab", "notes");
            model.addAttribute("notesMessage", "Note deleted!");
        } catch (Exception e) {
            model.addAttribute("notesError", e.getMessage());
        }
        setLists(model);

        return "home";
    }

    @PostMapping("/credential-save")
    public String saveCredential(@RequestParam(required = false) Integer credentialId, @RequestParam String url, @RequestParam String username, @RequestParam String password
            , Authentication auth, Model model) {
        try {
            User user = (User) auth.getDetails();

            SecureRandom random = new SecureRandom();
            byte[] key = new byte[16];
            random.nextBytes(key);
            String encodedKey = Base64.getEncoder().encodeToString(key);
            String encryptedPassword = encryptionService.encryptValue(password, encodedKey);

            Credential credential = new Credential(credentialId, url, username, encodedKey, encryptedPassword, user.getUserId());

            credentialsService.saveCredential(credential);

            credentials = credentialsService.getCredentialsByUserId(user.getUserId());
            model.addAttribute("activeTab", "credentials");

            if (credentialId == null)
                model.addAttribute("credentialsMessage", "Credential added successfully!");
            else
                model.addAttribute("credentialsMessage", "Credential updated successfully!");
        } catch (Exception e) {
            model.addAttribute("credentialsError", e.getMessage());
        }
        setLists(model);

        return "home";
    }

    @PostMapping("/credential-delete")
    public String deleteCredential(@RequestParam Integer credentialId, Authentication auth, Model model) {
        try {
            credentialsService.deleteCredential(credentialId);

            User user = (User) auth.getDetails();

            credentials = credentialsService.getCredentialsByUserId(user.getUserId());
            model.addAttribute("activeTab", "credentials");
            model.addAttribute("credentialsMessage", "Credential deleted!");
        } catch (Exception e) {
            model.addAttribute("credentialsError", e.getMessage());
        }
        setLists(model);

        return "home";

    }

    private void setLists(Model model) {
        model.addAttribute("files", files);
        model.addAttribute("notes", notes);
        for (Credential credential : credentials) {
            credential.setDecryptedPassword(encryptionService.decryptValue(credential.getPassword(), credential.getKey()));
        }

        model.addAttribute("credentials", credentials);
    }
}
