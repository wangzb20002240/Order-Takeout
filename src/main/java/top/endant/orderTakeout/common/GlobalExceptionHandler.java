package top.endant.orderTakeout.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exception(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        log.error(message);
        if (message.contains("Duplicate entry")) {
            String[] s = message.split(" ");
            return R.error("用户名" + s[2] + "已存在");
        }

        return R.error(ex.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exception(CustomException ex) {
        String message = ex.getMessage();
        log.error(message);
        return R.error(ex.getMessage());
    }
}
