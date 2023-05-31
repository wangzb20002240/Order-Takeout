package top.endant.orderTakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.endant.orderTakeout.common.R;
import top.endant.orderTakeout.dto.OrdersDto;
import top.endant.orderTakeout.entity.OrderDetail;
import top.endant.orderTakeout.entity.Orders;
import top.endant.orderTakeout.service.OrderDetailService;
import top.endant.orderTakeout.service.OrdersService;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    //会有报错，还有时间的问题
    @GetMapping("/page")
    public R<Page<Orders>> page(int page, int pageSize, String number, String beginTime, String endTime) {

        Page<Orders> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number != null, Orders::getNumber, number);
        queryWrapper.between(beginTime != null && endTime != null, Orders::getOrderTime, beginTime, endTime);
        Page<Orders> page1 = ordersService.page(p, queryWrapper);
        log.info(page1.toString());
        return R.success(page1);
    }

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page<OrdersDto>> page(int page, int pageSize) {
        Page<Orders> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(p, queryWrapper);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(p,ordersDtoPage,"records");
        ordersDtoPage.setRecords(p.getRecords().stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper =new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderDetail::getOrderId,item.getId());
            ordersDto.setOrderDetails(orderDetailService.list(lambdaQueryWrapper));
            return ordersDto;
        }).collect(Collectors.toList()));
        return R.success(ordersDtoPage);
    }
}
