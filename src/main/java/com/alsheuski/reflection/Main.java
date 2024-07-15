package com.alsheuski.reflection;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class Main {
  public void printString(){
    GenericParent<?,?,?> instance = new ParentGetter().get();
   // instance.get(null);
   // var instance = new GenericChild();
   // instance.printSize();
    //var str = instance.getSomeString();
   // System.out.println(str);
  }

  class ParentGetter{

    public GenericParent<?,?,?> get(){
      return new GenericChild();
    }
  }
}


