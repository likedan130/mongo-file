package com.loctek.file.util.office.strategy;

import com.loctek.file.constant.OfficeEnum;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/10
 */
public class FactoryContext {

    /**
     * 根据文件类型获取具体的工厂策略
     * @param fileType
     * @return
     */
    public FactoryProvider getStrategy(String fileType) {
        FactoryProvider factoryStrategy =  null;
        OfficeEnum officeEnum = OfficeEnum.getByExtension(fileType);
        switch (officeEnum) {
            case EXCEL03:
                factoryStrategy = new ExcelFactoryProvider();
                break;
            case EXCEL07:
                factoryStrategy = new ExcelFactoryProvider();
                break;
            case WORD03:
                factoryStrategy = new WordFactoryProvider();
                break;
            case WORD07:
                factoryStrategy = new WordFactoryProvider();
                break;
            case PDF:
                factoryStrategy = new PdfFactoryProvider();
                break;
            default:
                break;
        }
        return factoryStrategy;
    }
}
