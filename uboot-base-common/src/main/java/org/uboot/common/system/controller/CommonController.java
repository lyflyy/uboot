package org.uboot.common.system.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.uboot.common.api.vo.Result;
import org.uboot.common.system.vo.UploadFileInfoVo;
import org.uboot.common.util.UFileUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Slf4j
@RestController
@RequestMapping("/sys/common")
public class CommonController {

	@Value(value = "${uboot.path.upload}")
	private String uploadpath;

	/**
	 * @Author 政辉
	 * @return
	 */
	@GetMapping("/403")
	public Result<?> noauth()  {
		return Result.error("没有权限，请联系管理员授权");
	}

	@PostMapping(value = "/upload")
	public Result<?> upload(HttpServletRequest request, HttpServletResponse response) {
		Result<Object> result = new Result<>();
		String bizPath = "files";
		UploadFileInfoVo fileInfoVo = UFileUtils.saveUploadFile(uploadpath, bizPath,request);
		if (fileInfoVo != null) {
			result.setResult(fileInfoVo);
			result.setMessage(fileInfoVo.getDbpath());
			result.setSuccess(true);
		} else {
			result.setMessage("文件上传失败");
			result.setSuccess(false);
		}
		return result;
	}

	/**
	 * 预览图片
	 * 请求地址：http://localhost:8080/common/view/{user/20190119/e1fe9925bc315c60addea1b98eb1cb1349547719_1547866868179.jpg}
	 *
	 * @param request
	 * @param response
	 */
	@GetMapping(value = "/view/**")
	public void view(HttpServletRequest request, HttpServletResponse response) {
		// ISO-8859-1 ==> UTF-8 进行编码转换
		String imgPath = extractPathFromPattern(request);
		// 其余处理略
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			imgPath = imgPath.replace("..", "");
			if (imgPath.endsWith(",")) {
				imgPath = imgPath.substring(0, imgPath.length() - 1);
			}
			response.setContentType("image/jpeg;charset=utf-8");
			String localPath = uploadpath;
			String imgurl = localPath + File.separator + imgPath;
			inputStream = new BufferedInputStream(new FileInputStream(imgurl));
			outputStream = response.getOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				outputStream.write(buf, 0, len);
			}
			response.flushBuffer();
		} catch (IOException e) {
			log.error("预览图片失败" + e.getMessage());
			// e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}

	}

	/**
	 * 下载文件
	 * 请求地址：http://localhost:8080/common/download/{user/20190119/e1fe9925bc315c60addea1b98eb1cb1349547719_1547866868179.jpg}
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@GetMapping(value = "/download/**")
	public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// ISO-8859-1 ==> UTF-8 进行编码转换
		String filePath = extractPathFromPattern(request);
		// 其余处理略
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			filePath = filePath.replace("..", "");
			if (filePath.endsWith(",")) {
				filePath = filePath.substring(0, filePath.length() - 1);
			}
			String localPath = uploadpath;
			String downloadFilePath = localPath + File.separator + filePath;
			File file = new File(downloadFilePath);
	         if (file.exists()) {
	        	 response.setContentType("application/force-download");// 设置强制下载不打开            
	 			response.addHeader("Content-Disposition", "attachment;fileName=" + new String(file.getName().getBytes("UTF-8"),"iso-8859-1"));
	 			inputStream = new BufferedInputStream(new FileInputStream(file));
	 			outputStream = response.getOutputStream();
	 			byte[] buf = new byte[1024];
	 			int len;
	 			while ((len = inputStream.read(buf)) > 0) {
	 				outputStream.write(buf, 0, len);
	 			}
	 			response.flushBuffer();
	         }

		} catch (Exception e) {
			log.info("文件下载失败" + e.getMessage());
			// e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	/**
	 * @功能：pdf预览Iframe
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping("/pdf/pdfPreviewIframe")
	public ModelAndView pdfPreviewIframe(ModelAndView modelAndView) {
		modelAndView.setViewName("pdfPreviewIframe");
		return modelAndView;
	}

	/**
	  *  把指定URL后的字符串全部截断当成参数
	  *  这么做是为了防止URL中包含中文或者特殊字符（/等）时，匹配不了的问题
	 * @param request
	 * @return
	 */
	private static String extractPathFromPattern(final HttpServletRequest request) {
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
	}

}
