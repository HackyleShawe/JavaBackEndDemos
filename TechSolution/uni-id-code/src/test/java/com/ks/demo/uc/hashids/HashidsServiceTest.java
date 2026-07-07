package com.ks.demo.uc.hashids;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class HashidsServiceTest {

    @Autowired
    HashidsService hashidsService;

    @Test
    public void testEncode() {
        String encode = hashidsService.encode(1111);
        System.out.println(encode);
        System.out.println(hashidsService.decode(encode));

        encode = hashidsService.encode(12121212L);
        System.out.println(encode);
        System.out.println(hashidsService.decode(encode));
    }

    @Test
    public void testEncodeArr() {
        int[] aa = new int[]{1111,222,333};
        String encode = hashidsService.encodeArr(aa);
        System.out.println(encode);
        System.out.println(Arrays.toString(hashidsService.decodeArr(encode)));

        long[] bb = new long[]{111111L,2223234L,332341233L};
        encode = hashidsService.encodeArr(bb);
        System.out.println(encode);
        System.out.println(Arrays.toString(hashidsService.decodeArr(encode)));
    }

}
