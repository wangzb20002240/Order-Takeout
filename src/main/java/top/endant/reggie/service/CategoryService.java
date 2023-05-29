package top.endant.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.endant.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
