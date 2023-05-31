package top.endant.orderTakeout.dto;


import lombok.Data;
import top.endant.orderTakeout.entity.Setmeal;
import top.endant.orderTakeout.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
