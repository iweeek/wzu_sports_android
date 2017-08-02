package com.tim.app.server.entry;

import android.util.Log;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @创建者 倪军
 * @创建时间 2017/8/2
 * @描述
 */

public class Test {

    private static List<person> mList = new ArrayList<>();
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {

            person person =new person();
            person.setAge( i+10);
            person.setName("baba"+i);
            mList.add(person);
        }
        Log.d("Test", mList.toString());
        Collections.sort(mList,new MyComparator());
        Log.d("Test", mList.toString());
    }

}


class  person {

    private  String name;
    private  int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

class MyComparator implements Comparator<person> {
    @Override
    public int compare(person lhs, person rhs) {
        Collator collator = Collator.getInstance();
        return collator.compare(lhs.getAge(),rhs.getAge());
    }
}