package com.loctek.file.service.impl;

import com.loctek.file.service.IimageService;
import com.loctek.file.vo.*;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Coordinate;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.OutputStream;
import java.util.Optional;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/5/1310:30
 */
@Service("image")
public class DefaultImageServiceImpl extends DefaultFileServiceImpl implements IimageService {

    @Override
    public void compress(CompressVo compressVo, OutputStream os) throws Exception {
        GridFsResource gridFsResource = getStream(compressVo.getId());
        Thumbnails.of(gridFsResource.getInputStream()).outputQuality(compressVo.getQuality()).toOutputStream(os);
    }

    @Override
    public void zoomBySize(SizeVo sizeVo, OutputStream os) throws Exception {
        GridFsResource gridFsResource = getStream(sizeVo.getId());
        Thumbnails.of(gridFsResource.getInputStream()).size(sizeVo.getWidth(), sizeVo.getHeight())
                .keepAspectRatio(Optional.ofNullable(sizeVo.getAspectRatio()).orElse(true)).toOutputStream(os);
    }

    @Override
    public void zoomByScale(ScaleVo scaleVo, OutputStream os) throws Exception {
        GridFsResource gridFsResource = getStream(scaleVo.getId());
        Thumbnails.of(gridFsResource.getInputStream()).scale(scaleVo.getScale())
                .keepAspectRatio(Optional.ofNullable(scaleVo.getAspectRatio()).orElse(true)).toOutputStream(os);
    }

    @Override
    public void rotate(RotateVo rotateVo, OutputStream os) throws Exception {
        GridFsResource gridFsResource = getStream(rotateVo.getId());
        Thumbnails.of(gridFsResource.getInputStream()).rotate(rotateVo.getAngle()).toOutputStream(os);
    }

    @Override
    public void waterMark(WaterMarkVo waterMarkVo, OutputStream os) throws Exception {
        GridFsResource originalFile = this.getStream(waterMarkVo.getId());
        GridFsResource watermarkFile = this.getStream(waterMarkVo.getWatermarkId());
        Thumbnails.of(originalFile.getInputStream())
                .watermark(convertPosition(waterMarkVo),
                        ImageIO.read(watermarkFile.getInputStream()),
                        waterMarkVo.getOpacity(), waterMarkVo.getInsets()).scale(1D).toOutputStream(os);
    }

    @Override
    public void tailor() {
        //TODO 剪裁图片
    }

    @Override
    public void retype() {
        //TODO 类型转化
    }

    /**
     * 水印位置处理
     * @param waterMarkVo
     * @return
     */
    public Position convertPosition(WaterMarkVo waterMarkVo) {
        if (waterMarkVo.getOffsetX() != null) {
            return new Coordinate(waterMarkVo.getOffsetX(),
                    waterMarkVo.getOffsetY());
        } else {
            return Positions.BOTTOM_RIGHT;
        }
    }
}
