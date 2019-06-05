package br.org.cesar.discordtime.stickysessions.presentation.meeting

import br.org.cesar.discordtime.stickysessions.domain.model.Meeting
import br.org.cesar.discordtime.stickysessions.executor.IObservableUseCase
import io.reactivex.observers.DisposableSingleObserver
import java.text.SimpleDateFormat
import java.util.*

class MeetingPresenter(private val listMeetings: IObservableUseCase<Void, MutableList<Meeting>>)
    : MeetingContract.Presenter {

    var mView: MeetingContract.View? = null

    override fun attachView(view: MeetingContract.View) {
        mView = view
    }
    override fun detachView() {
        mView = null
        listMeetings.dispose()
    }
    override fun onResume() {
        mView?.let { view ->
            view.startLoadingMeetings()
            listMeetings.execute(MeetingsObserver(),null)
        }
    }

    private inner class MeetingsObserver: DisposableSingleObserver<MutableList<Meeting>>() {
        override fun onSuccess(meetings: MutableList<Meeting>) {
            mView?.showMeetings(
                // TODO: Run sort and mapping on background
                meetings.run {
                    sortBy {
                        it.date
                    }
                    map {
                        mapFromDomain(it).apply {
                            recent = isARecentMeeting(it)
                        }
                    }
                } as MutableList<MeetingItem>
            )
        }

        override fun onError(e: Throwable) {
            mView?.run {
                stopLoadingMeetings()
                showError(e.localizedMessage)
            }
        }
    }

    private fun isARecentMeeting(meeting: Meeting): Boolean {
            val meetingCalendar = Calendar.getInstance()
            val today = Calendar.getInstance()
            meetingCalendar.time = meeting.date
            return meetingCalendar.get(Calendar.DAY_OF_YEAR) >= today.get(Calendar.DAY_OF_YEAR)
                    && meetingCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
    }

    private fun mapFromDomain(meeting: Meeting): MeetingItem {
        return MeetingItem(
                id = meeting.id,
                title = meeting.title,
                description = meeting.description,
                location = meeting.location,
                date = SimpleDateFormat("dd/MM", Locale.US).format(meeting.date),
                time = SimpleDateFormat("HH:mm", Locale.US).format(meeting.date),
                numOfSessions = meeting.sessions.size,
                numOfParticipants = meeting.participants.size
        )
    }
}