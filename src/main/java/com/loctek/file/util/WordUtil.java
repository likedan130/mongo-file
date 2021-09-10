package com.loctek.file.util;

import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;

import java.io.InputStream;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/9/7
 */
public class WordUtil {

    /**
     * word转html
     *
     * @param is
     * @return
     * @throws Exception
     */
    public static String wordToHtml(InputStream is) throws Exception {
        DocumentConverter converter = new DocumentConverter();
        Result<String> result = converter.convertToHtml(is);
        return result.getValue();
    }
}
