package top.endant.orderTakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import top.endant.orderTakeout.common.R;
import top.endant.orderTakeout.entity.Employee;
import top.endant.orderTakeout.service.EmployeeService;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String username = employee.getUsername();
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, username);
        Employee one = employeeService.getOne(queryWrapper);
        if (one == null) return R.error("用户名不存在");

        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
        if (!Objects.equals(one.getPassword(), password)) return R.error("密码错误");

        if (one.getStatus() == 0) return R.error("用户已禁用");

        //session
        request.getSession().setAttribute("employee", one.getId());
        log.info("---"+one.getName() + "登录系统---");
        return R.success(one);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        log.info("---用户ID：" + request.getSession().getAttribute("employee") + "登出系统---");
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(@RequestBody Employee employee) {
        //初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        boolean save = employeeService.save(employee);
        if (!save) return R.error("新增失败");
        else return R.success("新增成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        Page<Employee> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(p, queryWrapper);

        return R.success(p);
    }

    /**
     * 修改信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee) {
        employeeService.updateById(employee);
        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
