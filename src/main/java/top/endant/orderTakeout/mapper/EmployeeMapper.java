package top.endant.orderTakeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.endant.orderTakeout.entity.Employee;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
