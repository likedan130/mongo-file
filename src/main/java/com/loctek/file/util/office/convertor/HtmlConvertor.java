package com.loctek.file.util.office.convertor;

import java.io.InputStream;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/9
 */
public interface HtmlConvertor {

    /**
     * 将offic文件转化成html格式文本内容
     * @param is
     * @return
     */
    String convert(InputStream is) throws Exception;
}
