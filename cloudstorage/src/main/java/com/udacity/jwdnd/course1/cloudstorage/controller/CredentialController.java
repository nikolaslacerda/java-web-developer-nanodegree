package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.util.Base64;

@Controller
@RequestMapping("/credentials")
public class CredentialController {

    private final CredentialService credentialService;
    private final EncryptionService encryptionService;

    public CredentialController(CredentialService credentialService, EncryptionService encryptionService) {
        this.credentialService = credentialService;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/credential-save")
    public String saveCredential(@RequestParam(required = false) Integer credentialId, @RequestParam String url, @RequestParam String username, @RequestParam String password
            , Authentication auth, Model model) {

        User user = (User) auth.getDetails();
        Integer userId = user.getUserId();

        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        String encryptedPassword = encryptionService.encryptValue(password, encodedKey);

        Credential credential = new Credential(credentialId, url, username, encodedKey, encryptedPassword, user.getUserId());

        credentialService.saveCredential(credential);

        model.addAttribute("activeTab", "credentials");

        if (credentialId == null) {
            model.addAttribute("credentialsMessage", "Credential added successfully!");
        } else {
            model.addAttribute("credentialsMessage", "Credential updated successfully!");
        }

        model.addAttribute("credentials", this.credentialService.getCredentialsByUserId(userId));
        model.addAttribute("resultSuccess", true);
        return "result";
    }

    @PostMapping("/credential-delete")
    public String deleteCredential(@RequestParam Integer credentialId, Authentication auth, Model model) {

        User user = (User) auth.getDetails();
        Integer userId = user.getUserId();

        int result = credentialService.deleteCredential(credentialId, userId);

        if (result > 0) {
            model.addAttribute("resultSuccess", true);
        } else {
            model.addAttribute("resultError", "Credential ID does not exist.");
        }

        return "result";
    }
}
