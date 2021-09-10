package com.loctek.file.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loctek.file.constant.ContentTypeEnum;
import com.loctek.file.util.FileUtil;
import com.loctek.file.vo.ResultMsg;
import com.loctek.file.vo.*;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.loctek.file.service.IimageService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

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

    /**
     * 根据ID预览图片
     * @param id
     * @param response
     * @throws Exception
     */
    @GetMapping("/preview")
    public void preview(@RequestParam("id") String id, HttpServletResponse response) throws Exception {
        //gridfs存储文件以document+bucket形式保存，故会分成两个部分分别保存在files和chunks
        //进行文件操作时要进行两次操作取出file的索引内容和流内容
        //先查询文件是否存在
        GridFSFile gridFSFile = imageService.findOneById(id);
        if (gridFSFile == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            ResultMsg result = ResultMsg.error("文件不存在!!!");
            response.setContentType("text/json;charset=utf-8");
            //塞到HttpServletResponse中返回给前端
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return;
        }
        //取出文件对应的流内容
        GridFsResource gridFsResource = imageService.getStream(gridFSFile);
        //预处理文件名
        String filename = gridFSFile.getFilename();
        String fileType = gridFsResource.getContentType();
        filename = FileUtil.beautifulName(filename, fileType);
        //文件名中存在中文时会有乱码问题，进行url编码处理，并且制定浏览器的解码方式
        response.addHeader("Cache-Control", "no-cache");
        //对图片做特殊contentType处理
        response.setContentType(ContentTypeEnum.getContentType(fileType));
        IOUtils.copy(imageService.getStream(gridFSFile).getInputStream(), response.getOutputStream());
        response.flushBuffer();
    }

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
