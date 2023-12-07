package com.bjpowernode.front.controller;

import com.bjpowernode.api.model.User;
import com.bjpowernode.common.constants.RedisKey;
import com.bjpowernode.common.util.CommonUtil;
import com.bjpowernode.front.view.RespResult;
import com.bjpowernode.front.view.invest.RankView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 投资功能
 */
@Api(tags = "投资理财产品")
@RestController // 是@Controller和ResponseBody的结合，返回值默认转为JSON字符串
public class InvestController extends BaseController{

    // 投资排行榜
    @ApiOperation(value = "投资排行榜", notes = "显示投资金额最高的3位用户信息")
    @GetMapping("/v1/invest/rank")
    public RespResult showInvestRank() {
        // 从redis中查询数据
        Set<ZSetOperations.TypedTuple<String>> sets = stringRedisTemplatem
                .boundZSetOps(RedisKey.KEY_INVEST_RANK).reverseRangeWithScores(0, 2);

        List<RankView> rankList = new ArrayList<>();
        sets.forEach(tuple -> {
            rankList.add(new RankView(CommonUtil.tuoMinPhone(tuple.getValue()), tuple.getScore()));
        });

        RespResult result = RespResult.ok();
        result.setList(rankList);
        return result;
    }

    // 购买理财产品，更新投资排行版
    @ApiOperation(value = "投资理财产品")
    @PostMapping("/v1/invest/product")
    public RespResult investProduct(
            @RequestParam("uid") Integer uid,
            @RequestParam("productId") Integer productId,
            @RequestParam("money")BigDecimal money
            ) {
        RespResult result = RespResult.fail();
        // 1、检查基本参数
        if ((uid != null && uid > 0) && (productId != null && productId > 0)
            && (money != null)) {
            int investResult = investService.investProduct(uid, productId, money);
            switch (investResult) {
                case 0:
                    result.setMsg("投资数据不正确");
                    break;
                case 1:
                    result = RespResult.ok();
                    modifyInvestRank(uid, money);
                    break;
                case 2:
                    result.setMsg("资金账号不存在");
                    break;
                case 3:
                    result.setMsg("资金不足");
                    break;
                case 4:
                    result.setMsg("理财产品不存在");
                    break;
                case 5:
                    result.setMsg("产品剩余可投资金额不足");
            }
        }
        return result;
    }

    // 更新投资排行榜
    private void modifyInvestRank(Integer uid, BigDecimal money) {
        User user = userService.queryById(uid);
        // 更新redis中的投资排行榜
        String key = RedisKey.KEY_INVEST_RANK;
        stringRedisTemplatem.boundZSetOps(key).incrementScore(user.getPhone(), money.doubleValue());
    }
}
