package br.org.cesar.discordtime.stickysessions

import androidx.test.platform.app.InstrumentationRegistry
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest


internal class MockServerDispatcher {

    /**
     * Return ok response from mock server
     */
    internal inner class RequestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {

            if (request.path == "/sessions") {
                return MockResponse().setResponseCode(200)
                        .setBody("{data:FakeData}")
            } else if (request.path == "/sessions/{id}") {
                return MockResponse().setResponseCode(200)
                        .setBody("{codes:FakeCode}")
            } else if (request.path == "/sessions/")
                return MockResponse().setResponseCode(200).setBody(
                        JsonReader().readFromAssets("get_sessions.json"))

            return MockResponse().setResponseCode(404)
        }
    }

    /**
     * Return error response from mock server
     */
    internal inner class ErrorDispatcher : Dispatcher() {

        override fun dispatch(request: RecordedRequest): MockResponse {

            return MockResponse().setResponseCode(400)

        }
    }
}