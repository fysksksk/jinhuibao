package com.bjpowernode.front.controller;

import com.bjpowernode.api.model.RechargeRecord;
import com.bjpowernode.front.view.RespResult;
import com.bjpowernode.front.view.recharge.ResultView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "充值业务")
@RestController // 是@Controller和ResponseBody的结合，返回值默认转为JSON字符串
public class RechargeController extends BaseController {

    // 查询流水
    @ApiOperation(value = "查询某个用户的充值记录")
    @GetMapping("/v1/recharge/records")
    public RespResult queryRechargePage(@RequestHeader("uid") Integer uid,
                                        @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                        @RequestParam(required = false, defaultValue = "6") Integer pageSize) {
        RespResult result = RespResult.fail();
        if (uid != null && uid > 0) {
            List<RechargeRecord> records = rechargeService.queryByUid(uid, pageNo, pageSize);
            result = RespResult.ok();
            result.setList(toView(records));
        }
        return result;
    }

    private List<ResultView> toView(List<RechargeRecord> src) {
        List<ResultView> target = new ArrayList<>();
        src.forEach(record -> {
            target.add(new ResultView(record));
        });
        return target;
    }
}
