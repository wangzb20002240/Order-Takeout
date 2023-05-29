package top.endant.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.endant.reggie.entity.Dish;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
