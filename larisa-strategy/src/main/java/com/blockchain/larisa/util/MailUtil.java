package com.blockchain.larisa.util;

import com.blockchain.larisa.common.Constants;
import com.blockchain.larisa.domain.MailInfo;
import com.blockchain.larisa.common.DirectionEnum;
import com.blockchain.larisa.common.SymbolEnum;

import java.math.BigDecimal;

public class MailUtil {

    public static MailInfo buildOpenMail(SymbolEnum symbol, BigDecimal openPrice, Integer positionVol, Integer leverRate, DirectionEnum direction) {
        String title = "【" + symbol.getName() + "】" + (direction.getName().equals("buy") ? Constants.OPEN_BUY_TITLE : Constants.OPEN_SELL_TITLE);
        String content = "开仓价格:" + openPrice + "| 开仓数量:" + positionVol + "| 杠杆比例:" + leverRate;
        title = title + "| " + content;

        MailInfo mailInfo = new MailInfo();
        mailInfo.setTitle(title);
        mailInfo.setContent(content);
        return mailInfo;
    }

    public static MailInfo buildCloseMail(SymbolEnum symbol, BigDecimal openPrice, BigDecimal leavePrice, Integer positionVol, Integer leverRate, DirectionEnum direction) {
        String title = "【" + symbol.getName() + "】" + (direction.getName().equals("sell") ? Constants.CLOSE_SELL_TILE : Constants.CLOSE_BUY_TITLE);
        String content = "平仓价格:" + leavePrice + "| 开仓价格:" + openPrice + "| 平仓数量:" + positionVol + "| 杠杆比例:" + leverRate;
        title = title + "| " + content;

        MailInfo mailInfo = new MailInfo();
        mailInfo.setTitle(title);
        mailInfo.setContent(content);
        return mailInfo;
    }
}
