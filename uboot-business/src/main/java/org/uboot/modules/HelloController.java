
package org.uboot.modules;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uboot.common.api.vo.Result;

@Slf4j
@Api(tags="新建module--jm")
@RestController
@RequestMapping("/hello")
public class HelloController  {
	@GetMapping(value="/")
    @ApiOperation("测试hello方法")
	public Result<String> hello(){
		Result<String> result = new Result<String>();
		result.setResult("hello word!");
		result.setSuccess(true);
		return result;
	}
}
