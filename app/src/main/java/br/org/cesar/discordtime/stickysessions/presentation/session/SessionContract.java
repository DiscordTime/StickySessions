package br.org.cesar.discordtime.stickysessions.presentation.session;

import java.util.List;

import br.org.cesar.discordtime.stickysessions.domain.model.Note;

public interface SessionContract {

    interface Presenter {
        void attachView(SessionContract.View view);
        void detachView();
        void onEnterSession(String sessionId);
        void onShareSession();
        void onAddNoteClicked();
        void addNewNote(String sessionId, String description);
        void onNoteWidgetClicked(Note note);
    }

    interface View {
        void displayAddNoteDialog(List<String> topics);
        void displaySession();
        void displayError(String message);
        void startLoadingSession();
        void stopLoadingSession();
        void shareSession(String sessionId);
        void showAddNoteSuccessfullyMessage();
        void displayNotes(List<Note> notes);
        void displayErrorInvalidNotes();
        void displayNoteContent(Note note);
        void addNoteToNoteList(Note note);
    }
}
