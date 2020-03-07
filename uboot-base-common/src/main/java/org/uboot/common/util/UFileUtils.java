package org.uboot.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.uboot.common.exception.UBootException;
import org.uboot.common.system.vo.UploadFileInfoVo;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * uboot 文件工具类
 *
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @created on  : 2020/3/7
 */
@Slf4j
public class UFileUtils {
    /**
     * 保存上传文件
     *
     * @param uploadpath
     * @param bizPath
     * @param request
     */
    public static UploadFileInfoVo saveUploadFile(String uploadpath, String bizPath, HttpServletRequest request) {
        UploadFileInfoVo fileInfoVo = new UploadFileInfoVo();
        try {
            String fileName = null;
            bizPath = StringUtils.isBlank(bizPath) ? "files" : bizPath;
            String nowday = new SimpleDateFormat("yyyyMMdd").format(new Date());
            File file = new File(uploadpath + File.separator + bizPath + File.separator + nowday);
            if (!file.exists()) {
                file.mkdirs();// 创建文件根目录
            }
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
            String orgName = mf.getOriginalFilename();// 获取文件名
            String name = orgName.substring(0, orgName.lastIndexOf("."));
            fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
            String savePath = file.getPath() + File.separator + fileName;
            File savefile = new File(savePath);
            FileCopyUtils.copy(mf.getBytes(), savefile);
            String dbpath = bizPath + File.separator + nowday + File.separator + fileName;
            if (dbpath.contains("\\")) {
                dbpath = dbpath.replace("\\", "/");
            }
            fileInfoVo.setDbpath(dbpath);
            fileInfoVo.setPath(savePath);
            fileInfoVo.setName(name);
            fileInfoVo.setOrName(orgName);
            fileInfoVo.setNewName(fileName);
        } catch (IOException e) {
            log.info("saveUploadFileError:", e);
            fileInfoVo = null;
        }
        return fileInfoVo;
    }

    /**
     * 解压到指定目录
     * @param zipPath
     * @param descDir
     */
    public static void unZipFiles(String zipPath, String descDir) throws IOException{
        unZipFiles(new File(zipPath), descDir);
    }

    /**
     * 解压文件到指定目录
     * 解压后的文件名，和之前一致
     * @param zipFile    待解压的zip文件
     * @param descDir    指定目录
     */
    @SuppressWarnings("rawtypes")
    public static String unZipFiles(File zipFile, String descDir)  {

        ZipFile zip = null;//解决中文文件夹乱码
        try {

            // windows下解压需要GBK，LINUX解压需要uft8，还未判断系统
//            zip = new ZipFile(zipFile, Charset.forName("UTF-8"));
            zip = new ZipFile(zipFile, Charset.forName("GBK"));
            log.info("zip---:{}:{}:{}", zip.getName(), zip.size(), zip.entries());
            File pathFile = new File(descDir);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {
                ZipEntry entry = entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zip.getInputStream(entry);
                String outPath = (descDir +"/"+ zipEntryName).replaceAll("\\*", "/");
                // 判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                if (!file.exists()) {
                    file.mkdirs();
                }
                // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }
                FileOutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }
                in.close();
                out.close();
            }
            log.info("******************解压完毕********************");
            // 解压完成后返回文件夹地址
            return descDir + File.separator;
        } catch (IOException e) {
            throw new UBootException("文件解压失败，请上传压缩文件！", e);
        }

    }

    public static String getFileName(String zipPath) {
        String reg = "([^<>/\\\\|:\"\"\\*\\?]+)\\.\\w+$+";
        Matcher m = Pattern.compile(reg).matcher(zipPath); //uri为需要匹配的路径
        String filename =  null;
        if (m.find()) {
            filename = m.group(1);
        }
        return filename;
    }

    /**
     * 递归获取所有文件
     * 解决 mac 的压缩包会出现__MACOSX和文件夹目录
     *
     * @param strPath
     * @return
     */
    public static List<File> getFileList(List<File> batchBizFiles, String strPath) {
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(batchBizFiles, files[i].getAbsolutePath()); // 获取文件绝对路径
                } else {
                    batchBizFiles.add(files[i]);
                }
            }

        }
        return batchBizFiles;
    }

    public static void main(String[] args) {
        System.out.println(getFileName("/kkk/dfd.zip"));
    }
}
