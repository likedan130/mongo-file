package com.loctek.file.util.office.strategy;

import com.loctek.file.util.office.convertor.HtmlConvertor;
import com.loctek.file.util.office.factory.OfficeFactory;

import java.io.InputStream;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/10
 */
public interface FactoryProvider {

    OfficeFactory getFactory();
}
