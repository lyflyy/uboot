package org.uboot.modules.system.controller;


import com.alicp.jetcache.anno.CacheInvalidate;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.uboot.common.api.vo.Result;
import org.uboot.common.aspect.annotation.PermissionData;
import org.uboot.common.constant.CacheConstant;
import org.uboot.common.system.base.controller.BaseController;
import org.uboot.common.system.query.QueryGenerator;
import org.uboot.common.system.util.JwtUtil;
import org.uboot.common.system.vo.LoginUser;
import org.uboot.common.system.vo.UploadFileInfoVo;
import org.uboot.common.util.UFileUtils;
import org.uboot.modules.system.entity.SysDepart;
import org.uboot.modules.system.model.DepartIdModel;
import org.uboot.modules.system.model.SysDepartModel;
import org.uboot.modules.system.service.ISysDepartService;
import org.uboot.modules.system.util.FindsDepartsChildrenUtil;
import org.uboot.modules.system.vo.SysDepartManagersVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @Description: sys_depart 拥有数据权限的部门操作
 * @Author: jeecg-boot
 * @Date:   2020-02-16
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/sysDepartTree")
@Slf4j
public class SysDepartTreeController extends BaseController<SysDepart, ISysDepartService> {

    @Autowired
    private ISysDepartService sysDepartService;

	/**
	 * 分页列表查询
	 *
	 * @param sysDepart1
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/rootList")
    @PermissionData(pageComponent="soldier/DepartList")
	public Result<?> queryPageList(SysDepart sysDepart1,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if(sysUser.getUsername().equals("admin")){
            return Result.ok(sysDepartService.getAll());
        }else{
            return Result.ok(sysDepartService.getByUser(sysUser.getId()));
        }
	}

    /**
     * 查询数据 添加或编辑页面对该方法发起请求,以树结构形式加载所有部门的名称,方便用户的操作
     *
     * @return
     */
    @RequestMapping(value = "/queryIdTree", method = RequestMethod.GET)
    public Result<List<DepartIdModel>> queryIdTree() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> listResult;
        if(sysUser.getUsername().equals("admin")){
            QueryWrapper<SysDepart> queryWrapper = new QueryWrapper<>();
            List<SysDepart> pageList = sysDepartService.list(queryWrapper);
            listResult = FindsDepartsChildrenUtil.wrapTreeDataToTreeList(pageList);
        }else{
            listResult = sysDepartService.getByUser(sysUser.getId());
        }
        // 封装listResult 为tree结构
        List<DepartIdModel> result = new ArrayList<>();
        handleDepartIdModel(listResult, result);
        Result<List<DepartIdModel>> res = new Result<>();
        try {
            res.setResult(result);
            res.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return res;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
        SysDepart sysDepart1 = sysDepartService.getById(id);
        if(sysDepart1==null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(sysDepart1);
    }

    /**
     * 添加新数据 添加用户新建的部门对象数据,并保存到数据库
     *
     * @param sysDepart
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @CacheInvalidate(name = CacheConstant.SYS_DEPARTS_CACHE, multi = true)
    public Result<SysDepart> add(@RequestBody SysDepartManagersVO sysDepart, HttpServletRequest request) {
        Result<SysDepart> result = new Result<SysDepart>();
        String username = JwtUtil.getUserNameByToken(request);
        try {
            sysDepart.setCreateBy(username);
            sysDepartService.saveDepartData(sysDepart, username);
            // 为管理员赋予相应角色
            if(sysDepart.getUserId() != null && sysDepart.getUserId().size() > 0){
                sysDepartService.toProcessAddMangers(sysDepart);
            }
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑数据 编辑部门的部分数据,并保存到数据库
     *
     * @param sysDepart
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    @CacheInvalidate(name = CacheConstant.SYS_DEPARTS_CACHE, multi = true)
    public Result<SysDepart> edit(@RequestBody SysDepartManagersVO sysDepart, HttpServletRequest request) {
        String username = JwtUtil.getUserNameByToken(request);
        sysDepart.setUpdateBy(username);
        Result<SysDepart> result = new Result<SysDepart>();
        SysDepart sysDepartEntity = sysDepartService.getById(sysDepart.getId());
        if (sysDepartEntity == null) {
            result.error500("未找到对应实体");
        } else {
            // TODO 返回false说明什么？
            if (sysDepartService.updateDepartDataById(sysDepart, username)) {
                // 为管理员赋予相应角色
                if(sysDepart.getUserId() != null && sysDepart.getUserId().size() > 0){
                    sysDepartService.toProcessUpdateMangers(sysDepart);
                }
                result.success("修改成功!");
            }
        }
        return result;
    }

    private void handleDepartIdModel(List<SysDepartModel> listResult, List<DepartIdModel> result){
        if(listResult != null && listResult.size() > 0){
            for (SysDepartModel sysDepartModel : listResult) {
                DepartIdModel d = new DepartIdModel();
                d.convert(sysDepartModel);
                result.add(d);
                if(sysDepartModel.getChildren() != null && sysDepartModel.getChildren().size() > 0){
                    List<DepartIdModel> children = new ArrayList<>();
                    handleDepartIdModel(sysDepartModel.getChildren(), children);
                    d.setChildren(children);
                }
            }
        }
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysDepart sysDepart, HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysDepart> queryWrapper = QueryGenerator.initQueryWrapper(sysDepart, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysDepart> pageList = sysDepartService.list(queryWrapper);
        //按字典排序
        Collections.sort(pageList, new Comparator<SysDepart>() {
            @Override
            public int compare(SysDepart arg0, SysDepart arg1) {
                return arg0.getOrgCode().compareTo(arg1.getOrgCode());
            }
        });
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "部门列表");
        mv.addObject(NormalExcelConstants.CLASS, SysDepart.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("部门列表数据", "导出人:"+user.getRealname(), "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    @CacheInvalidate(name = CacheConstant.SYS_DEPARTS_CACHE, multi = true)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                int num = sysDepartService.importDepart(request, file, params);
	            UploadFileInfoVo fileInfoVo = UFileUtils.saveUploadFileFromMultipartFile(uploadpath, "excel", file);
	            saveImportLog(fileInfoVo, num, SysDepart.class.getSimpleName());
                return Result.ok("文件导入成功！数据行数：" + num);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return Result.error("文件导入失败:"+e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }

}
