package top.endant.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import top.endant.reggie.common.R;
import top.endant.reggie.dto.DishDto;
import top.endant.reggie.dto.SetmealDto;
import top.endant.reggie.entity.Dish;
import top.endant.reggie.entity.DishFlavor;
import top.endant.reggie.entity.Setmeal;
import top.endant.reggie.entity.SetmealDish;
import top.endant.reggie.service.CategoryService;
import top.endant.reggie.service.DishService;
import top.endant.reggie.service.SetmealDishService;
import top.endant.reggie.service.SetmealService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/setmeal")
@Api(tags = "套餐接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @ApiOperation(value = "分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true),
            @ApiImplicitParam(name = "name",value = "套餐名称",required = false)
    })
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<Setmeal> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(p, queryWrapper);
        Page<SetmealDto> p1 = new Page<>();
        BeanUtils.copyProperties(p, p1, "records");
        p1.setRecords(p.getRecords().stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            setmealDto.setCategoryName(categoryService.getById(item.getCategoryId()).getName());
            return setmealDto;
        }).collect(Collectors.toList()));
        return R.success(p1);
    }

    @CacheEvict(value = "setmealCache", allEntries = true)
    @DeleteMapping
    public R<String> delete(String ids) {
        //TODO:此处ids的接受方式可以改成 (@RequestParam List<Long> ids)
        setmealService.deleteWithDish(ids);
        return R.success("删除成功");
    }

    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping("/status/{status}")
    public R<String> statusChange(@PathVariable Integer status, String ids) {
        String[] idsSplit = ids.split(",");
        List<Setmeal> setmeals = setmealService.listByIds(Arrays.asList(idsSplit));
        setmealService.updateBatchById(setmeals.stream().map((item) -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList()));
        return status == 1 ? R.success("启售成功") : R.success("停售成功");
    }

    @CacheEvict(value = "setmealCache", allEntries = true)
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("更新套餐成功");
    }

    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新建套餐成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getWithDish(id);
        return R.success(setmealDto);
    }

    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> getDish(@PathVariable Long id) {
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        return R.success(setmealDishService.list(queryWrapper));
    }

    @Cacheable(value = "setmealCache", key = "#categoryId+'_'+#status")
    @GetMapping("/list")
    public R<List<Setmeal>> getList(Long categoryId, String status) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null, Setmeal::getCategoryId, categoryId);
        queryWrapper.eq(status != null, Setmeal::getStatus, status);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
