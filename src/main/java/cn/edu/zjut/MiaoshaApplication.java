package cn.edu.zjut;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// jar包部署（默认）
// @SpringBootApplication
// @MapperScan("cn.edu.zjut.dao")
// public class MiaoshaApplication {
// public static void main(String[] args) {
// SpringApplication.run(MiaoshaApplication.class, args);
// }
// }

// 指定war部署[step4]继承 SpringBootServletInitializer
@SpringBootApplication
@MapperScan("cn.edu.zjut.dao")
public class MiaoshaApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MiaoshaApplication.class, args);
    }

    // 指定war部署[step5]配置入口类是谁
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MiaoshaApplication.class);
    }
}
