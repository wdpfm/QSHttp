package org.song.http.framework;

import org.song.http.framework.IHttpProgress;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


/**
 * Created by song on 2017/1/19.
 */

public class ReadHelp {

    InputStream is;
    IHttpProgress hp;
    int readCount, allCount = -1;

    public ReadHelp(InputStream is, IHttpProgress hp, int allCount) {
        this.is = is;
        this.hp = hp;
        this.allCount = allCount;
    }

    public ReadHelp(InputStream is) {
        this.is = is;
    }

    //读取成字节数组
    public byte[] readBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[4 * 1024];
        int len;
        while ((len = read(buf)) > 0) {
            baos.write(buf, 0, len);
        }
        baos.flush();
        return baos.toByteArray();
    }

    //读取成字符串
    public String readString(Charset charset) throws IOException {
        return new String(readBytes(), charset);
    }

    //读取后写入文件
    public void readToFile(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        byte[] buf = new byte[4 * 1024];
        int len;
        while ((len = read(buf)) > 0) {
            bos.write(buf, 0, len);
        }
        bos.close();
        fos.close();
    }

    private int read(byte[] bytes) throws IOException {
        int len = is.read(bytes);
        if (len > 0)
            readCount += len;
        if (hp != null)
            hp.onProgress(readCount, len < 0 ? readCount : allCount, "");
        return len;
    }
//
//    //线性链表
//    private class Segment {
//        byte[] data;
//        Segment next;
//        Segment prev;
//        int len;
//
//        Segment(int size) {
//            this.data = new byte[size];
//        }
//
//        //链表弹出一个
//        public Segment pop() {
//            Segment result = next != this ? next : null;
//            if (prev != null)
//                prev.next = next;
//            if (next != null)
//                next.prev = prev;
//            next = null;
//            prev = null;
//            return result;
//        }
//
//        //链表插入一个
//        public Segment push(Segment segment) {
//            segment.prev = this;
//            segment.next = next;
//            if (next != null)
//                next.prev = segment;
//            next = segment;
//            return segment;
//        }
//
//    }
}