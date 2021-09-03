package com.loctek.file.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/2
 */
@Data
public class ScaleVo {

    /**
     * 源文件ID
     */
    private String id;

    /**
     * 缩放比例,例如0.25f,小于1则表示压缩，大于1表示放大
     */
    @NotNull(message="缩放比例不能为空")
    private Double scale;
    /**
     * 是否保持长宽比
     */
    private Boolean aspectRatio;
}
