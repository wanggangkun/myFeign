package com.kun.core;

import com.kun.client.annotation.HttpConsumer;
import com.kun.core.config.HttpConsumerProperties;
import com.kun.core.model.HttpDomain;
import com.kun.core.util.BinderUtils;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.internal.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author kun
 * @data 2022/1/16 13:59
 */
@EnableConfigurationProperties(HttpConsumerProperties.class)
public class HttpConsumerPostProcessor implements BeanClassLoaderAware, EnvironmentAware, BeanFactoryPostProcessor, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(HttpConsumerPostProcessor.class);

    private ClassLoader classLoader;

    private ApplicationContext context;

    private ConfigurableEnvironment environment;

    private ConfigurableListableBeanFactory beanFactory;

    private final Map<String, BeanDefinition> httpClientBeanDefinitions = new HashMap<>(4);

    private OkHttpClient okHttpClient;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.beanFactory = configurableListableBeanFactory;
        this.okHttpClient = buildOkHttpClient(environment);
        postProcessBeanFactory(beanFactory, (BeanDefinitionRegistry) beanFactory);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    private OkHttpClient buildOkHttpClient(ConfigurableEnvironment environment) {
        HttpConsumerProperties properties = BinderUtils.bind(environment, HttpConsumerProperties.PREFIX, HttpConsumerProperties.class);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(properties.getCoreThreads(), properties.getMaxThreads(),
                properties.getKeepAliveTime(), TimeUnit.SECONDS, new SynchronousQueue<>(), Util.threadFactory("OkHttp Custom Dispatcher", false));
        Dispatcher dispatcher = new Dispatcher(executor);
        dispatcher.setMaxRequests(properties.getMaxRequests());
        dispatcher.setMaxRequestsPerHost(properties.getMaxRequests());
        builder.dispatcher(dispatcher);
        builder.connectTimeout(properties.getConnectTimeOut(), TimeUnit.SECONDS);
        builder.readTimeout(properties.getReadTimeOut(), TimeUnit.SECONDS);
        builder.writeTimeout(properties.getWriteTimeOut(), TimeUnit.SECONDS);
        builder.connectionPool(new ConnectionPool(properties.getMaxIdleConnections(), properties.getConnectionKeepAliveTime(), TimeUnit.SECONDS));
        return builder.build();
    }

    private void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory, BeanDefinitionRegistry registry) {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
            String beanClassName = definition.getBeanClassName();
            // 当用@Bean 返回的类型是Object时，beanClassName是null
            if (Objects.isNull(beanClassName)) {
                continue;
            }
            Class<?> clazz = ClassUtils.resolveClassName(definition.getBeanClassName(), this.classLoader);
            ReflectionUtils.doWithFields(clazz, this::parseElement, this::annotatedWithHttpConsumer);
        }
        for (String beanName : httpClientBeanDefinitions.keySet()) {
            if (context.containsBean(beanName)) {
                throw new IllegalArgumentException("[HttpConsumer Starter] Spring context already has a bean named " + beanName
                 + ", please change @HttpConsumer field name.");
            }
            registry.registerBeanDefinition(beanName, httpClientBeanDefinitions.get(beanName));
            logger.info("registered HttpConsumer factory bean \"{}\" in spring context.", beanName);
        }
    }

    private void parseElement(Field field) {
        Class<?> interfaceClass = field.getType();
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("field [" + field.getName() + "] annotated with @HttpConsumer must be interface!");
        }
        HttpConsumer httpConsumer = AnnotationUtils.getAnnotation(field, HttpConsumer.class);
        HttpDomain httpDomain = HttpDomain.from(httpConsumer);
        // 支持占位符${}
        httpDomain.setDomain(beanFactory.resolveEmbeddedValue(httpDomain.getDomain()));
        httpDomain.setPort(beanFactory.resolveEmbeddedValue(httpDomain.getPort()));
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(HttpConsumerProxyFactoryBean.class)
                .addConstructorArgValue(interfaceClass)
                .addConstructorArgValue(httpDomain)
                .addConstructorArgValue(okHttpClient);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        beanDefinition.setPrimary(true);
        beanDefinition.setAutowireCandidate(true);
        httpClientBeanDefinitions.put(field.getName(), beanDefinition);
    }

    private boolean annotatedWithHttpConsumer(Field field) {
        return field.isAnnotationPresent(HttpConsumer.class);
    }
}
