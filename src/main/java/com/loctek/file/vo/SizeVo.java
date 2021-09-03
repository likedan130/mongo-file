package com.loctek.file.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/5/1710:49
 */
@Data
public class SizeVo {

    /**
     * 源文件ID
     */
    private String id;

    /**
     * 图片高度，传入高度大于图片实际高度时不生效，按照宽度的缩放比例产生最终的高度值
     */
    @NotNull(message="缩放高度不能为空")
    private Integer height;

    /**
     * 图片宽度，传入宽度大于图片实际宽度时不生效，按照高度的缩放比例产生最终的宽度值
     */
    @NotNull(message="缩放宽度不能为空")
    private Integer width;

    /**
     * 是否保持长宽比
     */
    private Boolean aspectRatio;
}
