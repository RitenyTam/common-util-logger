package com.riteny.logger;

import lombok.Data;

/**
 * @author Riteny
 * 2020/11/5  11:55
 */
@Data
public class CommonLogConfigEntity {

    private String baseDir;

    private Integer maxHistory;

    private String maxFileSize;
}
