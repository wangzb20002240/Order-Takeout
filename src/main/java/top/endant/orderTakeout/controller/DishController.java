package top.endant.orderTakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.endant.orderTakeout.common.R;
import top.endant.orderTakeout.dto.DishDto;
import top.endant.orderTakeout.entity.Dish;
import top.endant.orderTakeout.entity.DishFlavor;
import top.endant.orderTakeout.service.CategoryService;
import top.endant.orderTakeout.service.DishFlavorService;
import top.endant.orderTakeout.service.DishService;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("创建成功");
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = dishService.getWithFlavor(id);
        return R.success(dishDto);
    }

    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        Page<Dish> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        queryWrapper.like(name != null, Dish::getName, name);
        dishService.page(p, queryWrapper);

        //多表查询的业务，代码有点搞，思路就是流拆包再封装，大概。
        Page<DishDto> p1 = new Page<>();
        BeanUtils.copyProperties(p, p1, "records");
        List<Dish> records = p.getRecords();
        List<DishDto> records1 = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            dishDto.setCategoryName(categoryService.getById(item.getCategoryId()).getName());
            return dishDto;
        }).collect(Collectors.toList());
        p1.setRecords(records1);
        return R.success(p1);
    }

    @DeleteMapping
    public R<String> delete(String ids) {
        dishService.deleteWithFlavor(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> statusChange(@PathVariable Integer status, String ids) {
        String[] idsSplit = ids.split(",");
        List<Dish> dishes = dishService.listByIds(Arrays.asList(idsSplit));
        //TODO：检查菜品是否包含在套餐中，如果包含在套餐中且套餐没有停售，则需要先停售套餐再停售菜品
        //代码...
        dishService.updateBatchById(dishes.stream().map((item) -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList()));
        return status == 1 ? R.success("启售成功") : R.success("停售成功");
    }

    /**
     * 这个获取列表是前后台公用的，现在加上redis缓存
     *
     * @param categoryId 菜品分类
     * @param name       查询关键字
     * @param status     状态
     * @return 菜品列表
     */
    @GetMapping("/list")
    @Transactional
    public R<List<DishDto>> getList(Long categoryId, String name, String status) {
        String key = "dish_" + categoryId + "_" + status;
        ValueOperations valueOperations = redisTemplate.opsForValue();
        List<DishDto> dishDtoList = (List<DishDto>) valueOperations.get(key);
        if (dishDtoList != null && !dishDtoList.isEmpty()) {
            return R.success(dishDtoList);
        }
        //用      stringRedisTemplate 写的好复杂  气死我了
//        ListOperations<String, String> redisList = stringRedisTemplate.opsForList();
//        HashOperations<String, Object, Object> redisHash = stringRedisTemplate.opsForHash();
//        //由于前台查询数据必然是不会有name且会有categoryId和status所以可以据此判断
//        if (categoryId != null && name == null && status != null) {
//            log.info("-------------从redis中获取数据-------------");
//            //从redis中查询数据
//            //一个列表对一个菜品分类，对应很多菜品
//            //一个菜品对应很多属性 键值对
//            //口味属性对应一个列表
//            List<String> dishIdList = redisList.range(categoryId.toString(), 0, -1);
//            if (dishIdList != null && !dishIdList.isEmpty()) {
//                log.info("-------------获取到菜品id列表-------------");
//                log.info(dishIdList.toString());
//                List<DishDto> dishDtoList = new ArrayList<>();
//                for (String dishId : dishIdList) {
//                    DishDto dishDto = new DishDto();
//                    dishDto.setId(Long.valueOf(dishId));
//                    dishDto.setName((String) redisHash.get(dishId, "name"));
//                    dishDto.setCategoryId(Long.valueOf((String) Objects.requireNonNull(redisHash.get(dishId, "categoryId"))));
//                    dishDto.setPrice((BigDecimal.valueOf(Double.parseDouble((String) Objects.requireNonNull(redisHash.get(dishId, "price"))))));
//                    dishDto.setStatus(Integer.valueOf((String) Objects.requireNonNull(redisHash.get(dishId, "status"))));
//                    dishDto.setSort(Integer.valueOf((String) Objects.requireNonNull(redisHash.get(dishId, "sort"))));
//                    dishDto.setCode((String) redisHash.get(dishId, "code"));
//                    dishDto.setImage((String) redisHash.get(dishId, "image"));
//
//                    Map<Object, Object> flavors = redisHash.entries("Flavor" + dishId);
//                    List<DishFlavor> dishFlavorList = new ArrayList<>();
//                    for (Object key : flavors.keySet()) {
//                        DishFlavor dishFlavor = new DishFlavor();
//                        dishFlavor.setName((String) key);
//                        dishFlavor.setValue((String) flavors.get(key));
//                        dishFlavorList.add(dishFlavor);
//                    }
//                    dishDto.setFlavors(dishFlavorList);
//                    dishDtoList.add(dishDto);
//                }
//                log.info("-------------获取到菜品全部信息列表-------------");
//                log.info(dishDtoList.toString());
//                return R.success(dishDtoList);
//            }
//        }
        log.info("-------------从mySql数据库中获取数据-------------");
        //redis中没查到数据，或者条件不匹配，则从数据库库中查询数据
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.eq(status != null, Dish::getStatus, status);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);
        dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> list = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(list);
            return dishDto;
        }).collect(Collectors.toList());

        //数据保存到redis
        log.info("------------将数据缓存到redis-------------");
        valueOperations.set(key, dishDtoList, 60, TimeUnit.MINUTES);

//        for (DishDto dishdto : dishDtoList) {
//            String id = dishdto.getId().toString();
//            redisList.leftPush(String.valueOf(categoryId), id);
//
//            redisHash.put(id, "name", dishdto.getName());
//            redisHash.put(id, "categoryId", dishdto.getCategoryId().toString());
//            redisHash.put(id, "price", dishdto.getPrice().toString());
//            redisHash.put(id, "status", dishdto.getStatus().toString());
//            redisHash.put(id, "sort", dishdto.getSort().toString());
//            redisHash.put(id, "code", dishdto.getCode());
//            redisHash.put(id, "image", dishdto.getImage());
//
//            for (DishFlavor flavor : dishdto.getFlavors()) {
//                redisHash.put("Flavor" + id, flavor.getName(), flavor.getValue());
//            }
//        }

        return R.success(dishDtoList);
    }
}
