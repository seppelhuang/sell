package cn.seppel.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: Huangsp
 * @create: 2019-03-17
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LogbackTest {

    @Test
    public void test1() {
        String name = "Seppel";
        String email = "seppelhuang@163.com";
        log.debug("DEDUG...");
        log.info("INFO...name:{},email;{}", name, email);
        log.error("ERROR..");
    }
}
