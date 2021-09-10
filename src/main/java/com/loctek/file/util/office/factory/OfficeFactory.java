package com.loctek.file.util.office.factory;

import com.loctek.file.util.office.convertor.HtmlConvertor;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/9
 */
public interface OfficeFactory {

    /**
     * 生成Office对象的html转化工具
     * @return
     */
    HtmlConvertor getHtmlConvertor();
}
