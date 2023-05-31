package top.endant.orderTakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.endant.orderTakeout.common.R;
import top.endant.orderTakeout.entity.User;
import top.endant.orderTakeout.service.UserService;
//import top.endant.orderTakeout.utils.SMSUtils;
//import top.endant.orderTakeout.utils.SendMailUtils;
import top.endant.orderTakeout.utils.ValidateCodeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/code")
    public R<String> code(@RequestBody User user, HttpServletRequest servletRequest) throws Exception {
        //可能需要加上phoneNumber为空的校验
        Integer code = ValidateCodeUtils.generateValidateCode(4);
        log.info("phone:{}", user.getPhone());
        log.info("code:{}", code);
        //理论上code应该是有时限的 而不是简单存储在Session里 现在修改了采用redis存储
        HttpSession session = servletRequest.getSession();
        session.setAttribute("phone", user.getPhone());
//        session.setAttribute("code", code);
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(user.getPhone(), String.valueOf(code), 5L, TimeUnit.MINUTES);
        //短信发送
        //SMSUtils.send(phoneNumber, String.valueOf(code));
        //邮件发送
        //SendMailUtils.send("752239019@qq.com", String.valueOf(code),"endant");//邮箱地址应该是输入的，用户名也应该是输入的
        return R.success("success");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpServletRequest servletRequest) {
        //可能需要加上phoneNumber和验证码为空的校验，这里不写了
        HttpSession session = servletRequest.getSession();
        String phone = session.getAttribute("phone").toString();
//        String code = session.getAttribute("code").toString();
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        String code = valueOperations.get(phone);
        if (map.get("code").equals(code) && map.get("phone").equals(phone)) {
            //判断是否有此用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User u = userService.getOne(queryWrapper);
            if (u == null) {//新建用户
                u = new User();
                u.setPhone(phone);
                u.setName("用户" + phone);
                userService.save(u);
            }
            //设置session为登录状态
            session.setAttribute("user", u.getId());
            log.info("---用户ID：" + u.getId() + "登录系统---");
            //登录成功删除redis的验证码数据
            stringRedisTemplate.delete(phone);
            return R.success(u);
        }
        return R.error("验证码错误");
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        log.info("---用户ID：" + request.getSession().getAttribute("user") + "登出系统---");
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
