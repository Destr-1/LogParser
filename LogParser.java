package com.javarush.task.task39.task3913;

import com.javarush.task.task39.task3913.query.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogParser implements IPQuery, UserQuery, DateQuery, EventQuery, QLQuery {
    private Path logDir;
    private List<LogEntity> logEntities = new ArrayList<>();
    private DateFormat formatter = new SimpleDateFormat("d.M.yyyy H:m:s");

    public LogParser(Path logDir) {
        this.logDir = logDir;
        logEntities = getListLogs();
    }

    List<LogEntity> getListLogs() {
        List<LogEntity> listLogs = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDir, "*.log")) {
            for (Path file : stream) {
                BufferedReader reader = new BufferedReader(new FileReader(file.toFile()));
                while (reader.ready()) {
                    String[] sa = reader.readLine().split("\t");
                    String ip = sa[0];
                    String user = sa[1];
                    Date date = formatter.parse(sa[2]);
                    String[] st = sa[3].split(" ");
                    Event event = Event.valueOf(st[0]);
                    int taskNumber = -1;
                    if (event.equals(Event.SOLVE_TASK) || event.equals(Event.DONE_TASK))
                        taskNumber = Integer.parseInt(st[1]);
                    Status status = Status.valueOf(sa[4]);
                    LogEntity logEntity = new LogEntity(ip, user, date, event, taskNumber, status);
                    listLogs.add(logEntity);
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return listLogs;
    }

    boolean dateCheck(Date date, Date after, Date before) {
        if (after == null)
            after = new Date(Long.MIN_VALUE);
        if (before == null)
            before = new Date(Long.MAX_VALUE);
        return (date.after(after)) &&
                (date.before(before));
    }

    @Override
    public int getNumberOfUniqueIPs(Date after, Date before) {

        return getUniqueIPs(after, before).size();
    }

    @Override
    public Set<String> getUniqueIPs(Date after, Date before) {
        Set<String> st = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)) {
                st.add(entity.getIp());
            }
        }
        return st;
    }

    @Override
    public Set<String> getIPsForUser(String user, Date after, Date before) {
        Set<String> st = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before) && entity.user.equals(user)) {
                st.add(entity.getIp());
            }
        }
        return st;
    }

    @Override
    public Set<String> getIPsForEvent(Event event, Date after, Date before) {
        Set<String> st = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before) && event.equals(entity.event)) {
                st.add(entity.getIp());
            }
        }
        return st;
    }

    @Override
    public Set<String> getIPsForStatus(Status status, Date after, Date before) {
        Set<String> st = new HashSet<>();

        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && status == entity.getStatus()) {
                st.add(entity.getIp());
            }
        }
        return st;
    }

    @Override
    public Set<String> getAllUsers() {
        Set<String> users = new HashSet<>();
        for (LogEntity entity : logEntities) {
            users.add(entity.getUser());
        }
        return users;
    }

    @Override
    public int getNumberOfUsers(Date after, Date before) {
        Set<String> users = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)) {
                users.add(entity.getUser());
            }
        }
        return users.size();
    }

    @Override
    public int getNumberOfUserEvents(String user, Date after, Date before) {
        Set<Event> events = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.user.equals(user)) {
                events.add(entity.event);
            }
        }
        return events.size();
    }

    @Override
    public Set<String> getUsersForIP(String ip, Date after, Date before) {
        Set<String> users = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getIp().equals(ip)) {
                users.add(entity.getUser());
            }
        }
        return users;
    }

    @Override
    public Set<String> getLoggedUsers(Date after, Date before) {
        Set<String> users = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getEvent().equals(Event.LOGIN)) {
                users.add(entity.getUser());
            }
        }
        return users;
    }

    @Override
    public Set<String> getDownloadedPluginUsers(Date after, Date before) {
        Set<String> users = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getEvent().equals(Event.DOWNLOAD_PLUGIN)) {
                users.add(entity.getUser());
            }
        }
        return users;

    }

    @Override
    public Set<String> getWroteMessageUsers(Date after, Date before) {
        Set<String> users = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getEvent().equals(Event.WRITE_MESSAGE)) {
                users.add(entity.getUser());
            }
        }
        return users;
    }

    @Override
    public Set<String> getSolvedTaskUsers(Date after, Date before) {
        Set<String> users = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getEvent().equals(Event.SOLVE_TASK)) {
                users.add(entity.getUser());
            }
        }
        return users;
    }

    @Override
    public Set<String> getSolvedTaskUsers(Date after, Date before, int task) {
        Set<String> users = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getEvent().equals(Event.SOLVE_TASK)
                    && entity.getTaskNumber() == task) {
                users.add(entity.getUser());
            }
        }
        return users;
    }

    @Override
    public Set<String> getDoneTaskUsers(Date after, Date before) {
        Set<String> users = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getEvent().equals(Event.DONE_TASK)) {
                users.add(entity.getUser());
            }
        }
        return users;

    }

    @Override
    public Set<String> getDoneTaskUsers(Date after, Date before, int task) {
        Set<String> users = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getEvent().equals(Event.DONE_TASK)
                    && entity.getTaskNumber() == task) {
                users.add(entity.getUser());
            }
        }
        return users;
    }

    @Override
    public Set<Date> getDatesForUserAndEvent(String user, Event event, Date after, Date before) {
        Set<Date> sd = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getUser().equals(user)
                    && entity.getEvent().equals(event)) {
                sd.add(entity.getDate());
            }
        }
        return sd;

    }

    @Override
    public Set<Date> getDatesWhenSomethingFailed(Date after, Date before) {
        Set<Date> sd = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getStatus().equals(Status.FAILED)) {
                sd.add(entity.getDate());
            }
        }
        return sd;
    }

    @Override
    public Set<Date> getDatesWhenErrorHappened(Date after, Date before) {
        Set<Date> sd = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getStatus().equals(Status.ERROR)) {
                sd.add(entity.getDate());
            }
        }
        return sd;
    }

    @Override
    public Date getDateWhenUserLoggedFirstTime(String user, Date after, Date before) {
        Date date = new Date(Long.MAX_VALUE);
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getUser().equals(user)
                    && entity.getEvent().equals(Event.LOGIN)) {
                if (entity.getDate().before(date))
                    date = entity.getDate();
            }
        }
        if (date.equals(new Date(Long.MAX_VALUE)))
            date = null;
        return date;
    }

    @Override
    public Date getDateWhenUserSolvedTask(String user, int task, Date after, Date before) {
        Date date = new Date(Long.MAX_VALUE);
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getUser().equals(user)
                    && entity.getEvent().equals(Event.SOLVE_TASK)
                    && entity.getTaskNumber() == task) {
                if (entity.getDate().before(date))
                    date = entity.getDate();
            }
        }
        if (date.equals(new Date(Long.MAX_VALUE)))
            date = null;
        return date;
    }

    @Override
    public Date getDateWhenUserDoneTask(String user, int task, Date after, Date before) {
        Date date = new Date(Long.MAX_VALUE);
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getUser().equals(user)
                    && entity.getEvent().equals(Event.DONE_TASK)
                    && entity.getTaskNumber() == task) {
                if (entity.getDate().before(date))
                    date = entity.getDate();
            }
        }
        if (date.equals(new Date(Long.MAX_VALUE)))
            date = null;
        return date;
    }

    @Override
    public Set<Date> getDatesWhenUserWroteMessage(String user, Date after, Date before) {
        Set<Date> sd = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getUser().equals(user)
                    && entity.getEvent().equals(Event.WRITE_MESSAGE)) {
                sd.add(entity.getDate());
            }
        }
        return sd;
    }

    @Override
    public Set<Date> getDatesWhenUserDownloadedPlugin(String user, Date after, Date before) {
        Set<Date> sd = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getUser().equals(user)
                    && entity.getEvent().equals(Event.DOWNLOAD_PLUGIN)) {
                sd.add(entity.getDate());
            }
        }
        return sd;
    }

    @Override
    public int getNumberOfAllEvents(Date after, Date before) {
        return getAllEvents(after, before).size();
    }

    @Override
    public Set<Event> getAllEvents(Date after, Date before) {
        Set<Event> se = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)) {
                se.add(entity.getEvent());
            }
        }
        return se;
    }

    @Override
    public Set<Event> getEventsForIP(String ip, Date after, Date before) {
        Set<Event> se = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getIp().equals(ip)) {
                se.add(entity.getEvent());
            }
        }
        return se;
    }

    @Override
    public Set<Event> getEventsForUser(String user, Date after, Date before) {
        Set<Event> se = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getUser().equals(user)) {
                se.add(entity.getEvent());
            }
        }
        return se;
    }

    @Override
    public Set<Event> getFailedEvents(Date after, Date before) {
        Set<Event> se = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getStatus().equals(Status.FAILED)) {
                se.add(entity.getEvent());
            }
        }
        return se;
    }

    @Override
    public Set<Event> getErrorEvents(Date after, Date before) {
        Set<Event> se = new HashSet<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getStatus().equals(Status.ERROR)) {
                se.add(entity.getEvent());
            }
        }
        return se;
    }

    @Override
    public int getNumberOfAttemptToSolveTask(int task, Date after, Date before) {
        int count = 0;
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getEvent().equals(Event.SOLVE_TASK)
                    && entity.getTaskNumber() == task) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getNumberOfSuccessfulAttemptToSolveTask(int task, Date after, Date before) {
        int count = 0;
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getEvent().equals(Event.DONE_TASK)
                    && entity.getTaskNumber() == task) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Map<Integer, Integer> getAllSolvedTasksAndTheirNumber(Date after, Date before) {
        Map<Integer, Integer> mp = new HashMap<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getEvent().equals(Event.SOLVE_TASK)) {
                if (!mp.containsKey(entity.taskNumber)) {
                    mp.put(entity.taskNumber, 0);
                }
                mp.put(entity.taskNumber, mp.get(entity.taskNumber) + 1);
            }
        }
        return mp;
    }

    @Override
    public Map<Integer, Integer> getAllDoneTasksAndTheirNumber(Date after, Date before) {
        Map<Integer, Integer> mp = new HashMap<>();
        for (LogEntity entity : logEntities) {
            if (dateCheck(entity.getDate(), after, before)
                    && entity.getEvent().equals(Event.DONE_TASK)) {
                if (!mp.containsKey(entity.taskNumber)) {
                    mp.put(entity.taskNumber, 0);
                }
                mp.put(entity.taskNumber, mp.get(entity.taskNumber) + 1);
            }
        }
        return mp;
    }

    @Override
    public Set<Object> execute(String query) {
        Set<Object> st = new HashSet<>();
        HashMap<String, Function<LogEntity, Command>> commands = new HashMap<>();
        {
            commands.put("ip", GetIpCommand::new);
            commands.put("user", GetUserCommand::new);
            commands.put("date", GetDateCommand::new);
            commands.put("event", GetEventCommand::new);
            commands.put("status", GetStatusCommand::new);
        }

        Pattern pattern = Pattern.compile("get (ip|user|date|event|status)" +
                "( for (ip|user|date|event|status) = \"(.*?)\")?" +
                "( and date between \"(.*?)\" and \"(.*?)\")?");

        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            String field1 = matcher.group(1);
            Stream<LogEntity> logEntitiesStream = logEntities.stream();
            if (matcher.group(2) != null) {
                String field2 = matcher.group(3);
                String value1 = matcher.group(4);
                logEntitiesStream = logEntities.stream()
                        .filter(logEntity -> {
                                    Command method = commands.get(field2).apply(logEntity);
                                    if (field2.equals("date")) {
                                        Date date = new Date();
                                        try {
                                            date = formatter.parse(value1);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        return method.execute().equals(date);
                                    }
                                    return method.execute().toString().equals(value1);
                                }
                        );
            }
            if (matcher.group(5) != null) {
                Date after = null, before = null;
                try {
                    after = formatter.parse(matcher.group(6));
                    before = formatter.parse(matcher.group(7));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date finalAfter = after;
                Date finalBefore = before;
                logEntitiesStream = logEntitiesStream
                        .filter(logEntity -> dateCheck(logEntity.getDate(), finalAfter, finalBefore));
            }

            st = logEntitiesStream.map(logEntity -> {
                Command method = commands.get(field1).apply(logEntity);
                return method.execute();
            }).collect(Collectors.toSet());
        }
        return st;
    }


    private class LogEntity {
        private String ip;
        private String user;
        private Date date;
        private Event event;
        private int taskNumber;
        private Status status;

        public LogEntity(String ip, String user, Date date, Event event, int taskNumber, Status status) {
            this.ip = ip;
            this.user = user;
            this.date = date;
            this.event = event;
            this.taskNumber = taskNumber;
            this.status = status;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        public int getTaskNumber() {
            return taskNumber;
        }

        public void setTaskNumber(int taskNumber) {
            this.taskNumber = taskNumber;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }

    private abstract class Command {
        protected LogEntity entity;

        abstract Object execute();
    }

    private class GetIpCommand extends Command {
        public GetIpCommand(LogEntity entity) {
            this.entity = entity;
        }

        @Override
        Object execute() {
            return entity.getIp();
        }
    }

    private class GetUserCommand extends Command {
        public GetUserCommand(LogEntity entity) {
            this.entity = entity;
        }

        @Override
        Object execute() {
            return entity.getUser();
        }
    }

    private class GetDateCommand extends Command {
        public GetDateCommand(LogEntity entity) {
            this.entity = entity;
        }

        @Override
        Object execute() {
            return entity.getDate();
        }
    }

    private class GetEventCommand extends Command {
        public GetEventCommand(LogEntity entity) {
            this.entity = entity;
        }

        @Override
        Object execute() {
            return entity.getEvent();
        }
    }

    private class GetStatusCommand extends Command {
        public GetStatusCommand(LogEntity entity) {
            this.entity = entity;
        }

        @Override
        Object execute() {
            return entity.getStatus();
        }
    }
}