package top.endant.orderTakeout.dto;

import lombok.Data;
import top.endant.orderTakeout.entity.OrderDetail;
import top.endant.orderTakeout.entity.Orders;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
