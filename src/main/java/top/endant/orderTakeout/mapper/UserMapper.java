package top.endant.orderTakeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.endant.orderTakeout.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
