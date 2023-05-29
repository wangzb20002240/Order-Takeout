package top.endant.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.endant.reggie.common.CustomException;
import top.endant.reggie.dto.SetmealDto;
import top.endant.reggie.entity.DishFlavor;
import top.endant.reggie.entity.Setmeal;
import top.endant.reggie.entity.SetmealDish;
import top.endant.reggie.mapper.SetmealDishMapper;
import top.endant.reggie.mapper.SetmealMapper;
import top.endant.reggie.service.DishService;
import top.endant.reggie.service.SetmealDishService;
import top.endant.reggie.service.SetmealService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto,setmeal);
        this.save(setmeal);
        Long id = setmeal.getId();
        setmealDishService.saveBatch(setmealDishes.stream().map((item) -> {
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList()));
    }

    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        Long id = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        setmealDishService.remove(queryWrapper);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setId(null);
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    public void deleteWithDish(String ids) {
        String[] idsSplit = ids.split(",");
        //查询套餐状态，如果是启售中则不能删除
        LambdaQueryWrapper<Setmeal> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Setmeal::getStatus,1);
        queryWrapper1.in(Setmeal::getId, Arrays.asList(idsSplit));

        int count = this.count(queryWrapper1);
        if (count>0) throw new CustomException("选中套餐启售中不能删除");

        this.removeByIds(Arrays.asList(idsSplit));
        //淘汰的写法
        /*for (String id : idsSplit) {
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId, id);
            setmealDishService.remove(queryWrapper);
        }*/
        //事实上生产开发中不推荐使用in语句，推荐使用exists代替，但是此处其实没有连接到多表exists不方便;
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,Arrays.asList(idsSplit));
        setmealDishService.remove(queryWrapper);
    }

    public SetmealDto getWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }
}
