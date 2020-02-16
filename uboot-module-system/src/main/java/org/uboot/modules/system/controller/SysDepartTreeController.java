package org.uboot.modules.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.uboot.common.api.vo.Result;
import org.uboot.common.aspect.annotation.PermissionData;
import org.uboot.common.system.base.controller.BaseController;
import org.uboot.common.system.query.QueryGenerator;
import org.uboot.common.system.vo.LoginUser;
import org.uboot.modules.system.entity.SysDepart;
import org.uboot.modules.system.model.DepartIdModel;
import org.uboot.modules.system.model.SysDepartTreeModel;
import org.uboot.modules.system.model.SysDepartTreeWithManagerModel;
import org.uboot.modules.system.service.ISysDepartService;
import org.uboot.modules.system.util.FindsDepartsChildrenUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            QueryWrapper<SysDepart> queryWrapper = QueryGenerator.initQueryWrapper(sysDepart1, req.getParameterMap());
            List<SysDepart> pageList = sysDepartService.list(queryWrapper);
            List<SysDepartTreeWithManagerModel> listResult = FindsDepartsChildrenUtil.wrapTreeDataToTreeList(pageList);
            return Result.ok(listResult);
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
        List<SysDepartTreeWithManagerModel> listResult;
        if(sysUser.getUsername().equals("admin")){
            QueryWrapper<SysDepart> queryWrapper = QueryGenerator.initQueryWrapper(null, null);
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

    private void handleDepartIdModel(List<SysDepartTreeWithManagerModel> listResult, List<DepartIdModel> result){
        if(listResult != null && listResult.size() > 0){
            for (SysDepartTreeWithManagerModel sysDepartTreeModel : listResult) {
                DepartIdModel d = new DepartIdModel();
                d.convert(sysDepartTreeModel);
                result.add(d);
                if(sysDepartTreeModel.getChildren() != null && sysDepartTreeModel.getChildren().size() > 0){
                    List<DepartIdModel> children = new ArrayList<>();
                    handleDepartIdModel(sysDepartTreeModel.getChildren(), children);
                    d.setChildren(children);
                }
            }
        }
    }

}
