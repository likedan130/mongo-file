package com.loctek.file.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
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

    public static final String CHARSET_UTF8 = "UTF-8";

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
     *
     * @param filename
     * @param fileType
     * @return
     */
    public static String beautifulName(String filename, String fileType) {
        return Optional.ofNullable(filename).map(name -> {
            if (!name.contains(FileUtil.EXTENSION_SEPARATION)) {
                return name + fileType;
            }
            return name;
        }).orElse(UNDEFINE_FILENAME + fileType);
    }


    /**
     * range信息转化，range取值有如下几种可能
     * Range: bytes=0-499 表示第 0-499 字节范围的内容
     * Range: bytes=500-999 表示第 500-999 字节范围的内容
     * Range: bytes=-500 表示最后 500 字节的内容
     * Range: bytes=500- 表示从第 500 字节开始到文件结束部分的内容
     * Range: bytes=0-0,-1 表示第一个和最后一个字节
     * Range: bytes=500-600,601-999 同时指定几个范围
     *
     * @param rangeStr
     * @param contentLength
     * @return
     * @throws Exception
     */
    public static List<int[]> convertRange(String rangeStr, long contentLength) throws Exception {
        List<int[]> result = new ArrayList<>();
        if (rangeStr.contains(",")) {
            String[] ranges = rangeStr.split(",");
            for (String range : ranges) {
                result.add(dealRange(range, contentLength));
            }
        } else {
            result.add(dealRange(rangeStr, contentLength));
        }
        return result;
    }

    /**
     * 获取rang起止值
     *
     * @param range
     * @param contentLength
     * @return
     * @throws Exception
     */
    public static int[] dealRange(String range, long contentLength) throws Exception {
        int[] result = new int[2];
        if (!range.contains("-")) {
            throw new Exception("无法解析的Range格式!!!");
        }
        String[] ary = range.split("-");
        int contentLengthInt = (int) contentLength;
        //如果长度小于2，则只有可能是500-的格式或异常格式
        if (ary.length < 2) {
            result[0] = Integer.parseInt(ary[0]);
            result[1] = contentLengthInt;
        } else {
            //如果长度大于2，取前两位进行计算，等于2时如果数组第一位为""，则是-500格式
            if (Objects.equals("", ary[0])) {
                result[1] = contentLengthInt - 1;
                result[0] = contentLengthInt - Integer.parseInt(ary[1]);
            } else {
                result[0] = Integer.parseInt(ary[0]);
                result[1] = Integer.parseInt(ary[1]);
            }
        }
        //解析完成后对最大长度进行验证
        if (result[0] > contentLengthInt || result[1] > contentLengthInt || result[0] > result[1]) {
            throw new Exception("Range长度错误!!!");
        }
        return result;
    }


    /**
     * 数组拼接
     *
     * @param data1
     * @param data2
     * @return data1 与 data2拼接的结果
     */
    public static byte[] addBytes(byte[] data1, byte[] data2) {
        if (data1 == null) {
            return data2;
        }
        if (data2 == null) {
            return data1;
        }
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;

    }
}
