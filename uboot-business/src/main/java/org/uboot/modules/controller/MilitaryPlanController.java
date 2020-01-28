package org.uboot.modules.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.uboot.common.api.vo.Result;
import org.uboot.common.system.base.controller.BaseController;
import org.uboot.common.system.query.QueryGenerator;
import org.uboot.common.aspect.annotation.AutoLog;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.uboot.modules.entity.MilitaryPlan;
import org.uboot.modules.service.IMilitaryPlanService;

/**
 * @Description: 方案表
 * @Author: jeecg-boot
 * @Date:   2020-01-27
 * @Version: V1.0
 */
@Slf4j
@Api(tags="方案表")
@RestController
@RequestMapping("/modules/militaryPlan")
public class MilitaryPlanController extends BaseController<MilitaryPlan, IMilitaryPlanService> {
	@Autowired
	private IMilitaryPlanService militaryPlanService;

	/**
	 * 分页列表查询
	 *
	 * @param militaryPlan
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "方案表-分页列表查询")
	@ApiOperation(value="方案表-分页列表查询", notes="方案表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(MilitaryPlan militaryPlan,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<MilitaryPlan> queryWrapper = QueryGenerator.initQueryWrapper(militaryPlan, req.getParameterMap());
		Page<MilitaryPlan> page = new Page<MilitaryPlan>(pageNo, pageSize);
		IPage<MilitaryPlan> pageList = militaryPlanService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param militaryPlan
	 * @return
	 */
	@AutoLog(value = "方案表-添加")
	@ApiOperation(value="方案表-添加", notes="方案表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody MilitaryPlan militaryPlan) {
		militaryPlanService.save(militaryPlan);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param militaryPlan
	 * @return
	 */
	@AutoLog(value = "方案表-编辑")
	@ApiOperation(value="方案表-编辑", notes="方案表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody MilitaryPlan militaryPlan) {
		militaryPlanService.updateById(militaryPlan);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "方案表-通过id删除")
	@ApiOperation(value="方案表-通过id删除", notes="方案表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		militaryPlanService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "方案表-批量删除")
	@ApiOperation(value="方案表-批量删除", notes="方案表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.militaryPlanService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "方案表-通过id查询")
	@ApiOperation(value="方案表-通过id查询", notes="方案表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		MilitaryPlan militaryPlan = militaryPlanService.getById(id);
		return Result.ok(militaryPlan);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param militaryPlan
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, MilitaryPlan militaryPlan) {
      return super.exportXls(request, militaryPlan, MilitaryPlan.class, "方案表");
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
      return super.importExcel(request, response, MilitaryPlan.class);
  }

}
