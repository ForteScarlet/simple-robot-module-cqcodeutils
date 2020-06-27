package com.simplerobot.modules.utils;

import com.forte.qqrobot.anno.depend.Beans;

/**
 * 加载工具类与解码编码工具
 * @author ForteScarlet
 */
@Beans
public class KQCodeConfiguration {

    @Beans("defaultKqCodeUtils")
    public KQCodeUtils getKQCodeUtils(){
        return KQCodeUtils.INSTANCE;
    }

    @Beans("defaultCqDecoder")
    public CQDecoder getCQDecoder(){
        return CQDecoder.INSTANCE;
    }

    @Beans("defaultCqEncoder")
    public CQEncoder getCQEncoder(){
        return CQEncoder.INSTANCE;
    }

    @Beans("defaultMqCodeUtils")
    public MQCodeUtils getMQCodeUtils() {
        return MQCodeUtils.INSTANCE;
    }

    @Beans("defaultCQCodeStringTemplate")
    public CodeTemplate<String> getCQCodeStringTemplate(){
        return KQCodeStringTemplate.INSTANCE;
    }


}
