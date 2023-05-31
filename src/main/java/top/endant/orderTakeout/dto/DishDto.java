package top.endant.orderTakeout.dto;

import lombok.Data;
import top.endant.orderTakeout.entity.Dish;
import top.endant.orderTakeout.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
