package com.loctek.file.controller;

import com.loctek.file.constant.ContentTypeEnum;
import com.loctek.file.constant.OfficeEnum;
import com.loctek.file.service.IfileService;
import com.loctek.file.util.ExcelUtil;
import com.loctek.file.util.PdfUtil;
import com.loctek.file.util.WordUtil;
import com.loctek.file.util.office.strategy.FactoryContext;
import com.loctek.file.util.office.strategy.FactoryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/3
 */
@RestController
@RequestMapping("/office")
public class OfficeController {

    @Autowired
    @Qualifier("file")
    IfileService fileService;

    @GetMapping("/excelPreview")
    public void preview(@RequestParam("id") String id, HttpServletResponse response) throws Exception {
        GridFsResource gridFsResource = fileService.getStream(id);
        String fileType = gridFsResource.getContentType();
        String excelHtml = new FactoryContext().getStrategy(fileType)
                .getFactory().getHtmlConvertor().convert(gridFsResource.getInputStream());
        response.setContentType(ContentTypeEnum.HTM.getMimeType());
        PrintWriter out = response.getWriter();
        out.print(excelHtml);
    }
}
