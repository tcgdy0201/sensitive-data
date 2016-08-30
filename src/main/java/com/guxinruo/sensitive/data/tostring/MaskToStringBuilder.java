package com.guxinruo.sensitive.data.tostring;

import com.guxinruo.sensitive.data.anno.Mask;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

/**
 * Created by 顾冬煜
 * Date : 2016-08-04
 */
public class MaskToStringBuilder extends ReflectionToStringBuilder {
    /**
     * <p>
     * Constructor.
     * </p>
     * <p>
     * <p>
     * This constructor outputs using the default style set with <code>setDefaultStyle</code>.
     * </p>
     *
     * @param object the Object to build a <code>toString</code> for, must not be <code>null</code>
     * @throws IllegalArgumentException if the Object passed in is <code>null</code>
     */
    public MaskToStringBuilder(Object object) {
        super(object);
    }

    /**
     * <p>
     * Constructor.
     * </p>
     * <p>
     * <p>
     * If the style is <code>null</code>, the default style is used.
     * </p>
     *
     * @param object the Object to build a <code>toString</code> for, must not be <code>null</code>
     * @param style  the style of the <code>toString</code> to create, may be <code>null</code>
     * @throws IllegalArgumentException if the Object passed in is <code>null</code>
     */
    public MaskToStringBuilder(Object object, ToStringStyle style) {
        super(object, style);
    }



    /**
     * <p>
     * Appends the fields and values defined by the given object of the given Class.
     * </p>
     *
     * <p>
     * If a cycle is detected as an object is &quot;toString()'ed&quot;, such an object is rendered as if
     * <code>Object.toString()</code> had been called and not implemented by the object.
     * </p>
     *
     * @param clazz
     *            The class of object parameter
     */
    protected void appendFieldsIn(Class clazz) {
        if (clazz.isArray()) {
            this.reflectionAppendArray(this.getObject());
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            if (this.accept(field)) {
                try {
                    Object fieldValue = this.getValue(field);
                    if(fieldValue==null){
                        continue;
                    }
                    //如果需要打码
                    Mask anno = field.getAnnotation(Mask.class);
                    if(anno!=null){
                        if(field.getType()==String.class){
                            String strFieldVal = (String) fieldValue;
                            int length = strFieldVal.length();
                            int totalNoMaskLen = anno.prefixNoMaskLen() + anno.suffixNoMaskLen();
                            if(totalNoMaskLen ==0 ){
                                fieldValue = fillMask(anno.maskStr(),length);
                            }

                            if(totalNoMaskLen < length){
                                StringBuilder sb = new StringBuilder();
                                for(int j=0;j<strFieldVal.length();j++){
                                    if(j<anno.prefixNoMaskLen()){
                                        sb.append(strFieldVal.charAt(j));
                                        continue;
                                    }
                                    if(j>(strFieldVal.length()-anno.suffixNoMaskLen()-1)){
                                        sb.append(strFieldVal.charAt(j));
                                        continue;
                                    }
                                    sb.append(anno.maskStr());
                                }
                                fieldValue = sb.toString();
                            }
                        }
                    }


                    // Warning: Field.get(Object) creates wrappers objects
                    // for primitive types.
                    this.append(fieldName, fieldValue);
                } catch (IllegalAccessException ex) {
                    //this can't happen. Would get a Security exception
                    // instead
                    //throw a runtime exception in case the impossible
                    // happens.
                    throw new InternalError("Unexpected IllegalAccessException: " + ex.getMessage());
                }
            }
        }
    }

    private String fillMask(String maskStr,int length){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<length;i++){
            sb.append(maskStr);
        }
        return sb.toString();
    }


}

//class TestClz{
//    @Size
//    @Mask(prefixNoMaskLen = 1,maskStr = "*",suffixNoMaskLen = 0)
//    @NotNull
//    private String name;
//    private int age;
//    @Mask(prefixNoMaskLen = 6,maskStr = "*",suffixNoMaskLen = 3)
//    private String idNo;
//
//    private TestClz clz;
//
//    public TestClz getClz() {
//        return clz;
//    }
//
//    public void setClz(TestClz clz) {
//        this.clz = clz;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getAge() {
//        return age;
//    }
//
//    public void setAge(int age) {
//        this.age = age;
//    }
//
//    public String getIdNo() {
//        return idNo;
//    }
//
//    public void setIdNo(String idNo) {
//        this.idNo = idNo;
//    }
//
//    public static void main(String[] args) {
//        TestClz c = new TestClz();
//        c.setName("顾冬煜");
//        c.setIdNo("310115188888888888");
//        c.setAge(20);
//
//
//        TestClz d = new TestClz();
//        d.setName("321932919顾冬煜");
//        d.setAge(20);
//        c.setClz(d);
//        System.out.println(c);
//
//    }
//
//
//    @Override
//    public String toString() {
//        return new MaskToStringBuilder(this).toString();
//    }
//}