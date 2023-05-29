package top.endant.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.endant.reggie.entity.AddressBook;
import top.endant.reggie.mapper.AddressBookMapper;
import top.endant.reggie.service.AddressBookService;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
