package org.example;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class MnemonicLogLevel extends Filter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getLevel().equals(Level.WARN)) {
            return FilterReply.ACCEPT;
        } else {
            return FilterReply.DENY;
        }
    }
}

