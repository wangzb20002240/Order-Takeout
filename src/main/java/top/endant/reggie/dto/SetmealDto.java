package top.endant.reggie.dto;


import lombok.Data;
import top.endant.reggie.entity.Setmeal;
import top.endant.reggie.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
