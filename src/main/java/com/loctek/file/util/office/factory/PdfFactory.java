package com.loctek.file.util.office.factory;

import com.loctek.file.util.office.convertor.HtmlConvertor;
import com.loctek.file.util.office.convertor.PdfConvertor;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/9
 */
public class PdfFactory implements OfficeFactory{

    @Override
    public HtmlConvertor getHtmlConvertor() {
        return new PdfConvertor();
    }
}
