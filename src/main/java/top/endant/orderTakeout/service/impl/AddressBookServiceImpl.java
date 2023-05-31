package top.endant.orderTakeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.endant.orderTakeout.entity.AddressBook;
import top.endant.orderTakeout.mapper.AddressBookMapper;
import top.endant.orderTakeout.service.AddressBookService;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
