package com.blockchain.larisa.controller;

import com.blockchain.larisa.domain.KLine;
import com.blockchain.larisa.service.KLineService;
import com.blockchain.larisa.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
@RequestMapping("/tool")
public class ToolController {

    @Autowired
    private MailService mailService;

    @Autowired
    private KLineService kLineService;

    @RequestMapping(value = "/mail", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String sendMail(@RequestParam(value = "address", required = false) String address,
                           @RequestParam("title") String title,
                           @RequestParam("content") String content) {
        mailService.send(address, title, content);
        return "OK";
    }

    @RequestMapping("/insert")
    @ResponseBody
    public String insertKline() {
        KLine kLine = new KLine();
        kLine.setId(System.currentTimeMillis() / 1000);
        kLine.setOpen(BigDecimal.ONE);
        kLine.setClose(BigDecimal.ONE);
        kLine.setHigh(BigDecimal.ONE);
        kLine.setLow(BigDecimal.ONE);
        kLine.setCount(1L);
        kLine.setVol(123.0);
        kLine.setAmount(120L);

        boolean result = kLineService.insertKline(kLine);
        return String.valueOf(result);
    }
}
