# 脱敏工具类




## 背景
　　所有敏感信息都需要脱敏传输，这就需要对打日志时进行脱敏工作。

　　许多框架的dto其顶层都是一个自定义的Printable的类,Override了toString方法,打印了所有fields,就有可能导致敏感信息泄露。
　　


## 目标
开发人员能相对比较容易的对需要脱敏的字段进行脱敏，并且脱敏的格式可以自定义。如姓名的话只显示首字母、手机号只显示前5位。

## 实现方案
* 将本工程deploy或install至私服或本地。

`mvn clean install` || `mvn clean deploy`

* 导入依赖包

在需要使用脱敏功能的工程内引入该包

```
<dependency>
    <groupId>com.guxinruo</groupId>
    <artifactId>sensitive-data</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

* override 你的dto基类,如Printable内的toString方法,或每个需要脱敏的对象单独ovveride

```
@Override
public String toString() {
   return new MaskToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
}
```

* 在需要脱敏的字段上增加注解,注意：
	1. 目前仅支持对String对象脱敏；
	2. 内嵌对象中的String也可被脱敏;
	3. 被脱敏的字段的所属对象必须继承改造后的Printable类，或override toString

## 脱敏演示

```
/**
 * 绑卡申请
 * Created by 顾冬煜
 */
public class BindBankCardReq{
    //一些其他字段略

    /*********四要素 end**********/
    /**
     * 真实姓名
     */
    @NotNull
    @Size(max=30)
    @Mask(prefixNoMaskLen = 1)
    private String name;
    /**
     * 身份证号
     */
    @NotNull
    @Size(max=18)
    @Mask
    private String idNo;
    /**
     * 银行卡号
     */
    @NotNull
    @Size(max=20)
    @Mask(suffixNoMaskLen = 4)
    private String bankCardNo;
    /**
     * 银行预留手机号
     */
    @Size(max=18)
    @Mask(prefixNoMaskLen = 3,suffixNoMaskLen = 3)
    private String mobile;
    /*********四要素 end**********/
    
```

## 脱敏后效果（打印出的对象）：

```
public static void main(String[] args) {
    BindBankCardReq req =  new BindBankCardReq();
    req.setIdNo("310110199999999999");
    req.setBankCardNo("6220622062206220");
    req.setMobile("18888888888");
    req.setCvv2("888");
    req.setBankId("ICBC");
    req.setBankName("工商银行");
    req.setCardType("C");
    req.setEndDate("201909");
    req.setName("郭小明");
    System.out.println(req);
}
```

**toString结果**

```
BindBankCardReq[name=郭**,idNo=******************,bankCardNo=************6220,mobile=188*****888,bankId=ICBC,bankName=工商银行,cardType=C,cvv2=***,endDate=******,merchantCode=hj,userId=123456,transAcctType=z1]
```

## Mask注解详解


```
/**
 * 用来打码
 * Created by 顾冬煜
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mask {
    /**
     * 前置不需要打码的长度
     * @return
     */
    int prefixNoMaskLen() default 0;
 
    /**
     * 后置不需要打码的长度
     * @return
     */
    int suffixNoMaskLen() default 0;
 
    /**
     * 用什么打码
     * @return
     */
    String maskStr() default "*";
}
```