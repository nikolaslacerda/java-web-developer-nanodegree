package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public List<Note> getNotesByUserId(Integer userId) {
        return noteMapper.getNotesByUserId(userId);
    }

    public Integer saveNote(Note note) {
        Integer noteId = note.getNoteId();
        if (noteId == null) {
            noteId = noteMapper.saveNote(note);
        } else {
            noteMapper.updateNote(note);
        }
        return noteId;
    }

    public Integer deleteNote(Integer noteId, Integer userId) {
        return noteMapper.deleteNote(noteId, userId);
    }
}
