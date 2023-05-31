package top.endant.orderTakeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.endant.orderTakeout.entity.OrderDetail;
import top.endant.orderTakeout.mapper.OrderDetailMapper;
import top.endant.orderTakeout.service.OrderDetailService;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
