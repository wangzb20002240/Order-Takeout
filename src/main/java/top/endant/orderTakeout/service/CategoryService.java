package top.endant.orderTakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.endant.orderTakeout.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
