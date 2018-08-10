package br.org.cesar.discordtime.stickysessions.presentation.session;

public interface SessionContract {

    interface Presenter {
        void attachView(SessionContract.View view);
        void detachView();
        void onLoadSession();
        void onStopLoadSession();
        void onEnterSession(String sessionId);
    }

    interface View {
        void displaySession();
        void displayError(String message);
        void startLoadingSession();
        void stopLoadingSession();
    }
}
