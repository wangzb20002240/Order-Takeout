package top.endant.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.endant.reggie.entity.Employee;
import top.endant.reggie.mapper.EmployeeMapper;
import top.endant.reggie.service.EmployeeService;

@Service
public class EmployServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
