
package org.learn.open.web.controller;

import org.learn.open.service.BookService;
import org.learn.open.web.executor.OneLevelAsyncContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

@Controller
public class BookController {

    @Autowired
    protected OneLevelAsyncContext oneLevelAsyncContext;
    private BookService bookService;

    public void getBook(HttpServletRequest request, final Long skuId, final Integer cat1, final Integer cat2) throws Exception {
        oneLevelAsyncContext.submitFuture(request, () -> bookService.getBook(skuId, cat1, cat2));
    }
}