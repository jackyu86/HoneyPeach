package com.open.zk;

public interface ZkSerializer<T> {

    /**
     * from object to bytes
     * 
     * @param path
     *            znode absolute path
     * @param src
     *            maybe null
     * @return
     * @throws Exception
     */
    byte[] serialize(String path, T src) throws Exception;

    /**
     * from bytes to object
     * 
     * @param path
     *            znode absolute path
     * @param bytes
     *            maybe null
     * @return
     * @throws Exception
     */
    T deserialize(String path, byte[] bytes) throws Exception;
}
