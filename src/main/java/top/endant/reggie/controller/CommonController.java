package top.endant.reggie.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.endant.reggie.common.R;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.photo-location}")
    private String photoLocation;

    //有个问题，如果添加菜品时取消，图片会继续保留在后台不会删除。
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        assert filename != null;
        int pointLocation = filename.lastIndexOf(".");
        File f = new File(photoLocation
                + UUID.randomUUID()
                + filename.substring(pointLocation));
        file.transferTo(f);

        //确保目录存在
        File dir = new File(photoLocation);
        if (!dir.exists()) dir.mkdirs();
        return R.success(f.getName());
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        File file = new File(photoLocation + name);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");

            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
