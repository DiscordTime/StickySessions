package br.org.cesar.discordtime.stickysessions.domain.interactor;

import java.util.List;

import br.org.cesar.discordtime.stickysessions.domain.model.Session;
import br.org.cesar.discordtime.stickysessions.domain.repository.SessionRepository;
import io.reactivex.Single;

public class ListSessions extends UseCase<String, List<Session>> {

    private final SessionRepository mRepository;

    public ListSessions(SessionRepository repository) {
        mRepository = repository;
    }

    @Override
    public Single<List<Session>> execute(String meetingId) {
        return mRepository.listSessions(meetingId);
    }
}
