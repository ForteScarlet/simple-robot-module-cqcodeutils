package com.simplerobot.modules.utils;

import com.forte.qqrobot.anno.depend.Beans;

/**
 * 加载工具类与解码编码工具
 * @author ForteScarlet
 */
@Beans
public class KqCodeConfiguration {

    @Beans("defaultKqCodeUtils")
    public KQCodeUtils getKqCodeUtils(){
        return KQCodeUtils.INSTANCE;
    }

    @Beans("defaultCqDecoder")
    public CQDecoder getCqDecoder(){
        return CQDecoder.INSTANCE;
    }

    @Beans("defaultCqEncoder")
    public CQEncoder getCqEncoder(){
        return CQEncoder.INSTANCE;
    }

    @Beans("defaultMqCodeUtils")
    public MQCodeUtils getMqCodeUtils() {
        return MQCodeUtils.INSTANCE;
    }

    @Beans("defaultCQCodeStringTemplate")
    public CodeTemplate<String> getCqCodeStringTemplate(){
        return KQCodeStringTemplate.INSTANCE;
    }


}
