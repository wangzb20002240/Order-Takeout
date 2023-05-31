package top.endant.orderTakeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.endant.orderTakeout.common.CustomException;
import top.endant.orderTakeout.entity.Category;
import top.endant.orderTakeout.entity.Dish;
import top.endant.orderTakeout.entity.Setmeal;
import top.endant.orderTakeout.mapper.CategoryMapper;
import top.endant.orderTakeout.service.CategoryService;
import top.endant.orderTakeout.service.DishService;
import top.endant.orderTakeout.service.SetmealService;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    public void remove(Long id){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, id);
        int c = dishService.count(queryWrapper);
        if (c > 0) throw new CustomException("该类别已关联菜品，无法删除");
        LambdaQueryWrapper<Setmeal> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Setmeal::getCategoryId, id);
        c = setmealService.count(queryWrapper1);
        if (c > 0) throw new CustomException("该类别已关联套餐，无法删除");

        super.removeById(id);
    }
}
