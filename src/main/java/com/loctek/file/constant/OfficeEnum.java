package com.loctek.file.constant;

import org.springframework.util.StringUtils;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/7
 */
public enum OfficeEnum {

    WORD03("doc", "word文档2003"),
    WORD07("docx", "word文档2007"),
    EXCEL03("xls", "excel文档2003"),
    EXCEL07("xlsx", "excel文档2007"),
    PDF("pdf", "pdf文档");

    /**
     * 文件扩展名
     */
    private String extension;

    /**
     * office类型
     */
    private String officeType;

    OfficeEnum(String extension, String officeType){
        this.extension = extension;
        this.officeType = officeType;
    }

    public String getExtension() {
        return extension;
    }

    public String getOfficeType() {
        return officeType;
    }

    /**
     * 根据扩展名获取枚举对象
     * @param extension
     * @return
     */
    public static OfficeEnum getByExtension(String extension) {
        if(StringUtils.hasText(extension)){
            return null;
        }
        for (OfficeEnum officeEnum : OfficeEnum.values()) {
            if (extension.equals(officeEnum.getExtension())) {
                return officeEnum;
            }
        }
        return null;
    }
}
