package com.bugsnag;

import com.bugsnag.HandledState.SeverityReasonType;

import org.springframework.util.ErrorHandler;

import java.util.Collections;

/**
 * Reports uncaught exceptions thrown from scheduled task execution to Bugsnag
 * and then passes the exception to any other existing error handler.
 */
class BugsnagScheduledTaskErrorHandler implements ErrorHandler {

    private final Bugsnag bugsnag;

    private ErrorHandler existingErrorHandler;

    BugsnagScheduledTaskErrorHandler(Bugsnag bugsnag) {
        this.bugsnag = bugsnag;
    }

    @Override
    public void handleError(Throwable throwable) {
        if (bugsnag.getConfig().shouldSendUncaughtExceptions()) {
            HandledState handledState = HandledState.newInstance(
                    SeverityReasonType.REASON_UNHANDLED_EXCEPTION_MIDDLEWARE,
                    Collections.singletonMap("framework", "Spring"),
                    Severity.ERROR,
                    true);

            bugsnag.notify(throwable, handledState);
        }

        if (existingErrorHandler != null
                && !(existingErrorHandler instanceof BugsnagScheduledTaskErrorHandler)) {
            existingErrorHandler.handleError(throwable);
        }
    }

    void setExistingErrorHandler(final ErrorHandler existingErrorHandler) {
        this.existingErrorHandler = existingErrorHandler;
    }
}
