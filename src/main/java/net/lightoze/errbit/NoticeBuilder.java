package net.lightoze.errbit;

import net.lightoze.errbit.api.*;
import net.lightoze.errbit.api.Error;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author Vladimir Kulev
 */
public abstract class NoticeBuilder {
    protected String apiKey;
    protected String environment;

    public NoticeBuilder setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public NoticeBuilder setEnvironment(String environment) {
        this.environment = environment;
        return this;
    }

    public Notice build() {
        Notice notice = new Notice();
        notice.setApiKey(apiKey);
        notice.setVersion("2.3");
        notice.setNotifier(notifier());
        notice.setError(error());
        notice.setServerEnvironment(serverEnvironment());
        notice.setRequest(request());
        notice.setCurrentUser(currentUser());
        return notice;
    }

    public Notifier notifier() {
        Notifier notifier = new Notifier();
        notifier.setName("errbit-java");
        notifier.setVersion("2.3");
        notifier.setUrl("https://github.com/lightoze/errbit-java");
        return notifier;
    }

    public abstract Error error();

    public Backtrace backtrace(Throwable throwable) {
        Backtrace backtrace = new Backtrace();
        List<Backtrace.Line> lines = backtrace.getLine();
        for (Throwable cause : ExceptionUtils.getThrowables(throwable)) {
            addBacktraceHeader(cause, lines);
            addBacktraceLines(cause, lines);
        }
        return backtrace;
    }

    public void addBacktraceHeader(Throwable cause, List<Backtrace.Line> lines) {
        Backtrace.Line line = new Backtrace.Line();
        if (lines.isEmpty()) {
            line.setMethod("Exception " + cause.toString());
        } else {
            line.setMethod("Caused by: " + cause.toString());
        }
        lines.add(line);
    }

    public void addBacktraceLines(Throwable throwable, List<Backtrace.Line> lines) {
        for (StackTraceElement element : throwable.getStackTrace()) {
            Backtrace.Line line = new Backtrace.Line();
            line.setMethod(String.format("%s.%s", element.getClassName(), element.getMethodName()));
            line.setFile(element.getFileName());
            line.setNumber(String.valueOf(element.getLineNumber()));
            lines.add(line);
        }
    }

    public ServerEnvironment serverEnvironment() {
        ServerEnvironment env = new ServerEnvironment();
        try {
            env.setHostname(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            env.setHostname("unknown");
        }
        env.setEnvironmentName(environment);
        return env;
    }

    public abstract Request request();

    public CurrentUser currentUser() {
        return null;
    }
}
