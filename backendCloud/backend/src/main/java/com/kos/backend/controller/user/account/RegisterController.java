package com.kos.backend.controller.user.account;

import com.kos.backend.consumer.utils.JwtAuthentication;
import com.kos.backend.mapper.UserMapper;
import com.kos.backend.pojo.User;
import com.kos.backend.service.user.account.RegisterService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
public class RegisterController {
    @Autowired
    private RegisterService registerService;
    @Autowired
    private UserMapper userMapper;

    @PostMapping("/user/account/register/")
    public Map<String, String> register(@RequestParam Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        String confirmedPassword = map.get("confirmedPassword");
        return registerService.register(username, password, confirmedPassword);
    }

    @PostMapping("/user/account/uploadAvatar/")
    public String uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam("token") String token) {
        try {
            System.out.println(file.getOriginalFilename());
            // Save the file to the server
            Path path = Paths.get("/Users/xyt/MainVault/KingOfSnake/backendCloud/backend/src/main/resources/static/image/" + file.getOriginalFilename());
            file.transferTo(path);

            // 更新用户 photo
            Integer userId = JwtAuthentication.getUserId(token);
            User user = userMapper.selectById(userId);
            String serverAddr = "http://192.168.31.157:3000"; // TODO
            user.setPhoto(serverAddr + "/user/account/avatar/" + file.getOriginalFilename());
            userMapper.updateById(user);

            // Return the URL of the file
            return "/user/account/avatar/" + file.getOriginalFilename();
        } catch (Exception e) {
            // Handle the exception
            return "Upload failed";
        }
    }

    @GetMapping("/user/account/avatar/{filename}")
    public ResponseEntity<Resource> serveAvatar(@PathVariable String filename) {
        try {
            Path path = Paths.get("/Users/xyt/MainVault/KingOfSnake/backendCloud/backend/src/main/resources/static/image/" + filename);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(resource);
            } else {
                // Handle the error
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Handle the exception
            return ResponseEntity.notFound().build();
        }
    }
}
