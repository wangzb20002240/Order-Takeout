package top.endant.orderTakeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.endant.orderTakeout.entity.Dish;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
