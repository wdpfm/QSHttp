package org.song.http.framework;

/**
 * Created by song on 2016/9/19.
 */
public interface Parser<T> {
    T parser(String result) throws Exception;
}
