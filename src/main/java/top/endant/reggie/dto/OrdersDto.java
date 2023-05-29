package top.endant.reggie.dto;

import lombok.Data;
import top.endant.reggie.entity.OrderDetail;
import top.endant.reggie.entity.Orders;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
