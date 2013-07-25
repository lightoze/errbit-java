package net.lightoze.errbit;

import net.lightoze.errbit.api.Notice;
import org.apache.commons.lang.Validate;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Vladimir Kulev
 */
public class Log4jErrbitAppender extends AppenderSkeleton {
    private Class<? extends Log4jNoticeBuilder> noticeBuilder = Log4jNoticeBuilder.class;
    private NoticeSender sender;
    private String url;
    private String apiKey;
    private String environment;
    private boolean enabled = true;

    public Log4jErrbitAppender() {
        setThreshold(Level.ERROR);
    }

    @Override
    protected void append(LoggingEvent event) {
        if (!enabled) {
            return;
        }
        try {
            Log4jNoticeBuilder builder = noticeBuilder.newInstance();
            Notice notice = builder
                    .setEvent(event)
                    .setApiKey(apiKey)
                    .setEnvironment(environment)
                    .build();
            sender.send(notice);
        } catch (Exception e) {
            LogLog.warn("Could not send error notice", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void setNoticeBuilder(String className) throws ClassNotFoundException {
        noticeBuilder = (Class<? extends Log4jNoticeBuilder>) Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void activateOptions() {
        Validate.notNull(url);
        Validate.notNull(apiKey);
        Validate.notNull(environment);
        sender = new NoticeSender(url);
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
