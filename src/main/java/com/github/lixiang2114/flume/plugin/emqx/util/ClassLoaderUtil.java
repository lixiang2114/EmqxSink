package com.github.lixiang2114.flume.plugin.emqx.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Louis(LiXiang)
 */
public class ClassLoaderUtil{
	/**
	 * 添加文件到类路径方法
	 */
	private static Method addClassPathMethod;
	
	static{
		try {
			addClassPathMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			addClassPathMethod.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param classPath
	 * @return
	 */
	public static String fixClassPath(String classPath){
		if(null==classPath || classPath.trim().isEmpty()) return null;
		String classPathFile=classPath.trim();
		if(classPathFile.startsWith("\\")){
			classPathFile="/"+classPathFile.substring(1);
		}else if(classPathFile.startsWith("./")||classPathFile.startsWith(".\\")){
			classPathFile="/"+classPathFile.substring(2);
		}else if(!classPathFile.startsWith("/")){
			classPathFile="/"+classPathFile;
		}
		return classPathFile;
	}
	
	/**
	 * @param absolutePathfile
	 * @return
	 */
	public static InputStream getAbsolutePathFileStream(String absolutePathfile){
		File file=new File(absolutePathfile);
		if(!file.exists()||file.isDirectory()) return null;
		try {
			return file.toURI().toURL().openStream();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @param classPath
	 * @return
	 */
	public static File getRealFile(String classPath){
		String fileFullPath=getRealPath(classPath);
		if(null==fileFullPath) return null;
		return new File(fileFullPath);
	}
	
	/**
	 * @param classPath
	 * @return
	 */
	public static String getRealPath(String classPath){
		String fileClassPath=fixClassPath(classPath);
		if(null==fileClassPath) return null;
		Class<?> caller=getCallerClass();
		if(null==caller) return null;
		return caller.getResource(fileClassPath).getPath();
	}
	
	/**
	 * @param classPathfile
	 * @return
	 */
	public static InputStream getClassPathFileStream(String classPathfile){
		String fileClassPath=fixClassPath(classPathfile);
		if(null==fileClassPath) return null;
		Class<?> caller=getCallerClass();
		if(null==caller) return null;
		return caller.getResourceAsStream(fileClassPath);
	}
	
	/**
	 * @return
	 */
	public static ClassLoader getCurrentClassLoader(){
		return getCallerClassLoader();
	}
	
	/**
	 * @return
	 */
	public static ClassLoader getCurrentCallerClassLoader(){
		Class<?> currentClass=getCallerClass();
		return getCallerClassLoader(currentClass);
	}
	
	/**
	 * @param currentClass
	 * @return
	 */
	public static ClassLoader getCallerClassLoader(Class<?>... currentClass){
		Class<?> caller=getCallerClass(currentClass);
		return null==caller?null:caller.getClassLoader();
	}
	
	/**
	 * @param currentClass
	 * @return
	 */
	public static Class<?> getCallerClass(Class<?>... currentClass){
		Class<?> type=(null==currentClass||0==currentClass.length)?ClassLoaderUtil.class:currentClass[0];
		StackTraceElement[] elements=Thread.currentThread().getStackTrace();
		String currentClassName=type.getName();
		
		int startIndex=-1;
		for(int i=0;i<elements.length;i++){
			if(!currentClassName.equals(elements[i].getClassName())) continue;
			startIndex=i;
			break;
		}
		
		if(-1==startIndex) return null;
		
		String callerClassName=null;
		for(int i=startIndex+1;i<elements.length;i++){
			String iteClassName=elements[i].getClassName();
			if(currentClassName.equals(iteClassName)) continue;
			callerClassName=iteClassName;
			break;
		}
		
		if(null==callerClassName) return null;
		
		try {
			return Class.forName(callerClassName);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public static URL[] getCurrentClassPath(Class<?>... type) throws IOException{
		ClassLoader classLoader=getCallerClassLoader(type);
		if(null==classLoader) return null;
		return ((URLClassLoader)classLoader).getURLs();
	}
	
	/**
	 * @param fullPath
	 * @param type
	 */
	public static void addFileToCurrentClassPath(String fullPath,Class<?>... type) {
		addFileToCurrentClassPath(new File(fullPath),type);
	}
	
	/**
	 * @param url
	 * @param type
	 */
	public static void addFileToCurrentClassPath(URL url,Class<?>... type) {
		File file=null;
		try {
			file=new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if(null==file) return;
		addFileToCurrentClassPath(file,type);
	}
	
	/**
	 * @param uri
	 * @param type
	 */
	public static void addFileToCurrentClassPath(URI uri,Class<?>... type) {
		addFileToCurrentClassPath(new File(uri),type);
	}
	
	/**
	 * @param file
	 * @param type
	 */
	public static void addFileToCurrentClassPath(File file,Class<?>... type) {
		if(!file.exists()) throw new RuntimeException(file.getAbsolutePath()+" is not exists...");
		
		ClassLoader classLoader=getCallerClassLoader(type);
		if(null==classLoader) throw new RuntimeException("can not get caller classloader...");
		
		addFileToCurrentClassPath(file,classLoader);
	}

	/**
	 * @param file
	 * @param classLoader
	 */
	public static void addFileToCurrentClassPath(File file,ClassLoader classLoader){
		if(file.isFile() && !file.getName().endsWith(".jar")) return;
		
		try {
			addClassPathMethod.invoke(classLoader, file.toURI().toURL());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(file.isFile()) return;
		
		File[] fileArray = file.listFiles();
		for(File subFile:fileArray) addFileToCurrentClassPath(subFile,classLoader);
	}
	
	/**
	 * @param fileFullPath
	 * @return
	 * @throws IOException
	 */
	public static ClassLoader getFileClassLoader(String fileFullPath) throws IOException{
		File file=new File(fileFullPath);
		if(!file.exists()){
			throw new FileNotFoundException(fileFullPath+" is not exists...");
		}
		if(file.isDirectory()){
			throw new IOException(fileFullPath+" is directory...");
		}
		return new URLClassLoader(new URL[]{file.toURI().toURL()});
	}
	
	/**
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static ClassLoader getPathClassLoader(String path) throws IOException{
		File fileDir=new File(path);
		if(!fileDir.exists()){
			throw new FileNotFoundException(path+" is not exists...");
		}
		if(fileDir.isFile()){
			throw new IOException(path+" is file...");
		}
		
		File[] jarFiles = fileDir.listFiles(new FilenameFilter() {  
			public boolean accept(File dir, String fileName) {  
				return fileName.endsWith(".jar");  
			}  
		});
		
		URL[] urls=new URL[jarFiles.length];
		for(int i=0;i<jarFiles.length;urls[i]=jarFiles[i].toURI().toURL(),i++);
		return new URLClassLoader(urls);
	}
	
	/**
	 * @param classFullName
	 * @param type
	 * @return
	 */
	public static boolean existClass(String classFullName,Class<?>... type){
		return null==loadType(classFullName,getCallerClassLoader(type))?false:true;
	}
	
	/**
	 * @param classFullName
	 * @param classLoader
	 * @return
	 */
	public static boolean existClass(String classFullName,ClassLoader classLoader){
		return null==loadType(classFullName,classLoader)?false:true;
	}
	
	/**
	 * @param classFullName
	 * @param type
	 * @return
	 */
	public static Class<?> loadType(String classFullName,Class<?>... type){
		try {
			return loadClass(classFullName,getCallerClassLoader(type));
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * @param classFullName
	 * @param classLoader
	 * @return
	 */
	public static Class<?> loadType(String classFullName,ClassLoader classLoader){
		try {
			return loadClass(classFullName,classLoader);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * @param classFullName
	 * @param type
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadClass(String classFullName,Class<?>... type) throws ClassNotFoundException{
		return loadClass(classFullName,getCallerClassLoader(type));
	}
	
	/**
	 * @param classFullName
	 * @param classLoader
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadClass(String classFullName,ClassLoader classLoader) throws ClassNotFoundException{
		if(null==classLoader) return null;
		return classLoader.loadClass(classFullName);
	}
	
	/**
	 * @param classFullName
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static Object instanceClass(String classFullName,Class<?>... type) throws Exception{
		return instanceClass(classFullName,Object.class,getCallerClassLoader(type));
	}
	
	/**
	 * @param classFullName
	 * @param returnType
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static <R> R instanceClass(String classFullName,Class<R> returnType,Class<?>... type) throws Exception{
		return instanceClass(classFullName,returnType,getCallerClassLoader(type));
	}
	
	/**
	 * @param classFullName
	 * @param returnType
	 * @param classLoader
	 * @return
	 * @throws Exception
	 */
	public static <R> R instanceClass(String classFullName,Class<R> returnType,ClassLoader classLoader) throws Exception{
		if(null==classLoader) return null;
		Class<?> type=Class.forName(classFullName, true, classLoader);
		return returnType.cast(type.newInstance());
	}
	
	/**
	 * @param absoluteFile
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public static Object getInstance(String absoluteFile,String className) throws Exception{
		return getInstance(absoluteFile,className,Object.class);
	}
	
	/**
	 * @param absoluteFile
	 * @param className
	 * @param returnType
	 * @return
	 * @throws Exception
	 */
	public static <R> R getInstance(String absoluteFile,String className,Class<R> returnType) throws Exception{
		Class<?> cla=getClass(absoluteFile,className);
		if(null==cla) return null;
		return returnType.cast(cla.newInstance());
	}
	
	/**
	 * @param absoluteFile
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public static Class<?> getClass(String absoluteFile,String className) throws Exception{
		if(null==absoluteFile||null==className||absoluteFile.trim().isEmpty()||className.trim().isEmpty()) return null;
		File file=new File(absoluteFile.trim());
		if(!file.exists()) return null;
		
		ClassLoader classLoader=null;
		if(file.isFile()){
			classLoader=getFileClassLoader(absoluteFile);
		}else{
			classLoader=getPathClassLoader(absoluteFile);
		}
		
		if(null==classLoader) return null;
		return classLoader.loadClass(className.trim());
	}
}
