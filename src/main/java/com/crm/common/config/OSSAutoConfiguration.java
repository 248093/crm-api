// 文件路径: src/main/java/generator/lyh/config/OSSAutoConfiguration.java
package com.crm.common.config;


import com.crm.service.impl.OssApiService;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

@Configuration
@AutoConfigureAfter(OssConfig.class)
@ConditionalOnClass(com.aliyun.oss.OSSClient.class)
@EnableConfigurationProperties(OssConfig.class)
public class OSSAutoConfiguration {

    /**
     * 将 jar 里的 aliyun-oss-default.properties 作为最低优先级源加进去，
     * 用户任何配置都会覆盖它。
     */
    @Bean
    public static PropertySource<?> defaultOSSPropertySource() throws IOException {
        Resource resource = new ClassPathResource("aliyun-oss.properties");
        Properties props  = PropertiesLoaderUtils.loadProperties(resource);
        return new PropertiesPropertySource("aliyun-oss-default", props);
    }

    @Bean
    @ConditionalOnMissingBean
    public OssApiService ossService(OssConfig props) {
        // 初始化静态工具类
        OssApiService.init(props);
        // 仍然创建 Bean 以保持向后兼容
        return new OssApiService();
    }
}
