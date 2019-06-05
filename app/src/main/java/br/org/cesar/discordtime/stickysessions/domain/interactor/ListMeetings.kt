package br.org.cesar.discordtime.stickysessions.domain.interactor

import br.org.cesar.discordtime.stickysessions.domain.model.Meeting
import br.org.cesar.discordtime.stickysessions.domain.repository.MeetingRepository
import io.reactivex.Single

class ListMeetings(repository: MeetingRepository): UseCase<Void, MutableList<Meeting>>() {

    private val mRepository = repository

    override fun execute(params: Void?): Single<MutableList<Meeting>> {
        return mRepository.listMeetings()
    }

}