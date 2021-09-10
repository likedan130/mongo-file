package com.loctek.file.util.office.strategy;

import com.loctek.file.util.office.factory.ExcelFactory;
import com.loctek.file.util.office.factory.OfficeFactory;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/10
 */
public class ExcelFactoryProvider implements FactoryProvider {
    @Override
    public OfficeFactory getFactory() {
        return new ExcelFactory();
    }
}
