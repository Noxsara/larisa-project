#!/usr/bin/python
# -*- coding: utf-8 -*-
# encoding: utf-8
# 客户端调用，用于查看API返回结果

from OkcoinSpotAPI import OKCoinSpot
from OkcoinFutureAPI import OKCoinFuture
import ssl, json, time
import sys
import os

# 初始化apikey，secretkey,url
apikey = 'fdcc0738-1090-4ac6-ae7a-0eb6c6a7d5ef'
secretkey = 'B7D1515088B94513FAF2C7B033F83893'
okcoinRESTURL = 'www.okex.com'  # 请求注意：国内账号需要 修改为 www.okcoin.cn
ssl._create_default_https_context = ssl._create_unverified_context  # 全局禁用ssl验证

# 现货API
okcoinSpot = OKCoinSpot(okcoinRESTURL, apikey, secretkey)

# 期货API
okcoinFuture = OKCoinFuture(okcoinRESTURL, apikey, secretkey)


# 计算是否止损退出
def isLossStop(direction, earnRate, priceNow):
    global stopPrice, operatePrice, stateLossStop, priceState, forbidPrice
    if earnRate <= stateLossStop:
        if direction == 'rise':
            priceState = 'bullForbid'
        elif direction == 'fall':
            priceState = 'bearForbid'
        forbidPrice = operatePrice
        print('lossStop 1')
        return True
    elif direction == 'rise' and priceNow <= stopPrice:
        priceState = 'bullForbid'
        forbidPrice = operatePrice
        print('lossStop 2')
        return True
    elif direction == 'fall' and priceNow >= stopPrice:
        priceState = 'bearForbid'
        forbidPrice = operatePrice
        print('lossStop 3')
        return True
    else:
        return False


# 计算盈亏函数，返回当前收益币数、收益率
def calIncome(restCoin, initialcoin):
    earnCoin = restCoin - initialCoin
    earnRate = (restCoin - initialCoin) / initialCoin

    return earnCoin, earnRate


# 计算是否加仓（ATR加仓）
def isAddPosition(direction, addCount, operatePrice, priceNow, atrcal):
    global atrNow, useCoin, restCoin, addPositionStop, addRate
    if addCount >= addMaxTime:
        return 0
    elif (useCoin * (1 + addRate[addCount])) / restCoin >= addPositionStop:
        return 0
    elif direction == 'rise' or direction == 'breakBull':
        addTime = int((priceNow - operatePrice) / (atrcal * breakAtrRate[addCount])) - addCount
        return addTime
    elif direction == 'fall' or direction == 'breakBear':
        addTime = int((operatePrice - priceNow) / (atrcal * breakAtrRate[addCount])) - addCount
        return addTime


# 计算均线是否反向交叉
def isAntiCross(direction, priceNow, shortEma, midEma, longMa):
    global shortTrend, longTrend, ratChetState, ratChetLeavePrice
    if direction == 'rise':
        if shortEma <= midEma and priceNow <= longMa and shortTrend == 'shortBear':
            print('leave 1')
            return True
        elif ratChetState == 'on' and priceNow <= ratChetLeavePrice:
            print('leave 2')
            return True
    elif direction == 'fall':
        if shortEma >= midEma and priceNow >= longMa and shortTrend == 'shortBull':
            print('leave 3')
            return True
        elif ratChetState == 'on' and priceNow >= ratChetLeavePrice:
            print('leave 4')
            return True


# 止损或离市后，参数初始化
def paramInitial():
    global useCoin, useLeverCoin, earnCoin, earnRate, stopPrice, useAmount, addCount, addTime, addCoin, addCoin, addAmount, operatePrice, operateState, ratChetState, ratChetStartPrice, ratChetLeavePrice
    useCoin, useLeverCoin, earnCoin, earnRate, stopPrice, useAmount, addCount, addTime, addCoin, addCoin, addAmount, ratChetStartPrice, ratChetLeavePrice = 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    ratChetState = 'off'
    operateState = 'empty'


# 断线重连
def restart_program():
    global sleepTimeLong, tradePair, tradeCycle, delayCounter
    try:
        priceAAA = float(okcoinFuture.future_ticker(tradePair, tradeCycle)['ticker']['buy'])
        print('1')
    except:
        delayCounter = delayCounter + 1
        time.sleep(sleepTimeLong)
        restart_program()


# main函数
if __name__ == '__main__':

    # 各种参数
    coinType = 'btc'  # 操作币种：btc
    if coinType == 'btc':
        contractValue = 100  # btc单张合约价值：100
    else:
        contractValue = 10  # 其他币单张合约价值：10
    tradePair = coinType + '_usd'  # 设置交易对：btc_usd
    tradeCycle = 'quarter'  # 设置交易周期：季度合约
    initialSleepTime = 10  # 初始休眠时间：60秒
    sleepTimeLong = 10  # 长休眠时间：60秒
    loopRange = 300000  # for循环范围
    priceList = []  # 价格序列

    delayCounter = 0
    delayPriceList = []

    shortSleepCounter = 1  # 计时器，计算当前是第几根5秒k

    # 均线参数
    shortEmaPer = 10  # 短期EMA：10
    midEmaPer = 30  # 中期EMA：10
    longMaPer = 60  # 长期MA：30

    shortEma = []  # EMA列表
    midEma = []  # 中期EMA列表
    longMa = []  # 长期MA列表

    shortTrend = ''  # 短期趋势
    longTrend = ''  # 长期趋势

    atrCycle = 20  # ATR计算周期
    atrMoveParam = 3  # 计算移动ATR的参数

    atrCal = 0  # 记录开单时的ATR

    atrRatchetParam = 0.1  # 计算ATR棘轮止损的单位持仓时间增加ATR参数
    ratChetLeavePrice = 0  # ATR棘轮止损价
    ratChetStartPrice = 0  # ATR棘轮起始价
    ratChetState = 'off'  # ATR棘轮启动状态：默认关闭

    kLineCycle = 60  # k线周期：30分
    trueKlineCycle = kLineCycle * (60 / initialSleepTime)  # 真实k线周期：180个10秒
    kLineName = '1min'  # k线周期名（传入获取k线数据函数的参数）
    klinePreSize = 2880  # k线数量（传入获取k线数据函数的参数）
    priceKLine = []  # k线价格列表
    kLineNow = []  # 当前k线

    priceState = ''  # 价格状态
    forbidPrice = 0  # 禁止入场价格

    # 止损参数
    eachLossMax = 0.01  # 用于计算底仓，使1倍反向ATR等于总资金1%的损失
    antiAtrRate = 2  # 反向波动3倍ATR则止损出场，基本等同于每单最多可承受总资金3%的损失
    stopPrice = 0  # 止损价格
    stateLossStop = -0.03  # 绝对比例止损线，亏损超过总资金3%则止损

    # 加仓参数
    addRate = [1, 0.5, 0.25, 0.125, 0.06125]  # 单次加仓比例
    addCount = 0  # 加仓次数
    addMaxTime = 3  # 最大加仓次数
    addTime = 0  # 可加仓次数，用于计算
    addCoin = 0  # 加仓币数
    addPositionStop = 0.3  # 加仓到50%仓位时停止
    breakAtrRate = [5, 3, 3, 3, 3, 1]  # 价格顺势突破2倍atr，则加仓

    slideProtect = 0.0015  # 防滑点修正参数

    # 资金参数
    # 获取本金数量
    initialCoin = 0
    try:
        initialCoin = float(json.loads(okcoinFuture.future_userinfo())['info'][coinType]['account_rights'])
    except:
        restart_program()
    restCoin = initialCoin  # 剩余币数，初始为1000
    leverRate = 20  # 杠杆倍率
    useCoin = 0  # 开仓币数
    leverFullCoin = initialCoin * leverRate  # 满仓可开资金 = 本金 * 杠杆
    useLeverCoin = useCoin * leverRate  # 持仓量 = 开仓币数 * 杠杆
    useAmount = 0  # 已开合约张数
    addCoin = 0  # 加仓币数
    addAmount = 0  # 加仓合约张数
    handFee = 0.001  # 手续费：0.05%

    operatePrice = 0  # 开仓价格
    operateState = 'empty'  # 初始仓位状态：空仓
    directionList = ['rise', 'fall']  # 开单方向列表：多，空
    operateDirection = 'rise'  # 初始开单方向：多
    orderId = ''  # 合约单id

    try:
        buyPrice = float(okcoinFuture.future_ticker(tradePair, tradeCycle)['ticker']['buy'])
        sellPrice = float(okcoinFuture.future_ticker(tradePair, tradeCycle)['ticker']['sell'])
    except:
        restart_program()

    # 获取之前的k线：交易对、k线周期、合约类型、k线数量
    kLinePre = okcoinFuture.future_kline(tradePair, kLineName, tradeCycle, str(klinePreSize))

    print('Get History KLine DONE.')

    # 用于计算EMA、MA、ATR的价格列表
    pricePre = []
    for i in range(len(kLinePre)):
        a = float(kLinePre[i][4])
        pricePre.append(a)

    # 记录此前的极端行情
    priceKLine = [pricePre[i] for i in range(len(pricePre)) if (i + 1) % kLineCycle == 0]

    # 按周期级别生成新价格表
    priceRecord = [pricePre[i - kLineCycle + 1: i + 1] for i in range(len(pricePre)) if (i + 1) % kLineCycle == 0]
    priceMax = []  # 当日最高价
    priceMin = []  # 当日最低价
    priceEnd = []  # 当日收盘价

    for i in range(len(priceRecord)):
        priceMax.append(max(priceRecord[i]))
        priceMin.append(min(priceRecord[i]))
        priceEnd.append(priceRecord[i][-1])

    # 计算短期、中期EMA
    for i in range(len(priceKLine)):
        # 计算短期EMA
        if i < shortEmaPer - 1:
            shortEma.append(priceKLine[i])
        elif i == shortEmaPer - 1:
            shortEma.append(sum(priceKLine[i - shortEmaPer + 1:i]) / shortEmaPer)
        else:
            a = shortEma[i - 1] * ((shortEmaPer - 1) / (shortEmaPer + 1)) + priceKLine[i] * (2 / (shortEmaPer + 1))
            shortEma.append(a)
    # print('Cal History EMA10 DONE.')

    for i in range(len(priceKLine)):
        # 计算中期EMA
        if i < midEmaPer - 1:
            midEma.append(priceKLine[i])
        elif i == midEmaPer - 1:
            midEma.append(sum(priceKLine[i - midEmaPer + 1:i]) / midEmaPer)
        else:
            a = midEma[i - 1] * ((midEmaPer - 1) / (midEmaPer + 1)) + priceKLine[i] * (2 / (midEmaPer + 1))
            midEma.append(a)
    # print('Cal History  EMA20 DONE.')

    # 计算长期MA
    for i in range(len(priceKLine)):
        if i < longMaPer - 1:
            # a = priceKLine[i]
            a = sum(priceKLine[0:i + 1]) / (i + 1)
            longMa.append(a)
        else:
            a = sum(priceKLine[i - longMaPer + 1: i + 1]) / longMaPer
            longMa.append(a)
    print('Cal History MA30 DONE.')

    # 计算ATR列表
    trList = []
    for i in range(len(priceRecord)):
        if i == 0:
            x = round(abs(priceMax[i] - priceMin[i]), 5)
            trList.append(x)
        else:
            x = round(max(abs(priceMax[i] - priceMin[i]), abs(priceMax[i] - priceEnd[i - 1]),
                          abs(priceEnd[i - 1] - priceMin[i])), 5)
            trList.append(x)

    atrList = []
    for i in range(len(trList)):
        if i < atrCycle - 1:
            atrList.append(trList[i])
        elif i == atrCycle - 1:
            a = sum(trList[0: atrCycle - 1]) / atrCycle
            atrList.append(a)
        elif i > atrCycle - 1:
            n = ((atrCycle - atrMoveParam) / atrCycle) * atrList[i - 1] + (atrMoveParam / atrCycle) * trList[i]
            atrList.append(n)

    print(time.strftime('%m-%d %H:%M:%S', time.localtime(time.time())))
    print('Cal History ATR DONE.')
    print('ATRNOW:', atrList[-1])

    if shortEma[-1] > shortEma[-2]:
        shortTrend = 'shortBull'
    else:
        shortTrend = 'shortBear'

    if longMa[-1] > longMa[-2]:
        longTrend = 'longBull'
    else:
        longTrend = 'longBear'

    # 主循环体
    for i in range(loopRange):
        # 休眠时间
        time.sleep(initialSleepTime)

        # 拉取价格数据
        try:
            buyPrice = float(okcoinFuture.future_ticker(tradePair, tradeCycle)['ticker']['buy'])
            sellPrice = float(okcoinFuture.future_ticker(tradePair, tradeCycle)['ticker']['sell'])

            # 拉取账户余额数据
            restCoin = float(json.loads(okcoinFuture.future_userinfo())['info'][coinType]['account_rights'])
        except:
            print('No!!!!!!!')
            restart_program()

        if delayCounter == 0:
            kLineNow.append(buyPrice)
        elif delayCounter >= 6:
            # 若断网时间>=1min，则重新拉取断掉的k线数据，并补入k线列表中
            try:
                delayPriceList = okcoinFuture.future_kline(tradePair, kLineName, tradeCycle,
                                                           str(int(delayCounter / 6) + 1))
            except:
                restart_program()
            # continue

            for x in range(len(delayPriceList) - 1):
                y = float(delayPriceList[x][4])
                kLineNow.extend([y, y, y, y, y, y])

            for n in range((delayCounter % 6) + 6):
                kLineNow.append(float(delayPriceList[-1][4]))

            delayCounter = 0

        if len(kLineNow) < trueKlineCycle:

            # 初始化最新EMA、MA、ATR
            priceMinNow = priceMin[-1]
            priceMaxNow = priceMax[-1]
            priceEndNow = priceEnd[-1]

            shortEmaNow = shortEma[-1]
            midEmaNow = midEma[-1]
            longMaNow = longMa[-1]

            trNow = trList[-1]
            atrNow = atrList[- 1]
        else:
            priceMinNow = min(kLineNow)
            priceMaxNow = max(kLineNow)
            priceEndNow = kLineNow[-1]

            priceKLine.append(buyPrice)

            trNow = round(
                max(abs(priceMaxNow - priceMinNow), abs(priceMaxNow - priceEnd[-1]), abs(priceEnd[-1] - priceMinNow)),
                5)
            atrNow = ((atrCycle - atrMoveParam) / atrCycle) * atrList[-1] + (atrMoveParam / atrCycle) * trNow

            shortEmaNow = shortEmaNow * ((shortEmaPer - 1) / (shortEmaPer + 1)) + buyPrice * (2 / (shortEmaPer + 1))
            midEmaNow = midEmaNow * ((midEmaPer - 1) / (midEmaPer + 1)) + buyPrice * (2 / (midEmaPer + 1))
            longMaNow = (sum(priceKLine[1 - longMaPer:]) + buyPrice) / longMaPer

            priceMin.append(priceMinNow)
            priceMax.append(priceMaxNow)
            priceEnd.append(priceEndNow)

            trList.append(trNow)
            atrList.append(atrNow)

            shortEma.append(shortEmaNow)
            midEma.append(midEmaNow)
            longMa.append(longMaNow)

            kLineNow = []

            if shortEma[-1] > shortEma[-2]:
                shortTrend = 'shortBull'
            else:
                shortTrend = 'shortBear'

            if longMa[-1] > longMa[-2]:
                longTrend = 'longBull'
            else:
                longTrend = 'longBear'

            print(time.strftime('%m-%d %H:%M:%S', time.localtime(time.time())))
            print(i, 'New ATR: ', atrNow, 'New PriceMin: ', priceMinNow, 'New PriceMax: ', priceMaxNow)

            if len(shortEma) >= 1200:
                trList = trList[-900:]
                atrList = atrList[-900:]
                print(time.strftime('%m-%d %H:%M:%S', time.localtime(time.time())))
                print(i, 'EMA、MA、ATR list length Reset Done.')

        # 计算a值，用于计算均线
        # a = int(i / kLineCycle) - 1

        if priceState == 'bullForbid' and buyPrice >= forbidPrice + atrNow:
            priceState = ''
        elif priceState == 'bearForbid' and buyPrice <= forbidPrice - atrNow:
            priceState = ''

        if i <= -1:
            # 当前仍在限制交易k线内，不交易
            print(time.strftime('%m-%d %H:%M:%S', time.localtime(time.time())))
            print('Forbidden Trade.Wait for next Kline.')
        else:
            # 判断持仓状态
            if operateState == 'empty':
                # 空仓，择时入市
                if shortTrend == 'shortBull' and longTrend == 'longBull' and priceState != 'bullForbid' and shortEmaNow >= midEmaNow:
                    # EMA10/MA30金叉，视为多方市场
                    if buyPrice >= midEmaNow:
                        # 价格在EMA20之上，入场做多
                        operateDirection = 'rise'

                        # 根据当前ATR，计算底仓开单币数
                        operatePrice = sellPrice * (1 + slideProtect)
                        useCoin = min((restCoin * operatePrice * eachLossMax) / (atrNow * leverRate),
                                      addPositionStop * restCoin)
                        useLeverCoin = useCoin * leverRate

                        useAmount = int((useLeverCoin * sellPrice) / contractValue)

                        if useAmount >= 1:
                            try:
                                x = okcoinFuture.future_trade(tradePair, tradeCycle, str(operatePrice), str(useAmount),
                                                              '1', '0', '20')
                            except:
                                restart_program()

                            print(time.strftime('%m-%d %H:%M:%S', time.localtime(time.time())))
                            # print(x)

                            # orderId = json.loads(x)['order_id']
                            print(i, 'Bull contract Done ')
                            print("==== | BullPrice：{} | UseCoin：{} | RestCoin：{} |".format(round(operatePrice, 2),
                                                                                            useCoin, restCoin))

                            operateState = 'bottom'
                            earnCoin = 0
                            initialCoin = restCoin

                            # 设置止损价格，记录当前ATR
                            stopPrice = buyPrice - antiAtrRate * atrNow  # 2倍ATR反向波动止损
                            atrCal = atrNow
                            priceState = ''
                            forbidPrice = 0
                        else:
                            operatePrice = 0
                            useCoin = 0
                            useLeverCoin = 0
                            useAmount = 0
                        continue

                elif shortTrend == 'shortBear' and longTrend == 'longBear' and priceState != 'bearForbid' and shortEmaNow <= midEmaNow:
                    # EMA10/MA30死叉，视为空方市场
                    if buyPrice <= midEmaNow:
                        # 价格在EMA20之下，入场做空
                        operateDirection = directionList[1]

                        # 根据当前ATR，计算底仓开单币数
                        operatePrice = buyPrice * (1 - slideProtect)
                        useCoin = min((restCoin * operatePrice * eachLossMax) / (atrNow * leverRate),
                                      addPositionStop * restCoin)
                        useLeverCoin = useCoin * leverRate

                        useAmount = int((useLeverCoin * buyPrice) / contractValue)

                        if useAmount >= 1:
                            try:
                                x = okcoinFuture.future_trade(tradePair, tradeCycle, str(operatePrice), str(useAmount),
                                                              '2', '0', '20')
                            except:
                                restart_program()

                            # orderId = json.loads(x)['order_id']

                            print(time.strftime('%m-%d %H:%M:%S', time.localtime(time.time())))
                            print(i, 'Bear contract Done:.')
                            print("==== | BearPrice：{} | UseCoin：{} | RestCoin：{} |".format(round(operatePrice, 2),
                                                                                            useCoin, restCoin))

                            operateState = 'bottom'
                            earnCoin = 0
                            initialCoin = restCoin

                            # 设置止损价格，记录当前ATR
                            stopPrice = buyPrice + antiAtrRate * atrNow  # 2倍ATR反向波动止损
                            atrCal = atrNow
                            priceState = ''
                            forbidPrice = 0
                        else:
                            operatePrice = 0
                            useCoin = 0
                            useLeverCoin = 0
                            useAmount = 0

                        continue
            elif operateState == 'bottom' or 'add':
                # 底仓，计算当前浮盈浮亏
                try:
                    restCoin = float(json.loads(okcoinFuture.future_userinfo())['info'][coinType]['account_rights'])
                except:
                    restart_program()
                # continue
                earnCoin = calIncome(restCoin, initialCoin)[0]
                earnRate = calIncome(restCoin, initialCoin)[1]

                if earnRate >= 0.03 and ratChetState == 'off':
                    ratChetState = 'on'
                    operateTime = i
                    print(i, 'ATR-RAT-CHET ON!!!!')

                if ratChetState == 'on':
                    if operateDirection == 'rise':
                        ratChetLeavePrice = stopPrice + int(
                            (i - operateTime) / trueKlineCycle) * atrRatchetParam * atrCal
                    # print(i, ratChetLeavePrice)
                    elif operateDirection == 'fall':
                        ratChetLeavePrice = stopPrice - int(
                            (i - operateTime) / trueKlineCycle) * atrRatchetParam * atrCal
                # print(i, ratChetLeavePrice)

                # 计算可加仓次数
                addTime = isAddPosition(operateDirection, addCount, operatePrice, buyPrice, atrCal)
                if isLossStop(operateDirection, earnRate, buyPrice):
                    if operateDirection == 'rise' or operateDirection == 'breakBull':
                        # 平多
                        try:
                            y = okcoinFuture.future_trade(tradePair, tradeCycle, str(buyPrice * (1 - slideProtect)),
                                                          str(useAmount), '3', '0', '20')
                            print("LeavePrice:{}".format(buyPrice * (1 - slideProtect)))
                        except:
                            restart_program()
                        # continue

                        priceState = 'bullForbid'
                        forbidPrice = buyPrice

                        print(time.strftime('%m-%d %H:%M:%S', time.localtime(time.time())))
                        print(i, 'Bull contract STOP LOSS.')
                        print("==== | LossCoin：{} | LossRate：{} | RestCoin：{} |".format(earnCoin, round(earnRate, 5),
                                                                                        restCoin))
                    elif operateDirection == 'fall' or operateDirection == 'breakBear':
                        # 平空
                        try:
                            z = okcoinFuture.future_trade(tradePair, tradeCycle, str(sellPrice * (1 + slideProtect)),
                                                          str(useAmount), '4', '0', '20')
                            print("LeavePrice:{}".format(sellPrice * (1 + slideProtect)))
                        except:
                            restart_program()
                        # continue

                        priceState = 'bearForbid'
                        forbidPrice = buyPrice
                        print(time.strftime('%m-%d %H:%M:%S', time.localtime(time.time())))
                        print(i, 'Bear contract STOP LOSS.')
                        print("==== | LossCoin：{} | LossRate：{} | RestCoin：{} |".format(earnCoin, round(earnRate, 5),
                                                                                        restCoin))

                    # 各项参数复位
                    paramInitial()
                    print(i, 'Params Initial.')

                    # 设置限制器
                    forbidKLine = i + 10  # 出场后，10分钟内禁止交易
                    continue
                if isAntiCross(operateDirection, buyPrice, shortEmaNow, midEmaNow, longMaNow):
                    if operateDirection == 'rise':
                        # 平多
                        try:
                            y = okcoinFuture.future_trade(tradePair, tradeCycle, str(buyPrice * (1 - slideProtect)),
                                                          str(useAmount), '3', '0', '20')
                        except:
                            restart_program()
                        # continue
                        print(time.strftime('%m-%d %H:%M:%S', time.localtime(time.time())))
                        print(i, 'Bull contract leave market.')
                        print("==== | EarnCoin：{} | EarnRate：{} | RestCoin：{} |".format(earnCoin, round(earnRate, 5),
                                                                                        restCoin))

                        if earnCoin <= 0:
                            priceState = 'bullForbid'
                            forbidPrice = buyPrice

                        # 各项参数复位
                        paramInitial()
                        print(i, 'Params Initial.')
                        forbidKLine = i + kLineCycle - 1
                        continue
                    elif operateDirection == 'fall':
                        # 平空
                        try:
                            z = okcoinFuture.future_trade(tradePair, tradeCycle, str(sellPrice * (1 + slideProtect)),
                                                          str(useAmount), '4', '0', '20')
                        except:
                            restart_program()
                        # continue
                        print(time.strftime('%m-%d %H:%M:%S', time.localtime(time.time())))
                        print(i, 'Bear contract leave market.')
                        print("==== | EarnCoin：{} | EarnRate：{} | RestCoin：{} |".format(earnCoin, round(earnRate, 5),
                                                                                        restCoin))

                        if earnCoin <= 0:
                            priceState = 'bearForbid'
                            forbidPrice = buyPrice

                        # 各项参数复位
                        paramInitial()
                        print(i, 'Params Initial.')
                        forbidKLine = i + kLineCycle - 1
                        continue
                if addTime >= 1:
                    if operateDirection == 'rise':
                        addPrice = sellPrice * (1 + slideProtect)
                        addCoin = useCoin * addTime * addRate[addCount]
                        addLeverCoin = addCoin * leverRate

                        addAmount = int((addLeverCoin * addPrice) / contractValue)
                        if addAmount >= 1:
                            useAmount = useAmount + addAmount

                            try:
                                x = okcoinFuture.future_trade(tradePair, tradeCycle, str(addPrice), str(addAmount), '1',
                                                              '0', '20')
                            except:
                                restart_program()
                            # continue

                            # 可加仓次数>=1，加仓
                            operatePrice = (operatePrice * useCoin + addPrice * addCoin) / (useCoin + addCoin)
                            useCoin = useCoin + addCoin

                            operateState = 'add'
                            earnCoin = 0
                            earnRate = 0
                            addCount = addCount + addTime
                            stopPrice = operatePrice
                            print(time.strftime('%m-%d %H:%M:%S', time.localtime(time.time())))
                            print(i, 'ADD Bull contract.')
                            print("==== | AddCoin：{} | AverPrice：{} | RestCoin：{} |".format(addCoin,
                                                                                            round(operatePrice, 2),
                                                                                            restCoin))
                            continue
                        else:
                            addPrice, addCoin, addLeverCoin, addAmount = 0, 0, 0, 0
                            continue
                    elif operateDirection == 'fall':
                        addPrice = buyPrice * (1 - slideProtect)
                        addCoin = useCoin * addTime * addRate[addCount]
                        addLeverCoin = addCoin * leverRate

                        addAmount = int((addLeverCoin * addPrice) / contractValue)
                        if addAmount >= 1:
                            useAmount = useAmount + addAmount

                            try:
                                x = okcoinFuture.future_trade(tradePair, tradeCycle, str(addPrice), str(addAmount), '2',
                                                              '0', '20')
                            except:
                                restart_program()
                            # continue

                            # 可加仓次数>=1，加仓
                            operatePrice = (operatePrice * useCoin + addPrice * addCoin) / (useCoin + addCoin)
                            useCoin = useCoin + addCoin

                            operateState = 'add'
                            earnCoin = 0
                            earnRate = 0
                            addCount = addCount + addTime
                            stopPrice = operatePrice
                            print(time.strftime('%m-%d %H:%M:%S', time.localtime(time.time())))
                            print(i, 'ADD Bear contract.')
                            print("==== | AddCoin：{} | AverPrice：{} | RestCoin：{} |".format(addCoin,
                                                                                            round(operatePrice, 2),
                                                                                            restCoin))
                            continue
                        else:
                            addPrice, addCoin, addLeverCoin, addAmount = 0, 0, 0, 0
                            continue