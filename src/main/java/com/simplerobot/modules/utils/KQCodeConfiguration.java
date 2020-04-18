package com.simplerobot.modules.utils;

import com.forte.qqrobot.anno.depend.Beans;

/**
 * 加载工具类与解码编码工具
 */
@Beans
public class KQCodeConfiguration {

    @Beans("kqCodeUtils")
    public KQCodeUtils getKQCodeUtils(){
        return KQCodeUtils.INSTANCE;
    }

    @Beans("cqDecoder")
    public CQDecoder getCQDecoder(){
        return CQDecoder.INSTANCE;
    }

    @Beans("cqEncoder")
    public CQEncoder getCQEncoder(){
        return CQEncoder.INSTANCE;
    }


}
