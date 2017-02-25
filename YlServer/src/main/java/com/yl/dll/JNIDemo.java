package com.yl.dll;

public class JNIDemo {  
    //定义一个本地方法  
    public native String getStr();  
    
    public static void main(String[] args){  
        //调用动态链接库  
        System.loadLibrary("ClientApplication");  
        JNIDemo jniDemo = new JNIDemo();  
        jniDemo.getStr();  
    }  
} 
