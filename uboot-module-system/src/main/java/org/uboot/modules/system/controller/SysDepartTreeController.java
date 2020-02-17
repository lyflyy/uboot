package org.uboot.modules.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import org.uboot.common.api.vo.Result;
import org.uboot.common.aspect.annotation.PermissionData;
import org.uboot.common.constant.CacheConstant;
import org.uboot.common.system.base.controller.BaseController;
import org.uboot.common.system.query.QueryGenerator;
import org.uboot.common.system.util.JwtUtil;
import org.uboot.common.system.vo.LoginUser;
import org.uboot.modules.system.entity.SysDepart;
import org.uboot.modules.system.model.DepartIdModel;
import org.uboot.modules.system.model.SysDepartModel;
import org.uboot.modules.system.model.SysDepartTreeWithManagerModel;
import org.uboot.modules.system.service.*;
import org.uboot.modules.system.util.FindsDepartsChildrenUtil;
import org.uboot.modules.system.vo.SysDepartManagersVO;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
    @CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
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
    @CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
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

}
