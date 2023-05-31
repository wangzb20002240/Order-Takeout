package top.endant.orderTakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.endant.orderTakeout.common.R;
import top.endant.orderTakeout.entity.ShoppingCart;
import top.endant.orderTakeout.service.ShoppingCartService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpServletRequest request) {
        String userId = request.getSession().getAttribute("user").toString();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        return R.success(shoppingCartService.list(queryWrapper));
    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart, HttpServletRequest request) {
        String userId = request.getSession().getAttribute("user").toString();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId)
                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one.getNumber() == 1) {
            shoppingCartService.removeById(one);
            return R.success("删除成功");
        }
        one.setNumber(one.getNumber() - 1);
        shoppingCartService.updateById(one);
        return R.success("减少成功");
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart, HttpServletRequest request) {
        String userId = request.getSession().getAttribute("user").toString();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId)
                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one != null) {
            one.setNumber(one.getNumber() + 1);
            shoppingCartService.updateById(one);
            return R.success("增加成功");
        }
        shoppingCart.setUserId(Long.valueOf(userId));
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartService.save(shoppingCart);
        return R.success("添加成功");
    }

    @DeleteMapping("/clean")
    public R<String> clean(HttpServletRequest request) {
        String userId = request.getSession().getAttribute("user").toString();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空成功");
    }

}
