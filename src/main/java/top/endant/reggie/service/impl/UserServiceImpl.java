package top.endant.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.endant.reggie.entity.User;
import top.endant.reggie.mapper.UserMapper;
import top.endant.reggie.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
