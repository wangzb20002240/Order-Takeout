package top.endant.orderTakeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.endant.orderTakeout.entity.Employee;
import top.endant.orderTakeout.mapper.EmployeeMapper;
import top.endant.orderTakeout.service.EmployeeService;

@Service
public class EmployServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
