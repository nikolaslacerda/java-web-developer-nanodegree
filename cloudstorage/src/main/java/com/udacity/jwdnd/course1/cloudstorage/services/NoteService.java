package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NotesMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NotesMapper notesMapper;

    public NoteService(NotesMapper notesMapper) {
        this.notesMapper = notesMapper;
    }

    public List<Note> getNotesByUserId(Integer userId) {
        return notesMapper.getNotesByUserId(userId);
    }

    public Integer saveNote(Note note) {
        Integer noteId = note.getNoteId();
        if (noteId == null) {
            noteId = notesMapper.saveNote(note);
        } else {
            notesMapper.updateNote(note);
        }
        return noteId;
    }

    public void deleteNote(Integer noteId) {
        notesMapper.deleteNoteByNoteId(noteId);
    }
}
