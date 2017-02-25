package com.yl.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public class TestJna {
    //上层接口  
    //需要继承stdCallLibrary，一种协议，当然也可以直接继承Library上层接口，看对方的DLL文件的编写遵从哪一种协议， 这个是jna标准    
    //必须定义一个接口,将Dll文件的方法提取出来，注意类型的对应  
    public interface EncrypStrTest extends Library  {  
          
         //加载动态链接库，把库dll文件默认放到系统C盘window目录下的system32文件夹下或者到java的bin目录    
        EncrypStrTest instance = (EncrypStrTest)Native.loadLibrary("ClientApplication", EncrypStrTest.class);  
        //定义接口，  
        public String getStr();//定义接口  
  
    }  
      
    //测试  
    public static void main(String[] args) {  
        try{  
            EncrypStrTest jnaDemo=EncrypStrTest.instance;  
            String resultString=jnaDemo.getStr();  
            System.out.println("会有结果么：" + resultString);  
        }catch(Exception e){    
            e.printStackTrace();    
        }    
    }  
  
}
