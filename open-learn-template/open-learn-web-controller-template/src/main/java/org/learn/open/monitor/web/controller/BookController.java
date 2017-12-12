
package org.learn.open.monitor.web.controller;

import org.learn.open.service.BookService;
import org.learn.open.monitor.web.executor.OneLevelAsyncContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/demo")
public class BookController {

    @Autowired
    protected OneLevelAsyncContext oneLevelAsyncContext;
    private BookService bookService;

    public void getBook(HttpServletRequest request, final Long skuId, final Integer cat1, final Integer cat2) throws Exception {
        oneLevelAsyncContext.submitFuture(request, () -> bookService.getBook(skuId, cat1, cat2));
    }
}