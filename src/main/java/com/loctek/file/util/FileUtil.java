package com.loctek.file.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author wneck130@gmail.com
 * @Description:
 * @date 2021/5/1310:56
 */
@Slf4j
public class FileUtil {

    public static final String EXTENSION_SEPARATION = ".";

    public static final String UNDEFINE_FILENAME = "未命名";

    public static final String IMAGE_PREFIX = "image/";

    public static final String CHARSET_UTF8 = "UTF-8";

    public static final Set<String> IMAGE = new HashSet(Arrays.asList("jpg", "bmp", "png", "gif", "jpeg"));

    public static final String JPG_TYPE = "jpg";

    public static final String JPEG_TYPE = "jpeg";

    public static String getExtension(String filename) {
        if (filename.lastIndexOf(EXTENSION_SEPARATION) < 0) {
            return null;
        }
        return filename.substring(filename.lastIndexOf(EXTENSION_SEPARATION) + 1);
    }

    /**
     * 功能:压缩多个文件成一个zip文件
     *
     * @param srcfile：源文件列表
     * @param zipfile：压缩后的文件
     */
    public static void zipFiles(File[] srcfile, File zipfile) {
        byte[] buf = new byte[1024];
        try {
            //ZipOutputStream类：完成文件或文件夹的压缩
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            for (int i = 0; i < srcfile.length; i++) {
                FileInputStream in = new FileInputStream(srcfile[i]);
                out.putNextEntry(new ZipEntry(srcfile[i].getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件保存进mongodb时未做特殊处理会导致文件名和类型紊乱，提供简易的补救方法
     * @param filename
     * @param contentType
     * @return
     */
    public static String beautifulName(String filename, String contentType) {
        return Optional.ofNullable(filename).map(name -> {
            if (!name.contains(FileUtil.EXTENSION_SEPARATION)) {
                return name + contentType;
            }
            return name;
        }).orElse(UNDEFINE_FILENAME + contentType);
    }
}
