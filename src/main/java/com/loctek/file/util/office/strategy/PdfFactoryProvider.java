package com.loctek.file.util.office.strategy;

import com.loctek.file.util.office.factory.OfficeFactory;
import com.loctek.file.util.office.factory.PdfFactory;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/10
 */
public class PdfFactoryProvider implements FactoryProvider {

    @Override
    public OfficeFactory getFactory() {
        return new PdfFactory();
    }
}
