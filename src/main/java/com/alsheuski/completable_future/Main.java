package com.alsheuski.completable_future;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {

   public static int tt = 0;
   
   public Main(){
     tt++;
   }


  public static void main(String[] args) throws IOException {
    List<Order> list= new ArrayList<>();
 
  }



  public class Order {
    Date date;
    String name;

    public Date getDate() {
      return date;
    }

    public void setDate(Date date) {
      this.date = date;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

}
