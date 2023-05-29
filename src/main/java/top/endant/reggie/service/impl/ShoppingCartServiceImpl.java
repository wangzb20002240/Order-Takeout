package top.endant.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.endant.reggie.entity.ShoppingCart;
import top.endant.reggie.mapper.ShoppingCartMapper;
import top.endant.reggie.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
