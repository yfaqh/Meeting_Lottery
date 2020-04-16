package practice.MeetingLottery;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class Candidators {

    Scanner input_num = null;
    Scanner input_seed = null;
    ArrayList<String> arrayList;        // 存储学生学号
    int num = 0;                        // 学生学号个数
    String[] sha256_num;                // 存储学号经sha256加密后的hash值
    String sha256_seed;                 // 随机数种子
    Map<Character, Integer> hashTemplateMap;    // String类型十六进制转换成十进制模板
    Map<Integer, Double> sha256_num_decimal;    // 存储十进制的形式的学号hash值
    double sha256_seed_decimal;                 // 随机数种子十进制形式的hash值

    public Candidators() {
        // System.out.println("This is the default constructor!");
    }

    // 将经sha256加密后的hash值中包含的十六进制字符对应到十进制
    public void hexTemplate() {

        hashTemplateMap = new HashMap<>();
        for (int i = 0; i < 6; i++) {
            hashTemplateMap.put((char) (97 + i), 10 + i);
        }
        hashTemplateMap.put('0', 0);
        hashTemplateMap.put('1', 1);
        hashTemplateMap.put('2', 2);
        hashTemplateMap.put('3', 3);
        hashTemplateMap.put('4', 4);
        hashTemplateMap.put('5', 5);
        hashTemplateMap.put('6', 6);
        hashTemplateMap.put('7', 7);
        hashTemplateMap.put('8', 8);
        hashTemplateMap.put('9', 9);
        // System.out.println(hashMap); //打印测试
    }

    // 获取各学号对应的sha256加密后的值，存于数组sha256_num中
    public void getSchoolNum() {

        try {
            // 读取班级学生的学号
            arrayList = new ArrayList<String>();
            input_num = new Scanner(
                    new File(System.getProperty("user.dir") + "/school_number.txt"));
            while (input_num.hasNext()) {
                arrayList.add(input_num.next());
            }
            num = arrayList.size();
            sha256_num = new String[num]; // 定义字符串数组，存放学号经sha256加密后的值
            for (int i = 0; i < num; i++) { // sha256加密
                sha256_num[i] = ComputeHashValue.getSHA256StrJava(arrayList.get(i));
            }

            // 读取学号到arrayList后，学号打印测试
            // System.out.println("班级学生的学号为：");
            // for (String string : arrayList) {
            // System.out.println(string);
            // }

            // 学号经sha256加密后，结果打印测试
            // System.out.println("各学号经sha256加密结果为：");
            // for (int i = 0; i < sha256_num.length; i++) {
            // System.out.println(arrayList.get(i) + " --> " + sha256_num[i]);
            // }
        } catch (FileNotFoundException e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            input_num.close();
        }

        // 获取学生的学号加密后的hash值，计算学号hash值对应的十进制值，存于sha256_num_decimal中（存到一个HashMap中）
        sha256_num_decimal = new HashMap<>();
        double temp = 0;
        for (int i = 0; i < num; i++) { // 遍历每个学号
            for (int j = 0; j < 64; j++) { // 计算当前学号的hash对应的十进制值
                temp += Math.pow(hashTemplateMap.get(sha256_num[i].charAt(j)), 63 - j);
            }
            sha256_num_decimal.put(i, temp); // 结果存入一个HashMap，key=i（默认序号），value=十进制形式的hash值
        }
        // System.out.println(sha256_num_decimal); //打印测试

    }

    // 获取种子对应的sha256加密后的hash值，存于sha256_seed中
    public void getRandomSeed() {

        try {
            // 输入随机数种子
            System.out.print("请粘贴随机数种子：");
            input_seed = new Scanner(System.in);
            // 测试用随机数种子
            // String seed =
            // "长腿熊本熊：经典抬杠，无缝是指跑跑卡丁车的一种脚本，可以取消两次连续氮气喷射中间的后摇，经常用于刷地图记录，因为取消这种后摇可以节约0.0几秒的时间，但人手是无法做到的，无缝已经存在很多年了，官方没有对其作出限制或者封禁，而杠精会用这个来diss跑跑卡丁车的高手";
            String seed = input_seed.nextLine();
            sha256_seed = ComputeHashValue.getSHA256StrJava(seed);
            // System.out.println();
            // System.out.println("随机数种子经sha256加密后的值：" + sha256_seed); //打印测试
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            input_seed.close();
        }

        // 获得随机数种子加密后的hash值后，计算随机数种子hash值对应的十进制，存于变量sha256_seed_decimal中
        sha256_seed_decimal = 0; // 存放随机数种子hash值的十进制形式
        for (int i = 0; i < 64; i++) { // 计算随机数种子hash值对应的十进制值
            // System.out.println(sha256_seed.charAt(i)); //输出测试
            sha256_seed_decimal += Math.pow(hashTemplateMap.get(sha256_seed.charAt(i)), 63 - i);
        }
        // System.out.println();
        // System.out.println("随机数种子对应的十进制值：" + sha256_seed_decimal); //打印测试
    }

    // 对随机数种子和学生的学号进行处理，即对sha256_num和sha256_seed进行处理，并选出最终的幸运儿
    public void chooseTheLuckyPerson() {
        // 定义一个HashMAp->subtractMap，key=学号，value=学号hash值与随机数种子hsah值差的绝对值
        Map<String, Double> subtractMap = new HashMap<>();
        for (int i = 0; i < num; i++) {
            subtractMap.put(arrayList.get(i), Math.abs(sha256_num_decimal.get(i) - sha256_seed_decimal));
        }
        // 需要对subtratMap里的Entry<K, V>按value值的大小进行排序
        // 1.先将subtractMap里的entrySet放入list集合
        List<Map.Entry<String, Double>> list = new ArrayList<>(subtractMap.entrySet());
        // 2.对list进行排序，并通过Comparator传入自定义的排序规则
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            //重写排序规则，返回值小于0表示升序，大于0表示降序
            @Override
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                // TODO Auto-generated method stub
                return (int) (o1.getValue() - o2.getValue());
            }
        });
        // 3.使用forEach遍历list中的键值对
        System.out.println();
        System.out.println("排序结果：");
        list.forEach(obj -> System.out.println(obj));
    }

    public static void main(String[] args) {

        Candidators candidators = new Candidators();
        candidators.hexTemplate();
        candidators.getSchoolNum();
        candidators.getRandomSeed();
        candidators.chooseTheLuckyPerson();
    }
}