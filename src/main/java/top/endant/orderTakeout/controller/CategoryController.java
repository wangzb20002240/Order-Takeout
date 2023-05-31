package top.endant.orderTakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.endant.orderTakeout.common.R;
import top.endant.orderTakeout.entity.Category;
import top.endant.orderTakeout.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("创建成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    public R<Category> getById(@PathVariable Long id) {
        Category c = categoryService.getById(id);
        return c != null ? R.success(c) : R.error(null);
    }

    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {
        Page<Category> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(p, queryWrapper);
        return R.success(p);
    }

    @DeleteMapping
    public R<String> delete(Long ids) {
        //检查是否有关联
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Integer type) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(type != null, Category::getType, type);
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return !list.isEmpty() ? R.success(list) : R.error("菜品分类列表为空");
    }
}
