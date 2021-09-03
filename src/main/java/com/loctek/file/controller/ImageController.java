package com.loctek.file.controller;

import com.loctek.file.vo.*;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.loctek.file.service.IimageService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Optional;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/5/1714:24
 */
@RestController
@RequestMapping("/image")
public class ImageController {


    @Autowired
    @Qualifier("image")
    IimageService imageService;

    @PostMapping("/compress")
    public void compress(@RequestBody CompressVo compressVo, HttpServletResponse response) throws Exception {
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            imageService.compress(compressVo, os);
        } finally {
            os.close();
        }
    }

    /**
     * 根据长宽缩放图片，长宽超出原始大小时无效
     *
     * @param sizeVo
     * @param response
     * @throws Exception
     */
    @PostMapping("/zoomBySize")
    public void zoomBySize(@RequestBody SizeVo sizeVo,
                           HttpServletResponse response) throws Exception {
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            imageService.zoomBySize(sizeVo, os);
        } finally {
            os.close();
        }
    }

    /**
     * 根据比例缩放图片，scale大于1.0D时放大，小于1.0D时缩小
     * @param scaleVo
     * @param response
     * @throws Exception
     */
    @PostMapping("/zoomByScale")
    public void zoomByScale(@RequestBody ScaleVo scaleVo, HttpServletResponse response) throws Exception {
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            imageService.zoomByScale(scaleVo, os);
        } finally {
            os.close();
        }
    }

    /**
     * 翻转图片，angle大于0时顺时针，小于0时逆时针
     * @param rotateVo
     * @param response
     * @throws Exception
     */
    @PostMapping("/rotate")
    public void rotate(@RequestBody RotateVo rotateVo, HttpServletResponse response) throws Exception {
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            imageService.rotate(rotateVo, os);
        } finally {
            os.close();
        }
    }

    /**
     * 图片增加水印
     * @param waterMarkVo
     * @param response
     * @throws Exception
     */
    @PostMapping("/watermark")
    public void watermark(@RequestBody WaterMarkVo waterMarkVo, HttpServletResponse response) throws Exception {
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            imageService.waterMark(waterMarkVo, os);
        } finally {
            os.close();
        }
    }
}
