package top.endant.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.endant.reggie.common.R;
import top.endant.reggie.entity.AddressBook;
import top.endant.reggie.service.AddressBookService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    public R<List<AddressBook>> list(HttpServletRequest request) {
        String userId = request.getSession().getAttribute("user").toString();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, userId);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook, HttpServletRequest request) {
        String userId = request.getSession().getAttribute("user").toString();
        addressBook.setUserId(Long.valueOf(userId));
        addressBookService.save(addressBook);
        return R.success("保存成功");
    }

    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id) {
        return R.success(addressBookService.getById(id));
    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return R.success("更新成功");
    }

    @DeleteMapping
    public R<String> delete(String ids) {
        addressBookService.removeById(ids);
        return R.success("删除成功");
    }

    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook) {
        log.info(String.valueOf(addressBook));
        //查找原本的默认地址并修改
        addressBook = addressBookService.getById(addressBook);
        Long userId = addressBook.getUserId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, userId).eq(AddressBook::getIsDefault, 1);
        AddressBook one = addressBookService.getOne(queryWrapper);
        if (one != null) {
            one.setIsDefault(0);
            addressBookService.updateById(one);
        }
        //更新现在的默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("更新成功");
    }

    @GetMapping("/default")
    public R<AddressBook> getDefault(HttpServletRequest request) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        queryWrapper.eq(AddressBook::getUserId,request.getSession().getAttribute("user").toString());
        return R.success(addressBookService.getOne(queryWrapper));
    }
}
