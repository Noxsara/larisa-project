#!/usr/bin/python
# -*- coding: utf-8 -*-
# encoding: utf-8
# 客户端调用，用于查看API返回结果

from OkcoinSpotAPI import OKCoinSpot
from OkcoinFutureAPI import OKCoinFuture
import time
import json

# 初始化apikey，secretkey,url
apikey = 'xxxxxxxxxxxxxxxx'
secretkey = 'xxxxxxxxxxxxxxxx'
okcoinRESTURL = 'www.okex.com'  # 请求注意：国内账号需要 修改为 www.okcoin.cn

# 现货API
okcoinSpot = OKCoinSpot(okcoinRESTURL, apikey, secretkey)

# 期货API
okcoinFuture = OKCoinFuture(okcoinRESTURL, apikey, secretkey)

# print (okcoinSpot.ticker('eth_usdt'))
# print('===========================================')
# print(okcoinSpot.depth('eth_usdt')['asks'][-1])
# print('===========================================')
# print(okcoinSpot.depth('eth_usdt')['bids'][0])

# print (u' 获取k线数据 ')
# print (okcoinSpot.kline('eth_usdt','1min','2000'))

# print (u' 现货历史交易信息 ')
# print (okcoinSpot.trades())

# print (u' 用户现货账户信息 ')
# print (okcoinSpot.userinfo())

# print (u' 现货下单 ')
# print (okcoinSpot.trade('ltc_usd','buy','0.1','0.2'))

# print (u' 现货批量下单 ')
# print (okcoinSpot.batchTrade('ltc_usd','buy','[{price:0.1,amount:0.2},{price:0.1,amount:0.2}]'))

# print (u' 现货取消订单 ')
# print (okcoinSpot.cancelOrder('ltc_usd','18243073'))

# print (u' 现货订单信息查询 ')
# print (okcoinSpot.orderinfo('ltc_usd','18243644'))

# print (u' 现货批量订单信息查询 ')
# print (okcoinSpot.ordersinfo('ltc_usd','18243800,18243801,18243644','0'))

# print (u' 现货历史订单信息查询 ')
# print (okcoinSpot.orderHistory('ltc_usd','0','1','2'))

# print (u' 期货行情信息')
# print (okcoinFuture.future_ticker('ltc_usd','this_week'))

# print (u' 期货市场深度信息')
# print (okcoinFuture.future_depth('btc_usd','this_week','6'))

# print (u'期货交易记录信息')
# print (okcoinFuture.future_trades('ltc_usd','this_week'))

# print (u'期货指数信息')
# print (okcoinFuture.future_index('ltc_usd'))

# print (u'美元人民币汇率')
# print (okcoinFuture.exchange_rate())

# print (u'获取预估交割价')
# print (okcoinFuture.future_estimated_price('ltc_usd'))

# print (u'获取全仓账户信息')
# print (okcoinFuture.future_userinfo())

# print (u'获取全仓持仓信息')
# print (okcoinFuture.future_position('ltc_usd','this_week'))

# print (u'期货下单')
# print (okcoinFuture.future_trade('ltc_usd','this_week','0.1','1','1','0','20'))

# print (u'期货批量下单')
# print (okcoinFuture.future_batchTrade('ltc_usd','this_week','[{price:0.1,amount:1,type:1,match_price:0},{price:0.1,amount:3,type:1,match_price:0}]','20'))

# print (u'期货取消订单')
# print (okcoinFuture.future_cancel('ltc_usd','this_week','47231499'))

# print (u'期货获取订单信息')
# print (okcoinFuture.future_orderinfo('ltc_usd','this_week','47231812','0','1','2'))

# print (u'期货逐仓账户信息')
# print (okcoinFuture.future_userinfo_4fix())

# print (u'期货逐仓持仓信息')
# print (okcoinFuture.future_position_4fix('ltc_usd','this_week',1))

if __name__ == '__main__':

    # 设定价格波动区间
    waveArea = [7400, 9200]
    # 将区间划分为几份
    waveDensity = 50
    # 波动基线价格
    lastPrice = 8060.6995

    # 生成网线列表
    waveRuler = [waveArea[0]]
    for i in range(waveDensity):
        x = waveRuler[i] + (waveArea[-1] - waveArea[0]) / waveDensity
        waveRuler.append(x)

    # 设定持仓区间
    positionRate = [0.3, 0.8]

    handFee = 0.002

    # 设定延时
    initialSleepTime = 60
    sleepTime = initialSleepTime
    checkTime = 30

    # 计算当前价格落在哪个网格区间，并返回对应价格
    def calPrice(price,state):
        resultPrice = 1
        if waveRuler[0] < price < waveRuler[-1]:
            if state == 'rise':
                # 涨，取下限为网线值
                for i in range(len(waveRuler)):
                    if waveRuler[i] > price:
                        resultPrice = waveRuler[i - 1]
                        calNum = len(waveRuler) - i + 1
                        break
            elif state == 'fall':
                # 跌，取上限为网线值
                for i in range(len(waveRuler)):
                    if waveRuler[i] > price:
                        resultPrice = waveRuler[i]
                        calNum = i
                        break
        elif price >= waveRuler[-1]:
            resultPrice = -1
        elif price <= waveRuler[0]:
            resultPrice = -2

        return resultPrice, calNum

    # 设置上次交易网线
    priceLine = calPrice(lastPrice, 'rise')[0]

    # 设定交易对
    tradePair = 'btc_usdt'

    # 获取账户中的币、钱数量
    initialMoney = float(json.loads(okcoinSpot.userinfo())['info']['funds']['free'][tradePair[tradePair.find('_') + 1:]])

    initialCoin = float(json.loads(okcoinSpot.userinfo())['info']['funds']['free'][tradePair[:tradePair.find('_')]])

    print(str(initialMoney)[:5], str(initialCoin)[:5], lastPrice)

    # 开始循环判定
    for i in range(16000):
        time.sleep(sleepTime)
        # 获取账户中的币、钱数量
        initialMoney = float(json.loads(okcoinSpot.userinfo())['info']['funds']['free'][tradePair[tradePair.find('_') + 1:]])

        initialCoin = float(json.loads(okcoinSpot.userinfo())['info']['funds']['free'][tradePair[:tradePair.find('_')]])

        # 获取最新买价和卖价
        buyPrice = float(okcoinSpot.depth(tradePair)['asks'][-1][0]) * 1.0001
        sellPrice = float(okcoinSpot.depth(tradePair)['bids'][0][0]) * 0.9999

        # 计算当前仓位
        positionNow = (initialCoin * sellPrice) / (initialMoney + (initialCoin * sellPrice))
        if positionRate[0] <= positionNow <= positionRate[1]:
            if sellPrice > lastPrice:
                if calPrice(sellPrice, 'rise')[0] > priceLine:
                    # 涨穿一格，卖出
                    calNum = calPrice(sellPrice, 'rise')[1]

                    y = okcoinSpot.trade(tradePair, 'sell', sellPrice, initialCoin / calNum)

                    # 记录订单id
                    orderId = json.loads(y)['order_id']

                    # 预防滑点，检测行情
                    time.sleep(checkTime)
                    sleepTime = sleepTime - checkTime
                    orderState = json.loads(okcoinSpot.orderinfo(tradePair, orderId))['orders'][0]['status']
                    if orderState == 2:
                        # 重置交易价格
                        priceLine = calPrice(sellPrice, 'rise')[0]
                        lastPrice = sellPrice

                        print('sell:order success')
                    else:
                        okcoinSpot.cancelOrder(tradePair, orderId)
                        print('sell:cancel order')
                else:
                    sleepTime = initialSleepTime
                    print('no wave')
            elif buyPrice < lastPrice:
                if calPrice(buyPrice, 'fall')[0] < priceLine:
                    # 跌穿一格，买进
                    calNum = calPrice(buyPrice, 'fall')[1]
                    x = okcoinSpot.trade(tradePair, 'buy', buyPrice, (initialMoney / calNum) / buyPrice)

                    # 记录订单id
                    orderId = json.loads(x)['order_id']

                    # 预防滑点，检测行情
                    time.sleep(checkTime)
                    sleepTime = sleepTime - checkTime
                    orderState = json.loads(okcoinSpot.orderinfo(tradePair, orderId))['orders'][0]['status']
                    if orderState == 2:
                        # 重置交易价格
                        lastPrice = buyPrice
                        priceLine = calPrice(buyPrice, 'fall')[0]

                        print('buy:order success')
                    else:
                        okcoinSpot.cancelOrder(tradePair, orderId)
                        print('buy:cancel order')
                else:
                    sleepTime = initialSleepTime
                    print('no wave')
            else:
                sleepTime = initialSleepTime
                print('fuck error')
        elif positionNow > positionRate[1]:
            if sellPrice > lastPrice:
                if calPrice(sellPrice, 'rise')[0] > priceLine:
                    # 涨穿一格，卖出
                    calNum = calPrice(sellPrice, 'rise')[1]

                    y = okcoinSpot.trade(tradePair, 'sell', sellPrice, initialCoin / calNum)

                    # 记录订单id
                    orderId = json.loads(y)['order_id']

                    # 预防滑点，检测行情
                    time.sleep(checkTime)
                    sleepTime = sleepTime - checkTime
                    orderState = json.loads(okcoinSpot.orderinfo(tradePair, orderId))['orders'][0]['status']
                    if orderState == 2:
                        # 重置交易价格
                        priceLine = calPrice(sellPrice, 'rise')[0]
                        lastPrice = sellPrice

                        print('sell:order success')
                    else:
                        okcoinSpot.cancelOrder(tradePair, orderId)
                        print('sell:cancel order')
                else:
                    sleepTime = initialSleepTime
                    print('no wave')
            else:
                print('position is too high')
                sleepTime = initialSleepTime
        elif positionNow < positionRate[0]:
            if buyPrice <= lastPrice:
                if calPrice(buyPrice, 'fall')[0] < priceLine:
                    # 跌穿一格，买进
                    calNum = calPrice(buyPrice, 'fall')[1]
                    x = okcoinSpot.trade(tradePair, 'buy', buyPrice, (initialMoney / calNum) / buyPrice)

                    # 记录订单id
                    orderId = json.loads(x)['order_id']

                    # 预防滑点，检测行情
                    time.sleep(checkTime)
                    sleepTime = sleepTime - checkTime
                    orderState = json.loads(okcoinSpot.orderinfo(tradePair, orderId))['orders'][0]['status']
                    if orderState == 2:
                        # 重置交易价格
                        lastPrice = buyPrice
                        priceLine = calPrice(buyPrice, 'fall')[0]

                        print('buy:order success')
                    else:
                        okcoinSpot.cancelOrder(tradePair, orderId)
                        print('buy:cancel order')
                else:
                    sleepTime = initialSleepTime
                    print('no wave')
            else:
                print('position is too low')
                sleepTime = initialSleepTime

    print('Now Money：', str(initialMoney)[:6], '\n', 'Now Coin：', str(initialCoin)[:6])
    print('Finance：', int(initialMoney + initialCoin * float(okcoinSpot.depth(tradePair)['bids'][0][0])))

#### 持仓成本计算问题
#### 强制调仓问题
#### 手续费问题
#### 每天手动调参数时，仓位高低会否导致暗亏