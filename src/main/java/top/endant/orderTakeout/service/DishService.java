package top.endant.orderTakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.endant.orderTakeout.dto.DishDto;
import top.endant.orderTakeout.entity.Dish;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    void updateWithFlavor(DishDto dishDto);

    void deleteWithFlavor(String ids);

    DishDto getWithFlavor(Long id);

}
