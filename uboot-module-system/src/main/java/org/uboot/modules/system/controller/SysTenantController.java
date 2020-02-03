package org.uboot.modules.system.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.uboot.common.api.vo.Result;
import org.uboot.common.system.base.controller.BaseController;
import org.uboot.common.system.query.QueryGenerator;
import org.uboot.common.aspect.annotation.AutoLog;
import org.uboot.common.util.oConvertUtils;
import org.uboot.modules.system.entity.SysTenant;
import org.uboot.modules.system.service.ISysTenantService;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 租户
 * @Author: uboot-boot
 * @Date:   2020-02-03
 * @Version: V1.0
 */
@Slf4j
@Api(tags="租户")
@RestController
@RequestMapping("/sys/sysTenant")
public class SysTenantController extends BaseController<SysTenant, ISysTenantService> {
	@Autowired
	private ISysTenantService sysTenantService;

	/**
	 * 分页列表查询
	 *
	 * @param sysTenant
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "租户-分页列表查询")
	@ApiOperation(value="租户-分页列表查询", notes="租户-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysTenant sysTenant,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SysTenant> queryWrapper = QueryGenerator.initQueryWrapper(sysTenant, req.getParameterMap());
		Page<SysTenant> page = new Page<SysTenant>(pageNo, pageSize);
		IPage<SysTenant> pageList = sysTenantService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 * todo - 租户在添加的时候需要跟吧超级管理员与租户相关联一下
	 * @param sysTenant
	 * @return
	 */
	@AutoLog(value = "租户-添加")
	@ApiOperation(value="租户-添加", notes="租户-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysTenant sysTenant) {
		sysTenantService.save(sysTenant);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param sysTenant
	 * @return
	 */
	@AutoLog(value = "租户-编辑")
	@ApiOperation(value="租户-编辑", notes="租户-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysTenant sysTenant) {
		sysTenantService.updateById(sysTenant);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "租户-通过id删除")
	@ApiOperation(value="租户-通过id删除", notes="租户-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		sysTenantService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "租户-批量删除")
	@ApiOperation(value="租户-批量删除", notes="租户-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sysTenantService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "租户-通过id查询")
	@ApiOperation(value="租户-通过id查询", notes="租户-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		SysTenant sysTenant = sysTenantService.getById(id);
		return Result.ok(sysTenant);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param sysTenant
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, SysTenant sysTenant) {
      return super.exportXls(request, sysTenant, SysTenant.class, "租户");
  }

  /**
   * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
  public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      return super.importExcel(request, response, SysTenant.class);
  }

}
