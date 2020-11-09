package com.sss.sssforum.utils;


import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 抽奖工具类
 */
public class ProbabilityControlUtil {
    private final static Logger LOG = LoggerFactory.getLogger(ProbabilityControlUtil.class);

    /**
     * 获取抽奖金额数值
     */
    public static Integer getRandomLottery(String probabilityControlStr) {
        int result = 0;
        try {
            int first = (new Random()).nextInt(100);
            int next = (new Random()).nextInt(100);
            Vo object = JSON.parseObject(probabilityControlStr, Vo.class);
            List<Integer> probabilityList = object.getProbability();
            int num = 0;
            int total = 0;
            for (; num < probabilityList.size() - 1; num++) {
                total += probabilityList.get(num);
                if (first < total) {
                    break;
                }
            }
            Amount amount = object.getAmountList().get(num);
            result = amount.min + (amount.max - amount.min) * next / 100;
        } catch (Exception e) {
            LOG.error("getRandomLottery fail :", e);
        }
        return result;
    }

    /*
    例子
     {
    "probability": [
        99,
        1
    ],
    "amountList": [
        {
            "min": 0,
            "max": 0
        },
        {
            "min": 1,
            "max": 2
        }
    ]
}
     */

    @Data
    static class Vo {
        private List<Integer> probability;
        private List<Amount> amountList;
    }

    @Data
    static class Amount {
        private int min;
        private int max;
    }

    public static void main(String[] args) {
      //  String str= "{"probability":[100,0],"amountList":[{"max":100,"min":100},{"max":0,"min":0}]}"
        Integer [] dengji={20,30,50};
        Amount amount = new Amount();
        amount.setMax(1000);
        amount.setMin(400);
        Amount amount2 = new Amount();
        amount2.setMax(300);
        amount2.setMin(200);
        Amount amount3 = new Amount();
        amount3.setMax(100);
        amount3.setMin(100);
        ArrayList<Object> list = new ArrayList<>();
        list.add(amount);
        list.add(amount2);
        list.add(amount3);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("probability",dengji);
        hashMap.put("amountList",list);
        String s = JSON.toJSONString(hashMap);
        Integer one=0,two=0,three=0;
        for (int i = 0; i < 100; i++) {
            Integer randomLottery = getRandomLottery(s);
            if (randomLottery >=400) {
                one++;
            }else if (randomLottery >=200){
                two++;
            }else {
                three++;
            }
            System.out.println("中奖金额："+randomLottery);
        }
        String s2 = JSON.toJSONString(hashMap);
        System.out.println("概率控制数据字符串："+s2);
        System.out.println("抽奖100次;"+"一等奖中了"+one+"次;"+"二等奖中了"+two+"次;"+"三等奖中了"+three+"次");

    }
}
