package com.loctek.file.service;

import com.loctek.file.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.OutputStream;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/5/1711:03
 */
@Service
@Validated
public interface IimageService extends IfileService {

    /**
     * 图片压缩
     * @param compressVo
     * @param os
     * @throws Exception
     */
    void compress(CompressVo compressVo, OutputStream os) throws Exception;

    /**
     * 根据长宽调整图片大小
     * @param sizeVo
     * @param os
     * @throws Exception
     */
    void zoomBySize(SizeVo sizeVo, OutputStream os) throws Exception;

    /**
     * 根据缩放比调整图片大小
     * @param scaleVo
     * @param os
     * @throws Exception
     */
    void zoomByScale(ScaleVo scaleVo, OutputStream os) throws Exception;

    /**
     * 图片翻转
     * @param rotateVo
     * @param os
     * @throws Exception
     */
    void rotate(RotateVo rotateVo, OutputStream os) throws Exception;

    /**
     * 对mongodb中存储的图片添加图片水印
     * @param waterMarkVo
     * @param os
     * @throws Exception
     */
    void waterMark(WaterMarkVo waterMarkVo, OutputStream os) throws Exception;

    /**
     * 图片裁剪(预留)
     */
    void tailor();

    /**
     * 图片重命名(预留)
     */
    void retype();
}
