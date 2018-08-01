package com.nala.faceCatch.util.netty;

import com.nala.faceCatch.service.FaceSearch;
import com.nala.faceCatch.util.FileUtil;
import com.nala.faceCatch.util.NumberUtil;
import com.nala.faceCatch.util.OutUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by heshangqiu on 2017/3/30 16:08
 * update by lizenn
 */
public class NettyClientDecoder extends LengthFieldBasedFrameDecoder {
    /**
     * @param byteOrder
     * @param maxFrameLength      字节最大长度,大于此长度则抛出异常
     * @param lengthFieldOffset   开始计算长度位置,这里使用0代表放置到最开始
     * @param lengthFieldLength   描述长度所用字节数
     * @param lengthAdjustment    长度补偿,这里由于命令码使用2个字节.需要将原来长度计算加2
     * @param initialBytesToStrip 开始计算长度需要跳过的字节数
     * @param failFast
     */
    public NettyClientDecoder(ByteOrder byteOrder, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    public NettyClientDecoder() {
        this(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE, 0, 4, 0, 4, true);
    }

    private static int index = 0;
    /**
     * 根据构造方法自动处理粘包,半包.然后调用此decode
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {

        int length = byteBuf.readableBytes();//计算可读字节数
        byte[] array = new byte[length];    //分配一个具有length大小的数组

        if (byteBuf.hasArray()){//检查是否有支持数组
            array = byteBuf.array();     //得到支持数组
            int offset = byteBuf.arrayOffset() + byteBuf.readerIndex();//计算第一个字节的偏移量
            for(int i = 0;i<array.length;i++){
                System.out.println("flagOne---->"+i+array[i]);
            }
        }else if (!byteBuf.hasArray()) {//false表示为这是直接缓冲
            System.out.print("----------here1--------");
            System.out.println(" ");

            byteBuf.getBytes(byteBuf.readerIndex(), array); //将缓冲区中的数据拷贝到这个数组中
            byteBuf.discardReadBytes();
//            byteBuf.clear();

            //4 - 7 字节的二进制表示·协议中低位在前，故反向拼接
            String bitStr = NumberUtil.binaryString(array[7])
                    +NumberUtil.binaryString(array[6])
                    +NumberUtil.binaryString(array[5])
                    +NumberUtil.binaryString(array[4]);

            int realLength = Integer.valueOf(bitStr,2);
            System.out.println("realLength---->"+realLength);
//            OutUtil.Out();
            for(int i = 0;i<array.length;i++){
                System.out.println("array"+i+"--->"+array[i]);
            }
            System.out.println("array size:"+array.length);
            if(realLength+12 == array.length){
                obtainImageData(array);
            }
        }
        index = byteBuf.writerIndex();
        return null;
    }
    public void obtainImageData(byte[] array){

        //4 - 7 字节的二进制表示·协议中低位在前，故反向拼接
        String bitStr = NumberUtil.binaryString(array[7])
                +NumberUtil.binaryString(array[6])
                +NumberUtil.binaryString(array[5])
                +NumberUtil.binaryString(array[4]);

        int realLength = Integer.valueOf(bitStr,2);
        //人脸数据长度
        int faceLength = realLength - 64;
        byte[] faceArray = new byte[array.length - 76];
        System.arraycopy(array,76,faceArray,0,faceLength-1);
//        OutUtil.Out();
        for(int i = 0;i<faceArray.length-1;i++){
            System.out.println("faceArray"+i+"--->"+faceArray[i]);
        }
        System.out.println("faceArray size:"+faceLength);
        //人脸库·搜索 匹配
        FaceSearch.search(faceArray,"group_repeat,group_celebrity");
        Date date = new Date();
        FileUtil.byte2image(faceArray,"/Users/lizengqi/Pictures/face_dev/"
                +new SimpleDateFormat("yyyyMMddHHmmssSSS").format(date)+".jpeg");
        System.out.print("----------here2--------");
    }

}
