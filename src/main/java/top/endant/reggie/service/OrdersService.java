package top.endant.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.core.annotation.Order;
import top.endant.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
