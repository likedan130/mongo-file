package com.loctek.file.vo;

import lombok.Data;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/2
 */
@Data
public class RotateVo {

    /**
     * 源文件ID
     */
    private String id;

    /**
     * 翻转角度
     */
    private Double angle;
}
