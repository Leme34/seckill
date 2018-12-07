package com.lee.seckill.web;

import com.lee.seckill.bean.Seckill;
import com.lee.seckill.dto.Exposer;
import com.lee.seckill.dto.SeckillExecution;
import com.lee.seckill.dto.SeckillResult;
import com.lee.seckill.enums.SeckillStateEnum;
import com.lee.seckill.exception.RepeatKillException;
import com.lee.seckill.exception.SeckillCloseException;
import com.lee.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("seckill")
public class SeckillController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SeckillService seckillService;

    /**
     * 商品列表页面
     */
    @GetMapping("/list")
    public String list(Model model) {
        List<Seckill> seckillList = seckillService.getAllsecSeckills();
        model.addAttribute("list", seckillList);
        return "list";
    }

    /**
     * 商品详细页面
     */
    @GetMapping("/{seckillId}/detail")
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        //返回列表页
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        //显示此商品信息
        Seckill seckill = seckillService.getById(seckillId);
        //若不存在此商品
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    /**
     * ajax请求接口
     * 获取暴露秒杀接口地址
     */
    @ResponseBody
    @PostMapping(value = "/{seckillId}/exposer", produces = "application/json;charset=UTF-8")
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult result;
        //捕获service层抛出的不同异常返回不同的结果
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = new SeckillResult(false, e.getMessage());
        }
        return result;
    }

    /**
     * ajax请求接口
     * 秒杀
     */
    @ResponseBody
    @PostMapping(value = "/{seckillId}/{md5}/execution", produces = "application/json;charset=UTF-8")
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   //非必需,若cookie中没有也不报错
                                                   @CookieValue(value = "killPhone", required = false) Long killPhone) {
        //cookie没有则未登录
        if (killPhone == null) {
            return new SeckillResult(false, "未登录");
        }

        //捕获service层抛出的不同异常返回不同的结果
        try {
            //秒杀成功
//            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, killPhone, md5);
            //TODO 优化点3、使用mysql存储过程执行秒杀操作，减少网络延时和GC造成的行级锁等待时间
            SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(seckillId, killPhone, md5);
            return new SeckillResult(true,seckillExecution);
        }catch (SeckillCloseException e){
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult(true,seckillExecution);
        }catch (RepeatKillException e){
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult(true,seckillExecution);
        }catch (Exception e){
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult(true,seckillExecution);
        }
    }

    /**
     * ajax请求接口
     * 返回当前系统时间
     */
    @ResponseBody
    @GetMapping("/time/now")
    public SeckillResult<Long> time(){
        Long now = new Date().getTime();
        return new SeckillResult<Long>(true,now);
    }

}
