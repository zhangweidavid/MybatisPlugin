package com.kaola.mybatis.plugin;

import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.executor.BaseExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
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
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

@Intercepts( {@Signature(type=Executor.class,method = "query",args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class CloseFirstLevelCachePlugin implements Interceptor {

    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();

    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();

    private static final ReflectorFactory DEFAULT_REFLACTOR_FACTORY = new DefaultReflectorFactory();


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Executor executor=(Executor) invocation.getTarget();
        MetaObject executorMetaObject=MetaObject.forObject(executor,DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLACTOR_FACTORY);
        Object[] objects= invocation.getArgs();

        while (executorMetaObject.hasGetter("h") || executorMetaObject.hasGetter("target")) {
            if (executorMetaObject.hasGetter("h")) {
                Object object = executorMetaObject.getValue("h");
                executorMetaObject = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLACTOR_FACTORY);
            }
            if(executorMetaObject.hasGetter("target")){
                Object object = executorMetaObject.getValue("target");
                executorMetaObject = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY,
                        DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLACTOR_FACTORY);
            }
        }
        MappedStatement ms=(MappedStatement)objects[0];
        Object parameter=objects[1];
        BoundSql boundSql = ms.getBoundSql(parameter);
        return  ((BaseExecutor)executorMetaObject.getOriginalObject()).query(ms,parameter,(RowBounds) objects[2],(ResultHandler)objects[3],null,boundSql);
    }

    @Override
    public Object plugin(Object target) {
        try {
            Executor executor=(Executor)target;
            MetaObject executorMetaObject=MetaObject.forObject(executor,DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLACTOR_FACTORY);
            while (executorMetaObject.hasGetter("h") || executorMetaObject.hasGetter("target")) {
                if (executorMetaObject.hasGetter("h")) {
                    Object object = executorMetaObject.getValue("h");
                    executorMetaObject = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLACTOR_FACTORY);
                }
                if(executorMetaObject.hasGetter("target")){
                    Object object = executorMetaObject.getValue("target");
                    executorMetaObject = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY,
                            DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLACTOR_FACTORY);
                }
            }
            executorMetaObject.setValue("localCache",new MockCache());
        }catch (Throwable e){
            //do nothing
        }
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }


    public static class MockCache extends PerpetualCache {

        public MockCache() {
            super("localCache");
        }


        @Override
        public void putObject(Object key, Object value) {
            return ;
        }

        @Override
        public Object getObject(Object key) {
            return null;
        }


    }

}
