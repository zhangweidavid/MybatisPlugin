package com.kaola.mybatis.plugin;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Intercepts({@Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class})})
public class AddtionalParameterPlugin implements Interceptor {

    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();

    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();

    private static final ReflectorFactory DEFAULT_REFLACTOR_FACTORY = new DefaultReflectorFactory();

    private static final Map<String, Object> addtionalParameterMap = new HashMap<>();

    static {
        addtionalParameterMap.put("additional_current", new Date());
    }


    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        ParameterHandler parameterHandler = (ParameterHandler) invocation.getTarget();
        MetaObject metaParameterHandler = MetaObject.forObject(parameterHandler, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLACTOR_FACTORY);
        //获取最终目标对象
        while (metaParameterHandler.hasGetter("h") || metaParameterHandler.hasGetter("target")) {
            if (metaParameterHandler.hasGetter("h")) {
                Object object = metaParameterHandler.getValue("h");
                metaParameterHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLACTOR_FACTORY);
            }
            if (metaParameterHandler.hasGetter("target")) {
                Object object = metaParameterHandler.getValue("target");
                metaParameterHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY,
                        DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLACTOR_FACTORY);
            }
        }
        //向扩展参数中添加附加信息
        BoundSql boundSql = (BoundSql) metaParameterHandler.getValue("boundSql");
        //添加扩展数据
        for (Map.Entry<String, Object> entry : addtionalParameterMap.entrySet()) {
            boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        if (properties != null && !properties.isEmpty()) {
            for (Map.Entry entry : properties.entrySet()) {
                addtionalParameterMap.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
    }


}

