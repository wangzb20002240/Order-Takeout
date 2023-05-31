package top.endant.orderTakeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.endant.orderTakeout.entity.User;
import top.endant.orderTakeout.mapper.UserMapper;
import top.endant.orderTakeout.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
