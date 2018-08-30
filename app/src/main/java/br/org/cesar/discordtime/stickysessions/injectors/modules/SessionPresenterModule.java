package br.org.cesar.discordtime.stickysessions.injectors.modules;

import java.util.List;

import br.org.cesar.discordtime.stickysessions.domain.model.Note;
import br.org.cesar.discordtime.stickysessions.domain.model.Session;
import br.org.cesar.discordtime.stickysessions.executor.ObservableUseCase;
import br.org.cesar.discordtime.stickysessions.presentation.session.SessionContract;
import br.org.cesar.discordtime.stickysessions.presentation.session.SessionPresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class SessionPresenterModule {

    @Provides
    public SessionContract.Presenter providesPresenter(
        ObservableUseCase<String, Session> enterSession,
        ObservableUseCase<Note, Note> addNote,
        ObservableUseCase<String, List<Note>> listNotes){

        return new SessionPresenter(enterSession, addNote, listNotes);
    }

}
