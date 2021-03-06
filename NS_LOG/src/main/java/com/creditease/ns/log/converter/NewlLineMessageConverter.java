package com.creditease.ns.log.converter;


import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.creditease.ns.log.LogConstants;
import org.slf4j.MDC;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewlLineMessageConverter extends MessageConverter {
	private static String ip;
	private static String hostName;
	
	@Override
	public String convert(ILoggingEvent event) {
		String message = super.convert(event);
		if(message == null){
			return "";
		}
//		if(!event.getLoggerName().equals(SimpleLogger.class.getName()))
//		{
//			return message;
//		}

		boolean endWithEnter = message.endsWith(LogConstants.LINUX_EOL);
		StringTokenizer stringTokenizer = new StringTokenizer(message,"\r\n");
		int tokenCounts = stringTokenizer.countTokens();
//		String[] stringLines = message.split(LogConstants.WIN_LINUX_EOL_REG);


//		if(tokenCounts == 1){
//			return message;
//		}

		StringBuilder sb = new StringBuilder();

		String prefix = buildLogPrefix();
		
		
		
		int i = 1;
		while (stringTokenizer.hasMoreElements()) 
		{
			String stringLine = (String) stringTokenizer.nextElement();
			
			//long startTime = System.currentTimeMillis();
		
			//System.out.println("[构造日志前缀] [cost:"+(System.currentTimeMillis()-startTime)+"ms]");
			if(stringLine.endsWith(" "))
//				sb.append(LogConstants.PART_SPLIT).append(stringLine).append(prefix);
				sb.append("||").append(stringLine).append(prefix);
			else 
			{
//				sb.append(LogConstants.PART_SPLIT).append(stringLine).append(" ").append(prefix);
				sb.append("||").append(stringLine).append("||").append(prefix);
			}
			if(i < tokenCounts){
				sb.append(LogConstants.WIN_EOL);
			}else {
				if(endWithEnter){
					sb.append(LogConstants.WIN_EOL);
				}
			}

			i++;
		}
		
		return sb.toString();

	}

	private String buildLogPrefix()
	{
		//long startTime = System.currentTimeMillis();
		StringBuilder stringBuilder = new StringBuilder();
		String prefix = "";
		String tail = "";
		
		logContentStart(stringBuilder, LogConstants.MDC_KEY_TXNID, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_ORDERID, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_SYSTEMORDERID, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_BATCHID, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_TRADESTATUS, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_RESPONSECODE, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_RESPONSECODEMESS, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_MERCHANTID, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_CHANNELMERCHANTID, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_CHANNELNAME, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_PAYTYPE, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_TXNTYPE, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_UPDATETIME, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_REMARK, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_ERRORDESP, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_CHKSTATUS, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_OPERATORID, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_THIRDHEADCODE, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_THIRDHEADMSG, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_THIRDBODYCODE, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_THIRDBODYMSG, prefix, tail);
		//获取系统ip
		if (ip == null) 
		{
			try {
				Enumeration<NetworkInterface>  networkInterfaces = NetworkInterface.getNetworkInterfaces();
				boolean isFindIp = false;
				String s = "e\\w+0";
				Pattern p = Pattern.compile(s);
				while (networkInterfaces.hasMoreElements()) {  
					NetworkInterface item = networkInterfaces.nextElement(); 
					String devName = item.getDisplayName();
					if(devName == null)
					{
						continue;
					}
					Matcher m = p.matcher(devName);
					boolean isNeedDev = m.matches();
					
					if(!item.isLoopback() && isNeedDev)
					{
						for (InterfaceAddress address : item.getInterfaceAddresses()) {   
							if (address.getAddress() instanceof Inet4Address) {   
								Inet4Address inet4Address = (Inet4Address) address.getAddress();   
								if (!inet4Address.isLoopbackAddress() && !inet4Address.isMulticastAddress()) 
								{
									String ip = inet4Address.getHostAddress();
									this.ip = ip;
									stringBuilder.append("||");
									stringBuilder.append(prefix).append(ip).append(tail);
									isFindIp = true;
									if (hostName == null) 
									{
										hostName = inet4Address.getHostName();
									}
									break;
									
								}
							}   
						}   
					}
					
					if (isFindIp) 
					{
						break;
					}
	
				}
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				String ip = "unknownhost";
				stringBuilder.append("||");
				stringBuilder.append(prefix).append(ip).append(tail);
				return stringBuilder.toString();
			}
		}
		else 
		{
			stringBuilder.append("||");
			stringBuilder.append(prefix).append(ip).append(tail);
		}
		
		stringBuilder.append("||");
		stringBuilder.append(prefix).append(UUID.randomUUID()).append(tail);
		
//		//获取系统主机名
//		if (hostName == null) 
//		{
//			try {
//				Enumeration<NetworkInterface>  networkInterfaces = NetworkInterface.getNetworkInterfaces();
//				boolean isFindIp = false;
//				while (networkInterfaces.hasMoreElements()) {  
//					NetworkInterface item = networkInterfaces.nextElement(); 
//	
//					if(!item.isLoopback())
//					{
//						for (InterfaceAddress address : item.getInterfaceAddresses()) {   
//							if (address.getAddress() instanceof Inet4Address) {   
//								Inet4Address inet4Address = (Inet4Address) address.getAddress();   
//								if (!inet4Address.isLoopbackAddress() && !inet4Address.isMulticastAddress()) 
//								{
//									String hostname = inet4Address.getHostName();
//									hostName = hostname;
//									stringBuilder.append("||");
//									stringBuilder.append(prefix).append(hostName).append(tail);
//									isFindIp = true;
//									break;
//									
//								}
//							}   
//						}   
//					}
//					
//					if (isFindIp) 
//					{
//						break;
//					}
//	
//				}
//			} catch (SocketException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//				String hostName = "";
//				stringBuilder.append("||");
//				stringBuilder.append(prefix).append(hostName).append(tail);
//				return stringBuilder.toString();
//			}
//		}
		if(hostName != null) 
		{
			stringBuilder.append("||");
			stringBuilder.append(prefix).append(hostName).append(tail);
		}
		
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_CHANID, prefix, tail);
		//logFileName 暂时不获取
		stringBuilder.append("||");
		stringBuilder.append(prefix).append("").append(tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_STTLAMT, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_ACTUALSTTLAMT, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_SPLITFLG, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_PROCSTS, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_NOTIFYURL, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_OUTTXNID, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_EXTXNTD, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_CAPTURETIME, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_EXETIME, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_TRACETIME, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_RESERVETM, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_NSACCSTTLDT, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_NSSTTLDT, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_POSTINGSTS, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_CARDTP, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_DBTRISSRCD, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_DBTRIDTP, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_DBTRIDNUMBER, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_DBTRACCTID, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_DBTRNM, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_DBTRCONTACTNO, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_CDTRISSRCD, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_CDTRIDTP, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_CDTRIDNUMBER, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_CDTRACCTID, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_CDTRNM, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_CDTRCONTACTNO, prefix, tail);
		
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_CHANNLEMSG, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_NOTICEMSG, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_VERSIONNO, prefix, tail);
		
		
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_EXD4, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_EXD5, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_EXD6, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_EXD7, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_EXD8, prefix, tail);
		logContentAppend(stringBuilder, LogConstants.MDC_KEY_EXD9, prefix, tail);
//		

		return stringBuilder.toString();
	}

	private void logContentAppend(StringBuilder stringBuilder, String logKey, String prefix,
			String tail) {
		stringBuilder.append("||");
		String appendString = MDC.get(logKey); 
		stringBuilder.append(prefix).append((appendString == null ? "" : appendString)).append(tail);
	}

	
	private void logContentStart(StringBuilder stringBuilder, String logKey, String prefix,
			String tail) {
		String appendString = MDC.get(logKey); 
		stringBuilder.append(prefix).append((appendString == null ? "" : appendString)).append(tail);
	}

}
