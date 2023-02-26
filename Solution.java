package com.javarush.task.task39.task3913;

import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Solution {
    public static void main(String[] args) throws ParseException {
        LogParser logParser = new LogParser(Paths.get("c:/logs/"));

        DateFormat formatter = new SimpleDateFormat("d.M.y H:m:s");
        Date after = formatter.parse("19.03.2016 00:00:00");
        Date before = formatter.parse("29.2.2026 5:4:7");
//        System.out.println(logParser.execute("get user for date = \"30.01.2014 12:56:22\""));
//        System.out.println(logParser.execute("get user for event = \"DONE_TASK\""));
//        System.out.println(logParser.execute("get ip for user = \"Vasya Pupkin\""));
//        System.out.println(logParser.execute("get user"));
        System.out.println(logParser.execute(
                "get ip for event = \"SOLVE_TASK\" and date between \"null\" and \"01.01.2099 23:59:59\""));


//        System.out.println(logParser.getDoneTaskUsers(after, before, 48));
    }
}