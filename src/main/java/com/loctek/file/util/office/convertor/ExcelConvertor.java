package com.loctek.file.util.office.convertor;

import com.loctek.file.util.ExcelUtil;

import java.io.InputStream;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/9
 */
public class ExcelConvertor implements HtmlConvertor {

    private static final boolean NEED_STYLE = true;

    @Override
    public String convert(InputStream is) throws Exception {
        return ExcelUtil.excelToHtml(is, NEED_STYLE);
    }
}
