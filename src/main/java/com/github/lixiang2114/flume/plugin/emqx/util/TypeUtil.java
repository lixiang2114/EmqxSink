package com.github.lixiang2114.flume.plugin.emqx.util;

import java.lang.reflect.Array;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * @author Louis(LiXiang)
 */
@SuppressWarnings({ "unchecked" })
public class TypeUtil {
	/**
	 * 基本类型到包装类型映射字典
	 */
	public static final HashMap<Class<?>,Class<?>> BASE_TO_WRAP;
	
	/**
     * 逗号正则式
     */
	public static final Pattern COMMA_SEPARATOR=Pattern.compile(",");
	
	/**
     * 数字正则式
     */
	public static final Pattern NUMBER_CHARACTER=Pattern.compile("[0-9.]+");
    
	static{
		BASE_TO_WRAP=new HashMap<Class<?>,Class<?>>();
		BASE_TO_WRAP.put(byte.class, Byte.class);
		BASE_TO_WRAP.put(short.class, Short.class);
		BASE_TO_WRAP.put(int.class, Integer.class);
		BASE_TO_WRAP.put(long.class, Long.class);
		BASE_TO_WRAP.put(float.class, Float.class);
		BASE_TO_WRAP.put(double.class, Double.class);
		BASE_TO_WRAP.put(boolean.class, Boolean.class);
		BASE_TO_WRAP.put(char.class, Character.class);
		BASE_TO_WRAP.put(void.class, Void.class);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static final Byte toByte(String value){
		return toType(value,Byte.class);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static final Short toShort(String value){
		return toType(value,Short.class);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static final Integer toInt(String value){
		return toType(value,Integer.class);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static final Long toLong(String value){
		return toType(value,Long.class);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static final Float toFloat(String value){
		return toType(value,Float.class);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static final Double toDouble(String value){
		return toType(value,Double.class);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static final Boolean toBoolean(String value){
		return toType(value,Boolean.class);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static final Character toChar(String value){
		return toType(value,Character.class);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static final Time toTime(String value){
		return toType(value,Time.class);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static final Timestamp toTimestamp(String value){
		return toType(value,Timestamp.class);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static final Date toDate(String value){
		return toType(value,Date.class);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static final java.sql.Date toSqlDate(String value){
		return toType(value,java.sql.Date.class);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static final Calendar toCalendar(String value){
		return toType(value,Calendar.class);
	}
	
    /**
     * @param type
     * @return
     */
    public static final boolean isBaseType(Class<?> type){
    	return type.isPrimitive();
    }
    
    /**
     * @param type
     * @return
     */
    public static final boolean isWrapType(Class<?> type){
    	return BASE_TO_WRAP.containsValue(type);
    }
    
    /**
     * @param type
     * @return
     */
    public static final boolean isDateType(Class<?> type){
    	return Date.class.isAssignableFrom(type) || Calendar.class.isAssignableFrom(type);
    }
    
    /**
     * @param type
     * @return
     */
    public static final boolean isSimpleType(Class<?> type){
    	if(isBaseType(type) || isWrapType(type) || String.class.isAssignableFrom(type)) return true;
    	if(Date.class.isAssignableFrom(type) || Calendar.class.isAssignableFrom(type)) return true;
    	return false;
    }
    
    /**
     * @param type
     * @return
     */
    public static final boolean isNumber(Class<?> type){
    	if(Number.class.isAssignableFrom(type)) return true;
    	if(boolean.class==type || char.class==type || void.class==type) return false;
    	return BASE_TO_WRAP.containsKey(type);
    }
    
    /**
     * @param string
     * @return
     */
    public static boolean isNumber(String string){
    	if(null==string || 0==string.trim().length()) return false;
    	return NUMBER_CHARACTER.matcher(string).matches();
    }
    
	/**
	 * @param type
	 * @return
	 */
	public static final Class<?> getWrapType(Class<?> type){
		if(null==type) return null;
		if(!type.isPrimitive()) return type;
		return BASE_TO_WRAP.get(type);
	}
    
	/**
	 * @param superType
	 * @param childType
	 * @return
	 */
	public static final boolean compatible(Class<?> superType,Class<?> childType){
		if(superType==childType) return true;
		if(null==superType && null!=childType) return false;
		if(null!=superType && null==childType) return false;
		if(superType.isAssignableFrom(childType)) return true;
		try{
			if(superType.isPrimitive() && superType==childType.getField("TYPE").get(null)) return true;
			if(childType.isPrimitive() && childType==superType.getField("TYPE").get(null)) return true;
			return false;
		}catch(Exception e){
			return false;
		}
	}

	/**
	 * @param value
	 * @param returnType
	 * @return
	 */
	public static final <R> R toType(String value,Class<R> returnType){
		if(null==value || null==returnType) return null;
		
		value=value.trim();
		if(returnType.isAssignableFrom(String.class)) return (R)value;
		if(0==value.length()) return null;
		
		if(Date.class.isAssignableFrom(returnType)){
			return (R)DateUtil.stringToDate(value,(Class<? extends Date>)returnType);
		}else if(Calendar.class.isAssignableFrom(returnType)){
			return (R)DateUtil.stringToCalendar(value);
		}else if(isNumber(returnType)){
			if(!NUMBER_CHARACTER.matcher(value).matches()) return null;
			Class<?> wrapType=getWrapType(returnType);
    		try {
                return (R)wrapType.getConstructor(String.class).newInstance(value);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }else if(Boolean.class==returnType || boolean.class==returnType){
            return (R)Boolean.valueOf(value);
        }else if(Character.class==returnType || char.class==returnType){
            return (R)Character.valueOf(value.charAt(0));
        }else if(returnType.isArray()){
        	String[] array=COMMA_SEPARATOR.split(value);
        	Class<?> componentType=returnType.getComponentType();
        	Object newArray=Array.newInstance(componentType, array.length);
        	for(int i=0;i<array.length;Array.set(newArray, i, toType(array[i].trim(),componentType)),i++);
        	return (R)newArray;
        }else{
        	throw new RuntimeException("java.lang.String Can Not Transfer To "+returnType.getName());
        }
	}
}
