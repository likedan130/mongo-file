package com.loctek.file.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loctek.file.service.IfileService;
import com.loctek.file.service.impl.DefaultFileServiceImpl;
import com.loctek.file.util.FileUtil;
import com.loctek.file.util.ResultMsg;
import com.loctek.file.vo.DownloadVo;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author wneck130@gmail.com
 * @Description: mongodb文件系统对外服务端点，提供基本的文件上传下载，批量下载功能
 * @date 2021/5/1217:45
 */
@RequestMapping("/file")
@RestController
public class FileController {

    @Autowired
    @Qualifier("file")
    IfileService fileService;

    /**
     * 单文件上传，可携带其他参数作为文件metaData进行保存，方便做特为殊检索条件
     *
     * @param file
     * @param request request中携带的所有非file参数都将作为metaData信息与文件绑定存储
     * @return
     */
    @RequestMapping("/upload")
    public ResultMsg upload(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request) throws Exception {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        //将multipartHttpServletRequest中的Map<String, String[]>转化成Map<String, Object>
        Map<String, Object> metaData = multipartHttpServletRequest.getParameterMap().entrySet().stream()
                .filter(stringEntry -> !Objects.equals(stringEntry.getValue(), "file"))
                .collect(Collectors.toMap(Map.Entry::getKey, stringEntry ->
                        stringEntry.getValue() != null && stringEntry.getValue().length > 0
                                ? stringEntry.getValue()[0] : null));
        try {
            InputStream inputStream = file.getInputStream();
            String fileName = file.getOriginalFilename();
            String objectId = fileService.save(inputStream, fileName, metaData);
            return ResultMsg.ok(objectId);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultMsg.error("文件保存失败!!!");
        }
    }

    /**
     * 文件下载
     *
     * @param id
     * @param response
     * @throws Exception
     */
    @RequestMapping("/download")
    public void download(@RequestParam("id") String id, HttpServletResponse response) throws Exception {
        //gridfs存储文件以document+bucket形式保存，故会分成两个部分分别保存在files和chunks
        //进行文件操作时要进行两次操作取出file的索引内容和流内容
        //先查询文件是否存在
        GridFSFile gridFSFile = fileService.findOneById(id);
        if (gridFSFile == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            ResultMsg result = ResultMsg.error("文件不存在!!!");
            response.setContentType("text/json;charset=utf-8");
            //塞到HttpServletResponse中返回给前端
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return;
        }
        //取出文件对应的流内容
        GridFsResource gridFsResource = fileService.getStream(gridFSFile);
        //预处理文件名
        String filename = gridFSFile.getFilename();
        String contentType = gridFsResource.getContentType();
        filename = FileUtil.beautifulName(filename, contentType);
        //文件名中存在中文时会有乱码问题，进行url编码处理，并且制定浏览器的解码方式
        String disposition = "attachment;filename*=utf-8''" + URLEncoder.encode(filename, FileUtil.CHARSET_UTF8);
        response.setHeader("Content-Disposition", disposition);
        response.addHeader("Cache-Control", "no-cache");
        //对图片做特殊contentType处理
        if (FileUtil.IMAGE.contains(contentType)) {
            contentType = FileUtil.IMAGE_PREFIX + contentType;
        }
        response.setContentType(contentType);
        IOUtils.copy(fileService.getStream(gridFSFile).getInputStream(), response.getOutputStream());
        response.flushBuffer();
    }

    /**
     * 根据文件id批量打包下载
     *
     * @param downloadVo
     * @param response
     * @throws Exception
     */
    @RequestMapping("/downloadAll")
    public void downloadAll(@RequestBody DownloadVo downloadVo, HttpServletResponse response) throws Exception {
        byte[] buf = new byte[1024];
        //统一压缩文件所需响应头
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-Disposition", "attachment;filename=download.zip");
        response.addHeader("Cache-Control", "no-cache");
        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        for (String id : downloadVo.getIds()) {
            GridFSFile gridFSFile = fileService.findOneById(id);
            if (gridFSFile == null) {
                ObjectMapper objectMapper = new ObjectMapper();
                ResultMsg result = ResultMsg.error("文件不存在!!!");
                response.setContentType("text/json;charset=utf-8");
                //塞到HttpServletResponse中返回给前台
                response.getWriter().write(objectMapper.writeValueAsString(result));
                return;
            }
            GridFsResource gridFsResource = fileService.getStream(gridFSFile);
            //预处理文件名
            String filename = gridFSFile.getFilename();
            String contentType = gridFsResource.getContentType();
            filename = FileUtil.beautifulName(filename, contentType);
            InputStream in = gridFsResource.getInputStream();
            out.putNextEntry(new ZipEntry(filename));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
        out.closeEntry();
        out.close();
    }

}
