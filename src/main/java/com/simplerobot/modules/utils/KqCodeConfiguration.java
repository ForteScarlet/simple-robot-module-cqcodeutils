/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  mod-cqcodeutils
 * File     KqCodeConfiguration.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 */

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
