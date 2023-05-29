package top.endant.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.endant.reggie.dto.DishDto;
import top.endant.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    void updateWithFlavor(DishDto dishDto);

    void deleteWithFlavor(String ids);

    DishDto getWithFlavor(Long id);

}
