package com.github.lixiang2114.flume.plugin.emqx.util;

/**
 * @author Louis(LiXiang)
 */
public class DateUtil {
    /**
     * 时间格式表
     */
    private static final java.util.List<String> FORMAT_LIST = java.util.Arrays.asList("yyyy","MM","dd","HH","mm","ss","S");

    /**
     * 时间解析正则式
     */
    private static final java.util.regex.Pattern DATE_REGEX = java.util.regex.Pattern.compile("(\\d{4})?-?([01]\\d{1})?(?!:)-?([0123]\\d{1})?(?!:)\\s*([012]\\d{1})?:?([012345]\\d{1})?:?([012345]\\d{1})?\\s*(\\d{1,3})?");
	
    /**
     * @return
     */
    public static String getSimpleDateFormatStr(){
    	return "yyyy-MM-dd HH:mm:ss S";
    }
    
    /**
     * @return
     */
    public static java.text.DateFormat getDefaultDateFormat(){
    	return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * @param calendar
     * @return
     */
    public static String calendarToString(java.util.Calendar calendar){
    	String dateStr=new java.sql.Timestamp(calendar.getTimeInMillis()).toString();
    	int endIndex=dateStr.indexOf(".");
    	return dateStr.substring(0, -1==endIndex?dateStr.length():endIndex).trim();
    }
    
    /**
     * @param calendar
     * @return
     */
    public static long calendarToMillSeconds(java.util.Calendar calendar){
    	return calendar.getTimeInMillis();
    }
    
    /**
     * @param calendar
     * @return
     */
    public static java.util.Date calendarToDate(java.util.Calendar calendar){
    	return new java.util.Date(calendar.getTimeInMillis());
    }
    
    /**
     * @param calendar
     * @return
     */
    public static java.sql.Date calendarToSqlDate(java.util.Calendar calendar){
    	return new java.sql.Date(calendar.getTimeInMillis());
    }
    
    /**
     * @param calendar
     * @return
     */
    public static java.sql.Time calendarToTime(java.util.Calendar calendar){
    	return new java.sql.Time(calendar.getTimeInMillis());
    }
    
    /**
     * @param calendar
     * @return
     */
    public static java.sql.Timestamp calendarToTimestamp(java.util.Calendar calendar){
    	return new java.sql.Timestamp(calendar.getTimeInMillis());
    }
    
    /**
     * @param date
     * @return
     */
    public static java.util.Calendar dateToCalendar(java.util.Date date){
    	java.util.Calendar calendar=java.util.Calendar.getInstance();
    	calendar.setTimeInMillis(date.getTime());
    	return calendar;
    }
    
    /**
     * @param millSeconds
     * @return
     */
    public static java.util.Calendar millSecondsToCalendar(long millSeconds){
    	java.util.Calendar calendar=java.util.Calendar.getInstance();
    	calendar.setTimeInMillis(millSeconds);
    	return calendar;
    }
    
    /**
     * @param dateString
     * @return
     */
    public static java.util.Calendar stringToCalendar(String dateString){
    	java.util.Calendar calendar=java.util.Calendar.getInstance();
    	calendar.setTimeInMillis(stringToDate(dateString,java.util.Date.class).getTime());
    	return calendar;
    }
    
    /**
     * @param dateString
     * @return
     */
    public static java.util.Date stringToDate(String dateString){
    	return (java.util.Date)stringToDate(dateString,java.util.Date.class);
    }
    
    /**
     * @param dateString
     * @return
     */
    public static java.sql.Date stringToSqlDate(String dateString){
    	return (java.sql.Date)stringToDate(dateString,java.sql.Date.class);
    }
    
    /**
     * @param dateString
     * @return
     */
    public static java.sql.Time stringToTime(String dateString){
    	return (java.sql.Time)stringToDate(dateString,java.sql.Time.class);
    }
    
    /**
     * @param dateString
     * @return
     */
    public static java.sql.Timestamp stringToTimestamp(String dateString){
    	return (java.sql.Timestamp)stringToDate(dateString,java.sql.Timestamp.class);
    }
    
    /**
     * @param millSeconds
     * @return
     */
    public static java.util.Date millSecondsToDate(long millSeconds){
    	return (java.util.Date)millSecondsToDate(millSeconds,java.util.Date.class);
    }
    
    /**
     * @param millSeconds
     * @return
     */
    public static java.sql.Date millSecondsToSqlDate(long millSeconds){
    	return (java.sql.Date)millSecondsToDate(millSeconds,java.sql.Date.class);
    }
    
    /**
     * @param millSeconds
     * @return
     */
    public static java.sql.Time millSecondsToTime(long millSeconds){
    	return (java.sql.Time)millSecondsToDate(millSeconds,java.sql.Time.class);
    }
    
    /**
     * @param millSeconds
     * @return
     */
    public static java.sql.Timestamp millSecondsToTimestamp(long millSeconds){
    	return (java.sql.Timestamp)millSecondsToDate(millSeconds,java.sql.Timestamp.class);
    }
    
    /**
     * @param millSeconds
     * @return
     */
    public static String millSecondsToString(long millSeconds){
    	String dateStr=new java.sql.Timestamp(millSeconds).toString();
    	int endIndex=dateStr.indexOf(".");
    	return dateStr.substring(0, -1==endIndex?dateStr.length():endIndex).trim();
    }
    
    /**
     * @param date
     * @return
     */
    public static long dateToMillSeconds(java.util.Date date){
    	return date.getTime();
    }
    
    /**
     * @param date
     * @return
     */
    public static String dateToString(java.util.Date date){
    	String dateStr=new java.sql.Timestamp(date.getTime()).toString();
    	int endIndex=dateStr.indexOf(".");
    	return dateStr.substring(0, -1==endIndex?dateStr.length():endIndex).trim();
    }
    
    /**
     * @param dateString
     * @return
     */
    public static long stringToMillSeconds(String dateString){
    	return stringToDate(dateString,java.sql.Timestamp.class).getTime();
    }
    
    /**
     * @param millSeconds
     * @param dateType
     * @return
     */
    public static java.util.Date millSecondsToDate(long millSeconds,Class<? extends java.util.Date> dateType){
    	try {
			return dateType.getConstructor(long.class).newInstance(millSeconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * @param dateString
     * @param dateType
     * @return
     */
    public static java.util.Date stringToDate(String dateString,Class<? extends java.util.Date> dateType){
    	try {
			return dateType.getConstructor(long.class).newInstance(getDateFormat(dateString).parse(dateString).getTime());
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * @param dateString
     * @return
     */
    public static java.text.DateFormat getDateFormat(String dateString){
        return new java.text.SimpleDateFormat(getDateFormatStr(dateString));
    }
    
    /**
     * @param dateString
     * @return
     */
    public static String getDateFormatStr(String dateString){
        if(null==dateString||dateString.trim().isEmpty())return null;
        java.util.regex.Matcher matcher=DATE_REGEX.matcher(dateString);
        if(!matcher.find()) return null;

        Integer groupCount=matcher.groupCount();
        StringBuilder formatBuilder=new StringBuilder();
        for(int i=1;i<=groupCount;i++){
            String curMatch=matcher.group(i);
            if(null==curMatch) continue;
            String format=FORMAT_LIST.get(i-1);
            if(i<3){
                formatBuilder.append(format).append("-");
                continue;
            }
            if(i==3){
                formatBuilder.append(format).append(" ");
                continue;
            }
            if(i<6){
                formatBuilder.append(format).append(":");
                continue;
            }
            if(i==6){
                formatBuilder.append(format).append(" ");
                continue;
            }
            formatBuilder.append(format).append(" ");
        }

        char lastChar=formatBuilder.charAt(formatBuilder.length()-1);
        if('-'==lastChar||':'==lastChar||' '==lastChar)formatBuilder.deleteCharAt(formatBuilder.length()-1);
        return formatBuilder.toString().trim();
    }
}
