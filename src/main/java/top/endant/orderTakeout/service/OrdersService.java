package top.endant.orderTakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.endant.orderTakeout.entity.Orders;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
