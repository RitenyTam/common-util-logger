package com.riteny.logger;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Riteny
 */
@Data
public class CommonLogProperties {

    private static final String baseDir = "logs";

    private static final Integer maxHistory = 30;

    private static final String maxFileSize = "50MB";

    private static final Map<String, CommonLogConfigEntity> config = new HashMap<>();

    public static CommonLogConfigEntity get(String loggerName) {

        CommonLogConfigEntity configEntity = config.get(loggerName);

        if (configEntity == null) {
            configEntity = new CommonLogConfigEntity();
            configEntity.setBaseDir(baseDir);
            configEntity.setMaxFileSize(maxFileSize);
            configEntity.setMaxHistory(maxHistory);
        }

        return configEntity;
    }

    public static void put(String loggerName, String baseDir, Integer maxHistory, String maxFileSize) {

        CommonLogConfigEntity configEntity = new CommonLogConfigEntity();
        configEntity.setBaseDir(baseDir);
        configEntity.setMaxFileSize(maxFileSize);
        configEntity.setMaxHistory(maxHistory);

        config.put(loggerName, configEntity);
    }
}
