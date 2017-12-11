/*
 *=======================================================================================
 *
 *
 *
 *
 *
 *
 *
 *
 *======================================================================================
 */
package org.learn.open.service;


public interface BookService {
    public <T> T getBook(Long skuId, Integer cat1, Integer cat2);
}