package top.endant.orderTakeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.endant.orderTakeout.entity.DishFlavor;
import top.endant.orderTakeout.mapper.DishFlavorMapper;
import top.endant.orderTakeout.service.DishFlavorService;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
