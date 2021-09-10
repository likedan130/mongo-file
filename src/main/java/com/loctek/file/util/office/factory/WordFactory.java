package com.loctek.file.util.office.factory;

import com.loctek.file.util.office.convertor.HtmlConvertor;
import com.loctek.file.util.office.convertor.WordConvertor;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/9
 */
public class WordFactory implements OfficeFactory {

    @Override
    public HtmlConvertor getHtmlConvertor() {
        return new WordConvertor();
    }
}
