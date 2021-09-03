package com.loctek.file.vo;

import lombok.Data;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/5/1714:39
 */
@Data
public class WaterMarkVo {

    /**
     * 源文件ID
     */
    private String id;

    /**
     * 水印偏移X轴位置
     */
    private Integer offsetX;

    /**
     * 水印偏移Y轴位置
     */
    private Integer offsetY;

    /**
     * 图片水印对应文件id
     */
    private String watermarkId;

    /**
     * 水印透明度
     */
    private Float opacity;

    /**
     * 文字水印内容
     */
    private String plaintext;

    /**
     *
     */
    private Integer insets;
}
