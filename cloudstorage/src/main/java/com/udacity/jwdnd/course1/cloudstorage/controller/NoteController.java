package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/note-save")
    public String saveNote(@RequestParam(required = false) Integer noteId, @RequestParam("noteTitle") String noteTitle,
                           @RequestParam("noteDescription") String noteDescription, Authentication auth, Model model) {

        User user = (User) auth.getDetails();

        Note note = new Note(noteId, noteTitle, noteDescription, user.getUserId());

        noteService.saveNote(note);

        model.addAttribute("activeTab", "notes");

        model.addAttribute("notes", this.noteService.getNotesByUserId(user.getUserId()));
        model.addAttribute("resultSuccess", true);
        return "result";
    }

    @PostMapping("/note-delete")
    public String deleteNote(@RequestParam("noteId") Integer noteId, Authentication auth, Model model) {
        User user = (User) auth.getDetails();
        Integer userId = user.getUserId();

        int result = noteService.deleteNote(noteId, userId);
        if (result > 0) {
            model.addAttribute("resultSuccess", true);
        } else {
            model.addAttribute("resultError", "Note ID does not exist.");
        }

        return "result";
    }

}
