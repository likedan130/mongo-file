package com.loctek.file.util.office.convertor;

import com.loctek.file.util.WordUtil;

import java.io.InputStream;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/10
 */
public class WordConvertor implements HtmlConvertor {

    @Override
    public String convert(InputStream is) throws Exception {
        return WordUtil.wordToHtml(is);
    }
}
