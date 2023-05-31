package top.endant.orderTakeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.endant.orderTakeout.common.CustomException;
import top.endant.orderTakeout.dto.DishDto;
import top.endant.orderTakeout.entity.Dish;
import top.endant.orderTakeout.entity.DishFlavor;
import top.endant.orderTakeout.mapper.DishMapper;
import top.endant.orderTakeout.service.DishFlavorService;
import top.endant.orderTakeout.service.DishService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    private void deleteRedisData(Long id) {
        Dish dish = this.getById(id);
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        redisTemplate.delete(key);

        //以下是清除StringRedisTemplate的方式 气死我了
//        ListOperations<String, String> redisList = stringRedisTemplate.opsForList();
//        //根据dishId找到categoryId
//        Long categoryId = this.getById(id).getCategoryId();
//        //删除数据
//        List<String> dishIds = redisList.range(categoryId.toString(), 0, -1);
//        if (dishIds != null) {
//            log.info("---------------后台数据更新，redis中缓存的菜品数据清理中...---------------");
//            //清理菜品列表
//            stringRedisTemplate.delete(String.valueOf(categoryId));
//            for (String dishId : dishIds) {
//                //清理每个菜品的信息
//                stringRedisTemplate.delete(dishId);
//                //清理口味数据表
//                stringRedisTemplate.delete("Flavor" + dishId);
//            }
//            log.info("---------------redis中缓存的菜品数据清理完毕---------------");
//        }
    }

    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        //应该被淘汰的写法
        /*for (DishFlavor flavor : dishDto.getFlavors()) {
            flavor.setId(dishDto.getId());
            dishFlavorService.save(flavor);
        }*/
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().peek((item) -> item.setDishId(id)).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

        //清理redis缓存
        this.deleteRedisData(id);
    }

    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        Long id = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().peek((item) -> {
            item.setId(null);//因为采用了逻辑删除，所以id需要随机生成，不能沿用之前的id
            item.setDishId(id);
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

        //清理redis缓存
        this.deleteRedisData(id);
    }

    @Transactional
    public void deleteWithFlavor(String ids) {
        String[] idsSplit = ids.split(",");

        //查询菜品状态，如果是启售中则不能删除
        LambdaQueryWrapper<Dish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Dish::getStatus, 1);
        queryWrapper1.in(Dish::getId, Arrays.asList(idsSplit));

        int count = this.count(queryWrapper1);
        if (count > 0) throw new CustomException("选中菜品启售中不能删除");
        //TODO：检查菜品是否包含在套餐中，如果包含在套餐中则不能删除
        //代码...


        //先清理redis缓存，否则查不到菜品分类数据
        for (String id : idsSplit) {
            this.deleteRedisData(Long.valueOf(id));
        }

        //删除mySql中的数据
        this.removeByIds(Arrays.asList(idsSplit));
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, Arrays.asList(idsSplit));
        dishFlavorService.remove(queryWrapper);
    }

    public DishDto getWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish, dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        dishDto.setFlavors(dishFlavorService.list(queryWrapper));
        return dishDto;
    }
}
