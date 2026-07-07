package com.ks.demo.uc.mod;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ModConfusionServiceTest {
    @Autowired
    private ModConfusionService modConfusionService;

    @Test
    void testEncode() {
        Random random = new Random();

        for (int i = 0; i < 1000000; i++) {
            long num = random.nextLong(1, 9999999999L);
            String encode = modConfusionService.encode(num);
            long decoded = modConfusionService.decode(encode);
            if(num != decoded) {
                throw new RuntimeException("编码解码失败");
            }
        }
        System.out.println("测试结束");
    }
}
