package com.loctek.file.util.office.factory;

import com.loctek.file.util.office.convertor.ExcelConvertor;
import com.loctek.file.util.office.convertor.HtmlConvertor;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/9
 */
public class ExcelFactory implements OfficeFactory {

    @Override
    public HtmlConvertor getHtmlConvertor() {
        return new ExcelConvertor();
    }
}
