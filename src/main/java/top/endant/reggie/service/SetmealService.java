package top.endant.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.endant.reggie.dto.DishDto;
import top.endant.reggie.dto.SetmealDto;
import top.endant.reggie.entity.Setmeal;
import top.endant.reggie.entity.SetmealDish;

public interface SetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);

    void updateWithDish(SetmealDto setmealDto);

    void deleteWithDish(String ids);


    SetmealDto getWithDish(Long id);
}
