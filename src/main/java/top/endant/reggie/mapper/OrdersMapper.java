package top.endant.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.endant.reggie.entity.Orders;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
