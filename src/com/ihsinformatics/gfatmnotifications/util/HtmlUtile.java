package com.ihsinformatics.gfatmnotifications.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.ihsinformatics.gfatmnotifications.model.PatientScheduled;



 public class HtmlUtile {
  private static HtmlUtile instance = null;	
	 
  public HtmlUtile() {
  
  }	

  public static HtmlUtile getInstance(){
	  
	  if (instance==null)
		instance = new HtmlUtile();
	 return instance;	
  }
  //this method used for the static email content ... 
  public String  getMessageFormate(){
  
  	StringBuilder buf = new StringBuilder();
		buf.append("<html><body style='font-family: Arial, Helvetica, Monospace;'>");
			buf.append("<p>");
		    buf.append("Dear Sadeeqa,<br><br>");
		    buf.append("This is to inform you that warehouse database is not sync today. <br><br>" );
		    buf.append("Thanks");
		    buf.append("</p></body></html>");
		    
		String msg = buf.toString();
		
		return msg;	
  }
  
  //
  @SuppressWarnings("rawtypes")
  public String  getHtmltableFormate(LinkedHashMap<String, Integer>mapping ,String locationName){
  	
  	StringBuilder buf = new StringBuilder();
		buf.append("<html>" +
		           "<body>" +
		           "<table style='font-family: 'Trebuchet MS', Arial, Helvetica, sans-serif; border-spacing: 0px;  border-style:none; width: 100%;'>" +
		           "<caption style='padding: 8px; padding-top: 12px; padding-bottom: 12px;font-size: 150%;'><code>");
		 buf.append(locationName.toUpperCase());
		 buf.append("</code></caption>"+
		           "<tr>" +
		           "<th style = ' background-color: #110934;color: white;  border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left;'>Forms</th>" +
		           "<th style = ' background-color: #110934;color: white;  border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left;'>Count</th>" +
		           "</tr>");
		 for(Map.Entry m:mapping.entrySet()){  
			 buf.append("<tr><td style = 'border: 1px solid #ddd;padding: 8px;'>")
		       .append(m.getKey())
		       .append("</td><td style = 'border: 1px solid #ddd;padding: 8px;'>")
		       .append(m.getValue())
		       .append("</td></tr>");
		}
		buf.append("</table>" +
		           "</body>" +
		           "</html>");
		String html = buf.toString();
		
		return html;
  }
  
  // 
  public String getHtmlTableWithMultipleCol(ArrayList<PatientScheduled> mapping){
	 
	  StringBuilder buf = new StringBuilder();
		buf.append("<html>" +
		           "<body>" +
		           "<table style='font-family: 'Trebuchet MS', Arial, Helvetica, sans-serif; border-spacing: 0px;  border-style:none; width: 100%;'>" +
		           "<caption style='padding: 8px; padding-top: 12px; padding-bottom: 12px;font-size: 150%;'><code>");
		 buf.append(StringUtils.isBlank(missedFupConditions(mapping.get(0)))?mapping.get(0).getFacilityScheduled():mapping.get(0).getFacilityName());
		 buf.append("</code></caption>"+
		           "<tr>" +
		           "<th style = ' background-color: #110934;color: white;  border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left;'>Internal Patient ID</th>" +
		           "<th style = ' background-color: #110934;color: white;  border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left;'>PID</th>" +
		           "<th style = ' background-color: #110934;color: white;  border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left;'>Reason For Visit</th>" +
		           "<th style = ' background-color: #110934;color: white;  border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left;'>Facility Visit Date</th>" +  
				 "</tr>");
		 for (PatientScheduled patientScheduled : mapping) {
			 buf.append("<tr><td style = 'border: 1px solid #ddd;padding: 8px;'>")
		        .append(patientScheduled.getPatientId())
		        .append("</td><td style = 'border: 1px solid #ddd;padding: 8px;'>")
		        .append(patientScheduled.getPatientIdentifier())
		        .append("</td><td style = 'border: 1px solid #ddd;padding: 8px;'>")
		        .append(StringUtils.isBlank(missedFupConditions(patientScheduled))?(StringUtils.isBlank(patientScheduled.getTestType())? patientScheduled.getReasonForCall():patientScheduled.getTestType()):missedFupConditions(patientScheduled))
		        .append("</td><td style = 'border: 1px solid #ddd;padding: 8px;'>")
		        .append(StringUtils.isBlank(missedFupConditions(patientScheduled))?(StringUtils.isBlank(patientScheduled.getRaFacilityVisitDate())? patientScheduled.getFupFacilityVisitDate() : patientScheduled.getRaFacilityVisitDate()):getMisedFupReturnVisitDate(patientScheduled))
		        .append("</td></tr>");	
		  }		 
		buf.append("</table>" +
		           "</body>" +
		           "</html>");
		String html = buf.toString();
		
		return html;
  
  }

  //execute the Set collection 
  
  public String getHtmlTableWithSet(Set<String> locationName){
	  
      int index =0;
	  StringBuilder buf = new StringBuilder();
		buf.append("<html>" +
		           "<body>" +
		           "<table style='font-family: 'Trebuchet MS', Arial, Helvetica, sans-serif; border-spacing: 0px;  border-style:none; width: 100%;'>" +
		           "<caption style='padding: 8px; padding-top: 12px; padding-bottom: 12px;font-size: 150%;'><code>");
		 buf.append("LIST OF LOCATION");
		 buf.append("</code></caption>"+
		           "<tr>" +
		           "<th style = ' background-color: #110934;color: white;  border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left;'>Sr.No</th>" +
		           "<th style = ' background-color: #110934;color: white;  border: 1px solid #ddd; padding: 8px; padding-top: 12px; padding-bottom: 12px; text-align: left;'>Location Name</th>" +
				 "</tr>");
		 Iterator<String> iterator = locationName.iterator();
		    while(iterator.hasNext()) {
		        String setElement = iterator.next();
		        buf.append("<tr><td style = 'border: 1px solid #ddd;padding: 8px;'>")
		        .append(index++)
		        .append("</td><td style = 'border: 1px solid #ddd;padding: 8px;'>")
		        .append(setElement)
		        .append("</td></tr>");	
		    }	 
		buf.append("</table>" +
		           "</body>" +
		           "</html>");
		String html = buf.toString();
		
		return html;

  }
  
  public String missedFupConditions(PatientScheduled patientRecords){
	  
	  if(patientRecords.getcReturnVisitDate() != null)
	        return "Childhood TB-Missed Visit Followup";
	   else if(patientRecords.getpReturnVisitDate() !=null )
           return "PET-Missed Visit Followup";
      else if(patientRecords.getfReturnVisitDate() !=null)			
	         return "FAST-Missed Visit Followup";
		else
           return null;		
	 } 
 public String getMisedFupReturnVisitDate(PatientScheduled patientRecords){
	  
	  if(patientRecords.getcReturnVisitDate() != null)
	        return patientRecords.getcReturnVisitDate();
	   else if(patientRecords.getpReturnVisitDate() !=null )
           return patientRecords.getpReturnVisitDate();
      else if(patientRecords.getfReturnVisitDate() !=null)			
	         return  patientRecords.getfReturnVisitDate();
		else
           return null;		
	 } 
}
