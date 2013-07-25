package net.lightoze.errbit;

import net.lightoze.errbit.api.Backtrace;
import net.lightoze.errbit.api.Error;
import net.lightoze.errbit.api.Request;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * @author Vladimir Kulev
 */
public class Log4jNoticeBuilder extends NoticeBuilder {
    protected LoggingEvent event;

    public NoticeBuilder setEvent(LoggingEvent event) {
        this.event = event;
        return this;
    }

    @Override
    public net.lightoze.errbit.api.Error error() {
        Error error = new Error();
        Throwable throwable = throwable(event);
        if (throwable != null) {
            error.setBacktrace(backtrace(throwable));
            error.setClazz(throwable.getClass().getName());
        } else {
            error.setBacktrace(new Backtrace());
            error.setClazz(event.getLoggerName());
        }
        error.setMessage(event.getRenderedMessage());
        return error;
    }

    @Override
    public Request request() {
        Request request = new Request();
        request.setComponent(event.getLoggerName());
        return request;
    }

    public static Throwable throwable(LoggingEvent event) {
        ThrowableInformation info = event.getThrowableInformation();
        if (info != null) {
            return info.getThrowable();
        }

        Object message = event.getMessage();
        if (message instanceof Throwable) {
            return (Throwable) message;
        }

        return null;
    }
}
