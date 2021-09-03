package com.loctek.file.vo;

import lombok.Data;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/2
 */
@Data
public class CompressVo {

    /**
     * 源文件ID
     */
    private String id;

    /**
     * 图片输出质量
     */
    private Double quality;
}
