package br.org.cesar.discordtime.stickysessions.domain.repository;

import br.org.cesar.discordtime.stickysessions.domain.model.Note;
import io.reactivex.Single;

public interface NoteRepository {
    Single<Note> addNote(Note note);
}