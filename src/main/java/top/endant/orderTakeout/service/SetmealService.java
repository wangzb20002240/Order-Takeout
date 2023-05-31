package top.endant.orderTakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.endant.orderTakeout.dto.SetmealDto;
import top.endant.orderTakeout.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);

    void updateWithDish(SetmealDto setmealDto);

    void deleteWithDish(String ids);


    SetmealDto getWithDish(Long id);
}
