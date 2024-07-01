package com.riteny.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Riteny
 */
public class CommonLoggerFactory {

    private static final Map<String, Logger> loggers = new HashMap<>();

    public static Logger getLogger(String name) {

        Logger logger = loggers.get(name);
        if (logger != null) {
            return logger;
        }

        synchronized (CommonLoggerFactory.class) {
            logger = loggers.get(name);
            if (logger != null) {
                return logger;
            }
            logger = createLogger(name);
            loggers.put(name, logger);
        }

        return logger;
    }

    public static void registerLogger(String loggerName, String baseDir, Integer maxHistory, String maxFileSize) {
        CommonLogProperties.put(loggerName, baseDir, maxHistory, maxFileSize);
    }

    private static Logger createLogger(String loggerName) {

        CommonLogConfigEntity configEntity = CommonLogProperties.get(loggerName);

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        Logger logger = loggerContext.getLogger(loggerName);

        RollingFileAppender<ILoggingEvent> rollingFileAppender = getInfoAppender(loggerName, loggerContext, configEntity);
        RollingFileAppender<ILoggingEvent> errorRollingFileAppender = getErrorAppender(loggerName, loggerContext, configEntity);
        logger.addAppender(rollingFileAppender);
        logger.addAppender(errorRollingFileAppender);

        return logger;
    }


    private static RollingFileAppender<ILoggingEvent> getInfoAppender(String loggerName, LoggerContext loggerContext, CommonLogConfigEntity configEntity) {

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setContext(loggerContext);
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setName(loggerName + "LogAppender");
        rollingFileAppender.setFile(configEntity.getBaseDir() + "/" + loggerName + ".info.log");

        SizeAndTimeBasedRollingPolicy<Object> rollingPolicy = getRollingPolicy(loggerName, "info", loggerContext, configEntity, rollingFileAppender);
        rollingFileAppender.setRollingPolicy(rollingPolicy);

        LevelFilter levelFilter = new LevelFilter();
        levelFilter.setLevel(Level.ERROR);
        levelFilter.setOnMatch(FilterReply.DENY);
        levelFilter.setOnMismatch(FilterReply.ACCEPT);
        levelFilter.start();
        rollingFileAppender.addFilter(levelFilter);

        PatternLayoutEncoder encoder = getPatternLayoutEncoder(loggerContext);
        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();

        return rollingFileAppender;
    }


    private static RollingFileAppender<ILoggingEvent> getErrorAppender(String loggerName
            , LoggerContext loggerContext, CommonLogConfigEntity configEntity) {

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setContext(loggerContext);
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setName(loggerName + "ErrorLogAppender");
        rollingFileAppender.setFile(configEntity.getBaseDir() + "/" + loggerName + ".error.log");

        SizeAndTimeBasedRollingPolicy<Object> rollingPolicy = getRollingPolicy(loggerName, "error", loggerContext, configEntity, rollingFileAppender);
        rollingFileAppender.setRollingPolicy(rollingPolicy);

        LevelFilter levelFilter = new LevelFilter();
        levelFilter.setLevel(Level.ERROR);
        levelFilter.setOnMatch(FilterReply.ACCEPT);
        levelFilter.setOnMismatch(FilterReply.DENY);
        levelFilter.start();
        rollingFileAppender.addFilter(levelFilter);

        PatternLayoutEncoder encoder = getPatternLayoutEncoder(loggerContext);
        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();

        return rollingFileAppender;
    }

    private static SizeAndTimeBasedRollingPolicy<Object> getRollingPolicy(String loggerName, String loggerLevel
            , LoggerContext loggerContext, CommonLogConfigEntity configEntity, RollingFileAppender<ILoggingEvent> rollingFileAppender) {

        SizeAndTimeBasedRollingPolicy<Object> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setFileNamePattern(configEntity.getBaseDir() + "/" + loggerName + "." + loggerLevel + ".%d{yyyy-MM-dd}.%i.log");
        rollingPolicy.setMaxHistory(configEntity.getMaxHistory());
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setParent(rollingFileAppender);
        rollingPolicy.setMaxFileSize(FileSize.valueOf(configEntity.getMaxFileSize()));
        rollingPolicy.setCleanHistoryOnStart(true);
        rollingPolicy.start();

        return rollingPolicy;
    }


    private static PatternLayoutEncoder getPatternLayoutEncoder(LoggerContext loggerContext) {

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %logger{50} %msg%n");
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.setContext(loggerContext);
        encoder.start();

        return encoder;
    }
}
