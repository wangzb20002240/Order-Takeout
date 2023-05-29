package top.endant.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.endant.reggie.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
