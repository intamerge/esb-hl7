/**
 * Copyright (c) 2016-2017 Intamerge http://www.intamerge.com
 * All Rights Reserved.
 *
 * This source code is licensed under AGPLv3 and allows you to freely download and use this source:  Try it free !
 * This license does not extend to source code in other Intamerge source code projects, please refer to those projects for their specific licensing.
 */

package scripts.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;


/**
 * Generates a BuildInfo.java class
 * 
 * 
 *
 */
public class GenerateBuildVersionClass {		

	/**
	 * @param args
	 * args[0]=${project_loc} eg /home/user/git/Connectic/Connectic.Master
	 * args[1]= ${git_work_tree} eg /home/user/git/Connectic
	 * args[2] = build revision number eg 1234
	 * args[3] = unique classname prefix eg "EsbTcp"
	 */
	public static void main(String[] args) {
		
		GenerateBuildVersionClass gen = new GenerateBuildVersionClass();
		
		System.out.println("argc="+args.length+" "+Arrays.toString(args));
		
		if(
				args.length<5 || 
				0==args[0].length() ||
				0==args[1].length() || 
				!(new File(args[0]).isDirectory()) ||
				!(new File(args[1]).isDirectory())
				){
			System.err.println(
					"args[0] must be ${project_loc} eg /home/user/git/intamerge/esb-xyz\n"+
					"args[1] must be ${git_work_tree} eg /home/user/git/intamerge\n"+
					"args[2] must be revision eg 1234\n"+
					"args[3] must be prefix eg EsbTcp\n"+
					"args[4] must be productName eg Intamerge\n"
					);
			System.exit(-1);
		}
		String revisionNumber=null;
		if(args.length>2){
			revisionNumber=args[2];
		}
		String prefix=null;
		if(args.length>3){
			prefix=args[3];
		}
		String productName=null;
		if(args.length>4){
			productName=args[4];
		}	
		System.out.println(
				"${project_loc}="+args[0]+"\n"+
				"${git_work_tree}="+args[1]+"\n"+
				((null!=revisionNumber)?revisionNumber:"")+"\n"+
				((null!=prefix)?prefix:"")+"\n"+
				((null!=productName)?productName:"")+"\n"
				);
		
		gen.build(args[0], args[1], revisionNumber, prefix, productName);
	}
	
	/**
	 * 
	 * @param project_loc
	 * @param git_work_tree
	 * @param revisionNumber optional - set to null if not known
	 */
	private void build(String project_loc, String git_work_tree, String revisionNumber, String prefix, String productName){
		
		prefix = prefix.replaceAll("'", "");

		String versionFilePath = project_loc + "/" + "version.property";
		String versionPropertyName = "version.property";
		String timeStamp = "";
		String version;
		String name;
		String nameFilePath = project_loc + "/" + "name.property";
		String namePropertyName = "name.property";
		
		if(null==revisionNumber){
			revisionNumber = ""+GetBuildRevision.getRevision(git_work_tree);
		}
			
		System.out.println("Revision Number = " +revisionNumber);
			
		// Get the version from version.property
		Properties p = new Properties();
    	try{
    		System.out.println("Get version from "+versionFilePath);
    		InputStream istream = new FileInputStream(versionFilePath);
    		p.load(istream);
        	version = p.getProperty(versionPropertyName);
    	}catch(Exception e){
    		e.printStackTrace();
    		version = "NULL";
    	}
    	System.out.println("version="+version);
    	

    	System.out.println("productName="+productName);
    	
		// Get the name from version.property
		p = new Properties();
    	try{
    		System.out.println("Get name from "+nameFilePath);
    		InputStream istream = new FileInputStream(nameFilePath);
    		p.load(istream);
        	name = p.getProperty(namePropertyName);
    	}catch(Exception e){
    		e.printStackTrace();
    		name = "NULL";
    	}
    	System.out.println("name="+name);
    	
    	// Generate a timestamp
    	// Fri, 25-January-2013 12:26:56 GMT
    	// http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
    	Date date = new Date();
    	DateFormat df = new SimpleDateFormat("E, dd-MMMM-yyyy HH:mm:ss z");  
    	
    	@SuppressWarnings("deprecation")
		String year = ""+(date.getYear()+1900);
    	   
    	timeStamp = df.format(date);  
    	System.out.println("Timestamp: "+timeStamp);
    	
    	
    	// Code generation: 
    	// http://stackoverflow.com/questions/121324/a-java-api-to-generate-java-source-files
    	// http://namanmehta.blogspot.co.uk/2010/01/use-codemodel-to-generate-java-source.html
    	// javadoc http://www.jarvana.com/jarvana/view/com/sun/codemodel/codemodel/2.3/codemodel-2.3-javadoc.jar!/index.html?com/sun/codemodel/JCodeModel.html
    	
    	genBackendClass(project_loc + "/src/main/java", "net.esb.build."+prefix+"BuildInfo", revisionNumber, productName, timeStamp, version, name, year);

		System.out.println("DONE");
		System.exit(0);
	}

	void genBackendClass(String path, String className, String revisionNumber, String productName, String timeStamp, String version, String name, String year){
    	// Generate the BuildInfo.java class
    	JCodeModel cm = new JCodeModel();
    	
		try {
			JDefinedClass dc = cm._class(className);
			
			dc.javadoc().add("Generated by "+this.getClass().getCanonicalName());
			
			JFieldVar revisionNumberVar = dc.field(JMod.PRIVATE + JMod.FINAL, java.lang.String.class, "revisionNumber");
			revisionNumberVar.init(JExpr.lit(""+revisionNumber));

			JFieldVar productNameVar = dc.field(JMod.PRIVATE + JMod.FINAL, java.lang.String.class, "productName");
			productNameVar.init(JExpr.lit(""+productName));
			
			// eg Fri, 25-January-2013 12:26:56 GMT
			JFieldVar timeStampVar = dc.field(JMod.PRIVATE + JMod.FINAL, java.lang.String.class, "timeStamp");
			timeStampVar.init(JExpr.lit(timeStamp));

			JFieldVar yearVar = dc.field(JMod.PRIVATE + JMod.FINAL, java.lang.String.class, "year");
			yearVar.init(JExpr.lit(year));
			
			JFieldVar versionVar = dc.field(JMod.PRIVATE + JMod.FINAL, java.lang.String.class, "version");
			versionVar.init(JExpr.lit(version));
			
			JFieldVar nameVar = dc.field(JMod.PRIVATE + JMod.FINAL, java.lang.String.class, "name");
			nameVar.init(JExpr.lit(name));
			
			JFieldVar buildInfo = dc.field(JMod.PRIVATE + JMod.STATIC + JMod.FINAL, dc, "buildInfo");
			buildInfo.init(JExpr._new(dc));
			
			//JMethod mc = dc.constructor(JMod.PRIVATE);
			
			JMethod mb = dc.method(JMod.PUBLIC + JMod.STATIC, dc, "buildInfo");
	    	mb.body()._return(buildInfo);
    	
	    	JMethod m1 = dc.method(JMod.PUBLIC, String.class, "getRevisionNumber");
	    	m1.body()._return(revisionNumberVar);
	    	
	    	JMethod m2 = dc.method(JMod.PUBLIC, String.class, "getTimestamp");
	    	m2.body()._return(timeStampVar);
	    	
	    	JMethod m3 = dc.method(JMod.PUBLIC, String.class, "getVersion");
	    	m3.body()._return(versionVar);
	    	
	    	JMethod m4 = dc.method(JMod.PUBLIC, String.class, "getName");
	    	m4.body()._return(nameVar);
	    	
	    	JMethod m5 = dc.method(JMod.PUBLIC, String.class, "getProductName");
	    	m5.body()._return(productNameVar);

	    	JMethod m6 = dc.method(JMod.PUBLIC, String.class, "getYear");
	    	m6.body()._return(yearVar);

	    	File file = new File(path);
	    	System.out.println("building file "+file.getAbsolutePath());
	    	file.mkdirs();
	    	cm.build(file);
		} catch (JClassAlreadyExistsException | IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	
	}
	void genFrontendClass(String path, String className, String revisionNumber, String productName, String timeStamp, String version, String name, String year){
    	// Generate the BuildInfo.java class
    	JCodeModel cm = new JCodeModel();
    	
		try {
			JDefinedClass dc = cm._class(className);
			dc._implements(Serializable.class);
			
			dc.javadoc().add("DO NOT EDIT: Generated by "+this.getClass().getCanonicalName());
			
			JFieldVar serialVar = dc.field(JMod.PRIVATE+JMod.STATIC+JMod.FINAL, cm.LONG, "serialVersionUID");
			serialVar.init(JExpr.lit(1L));
			
			JFieldVar revisionNumberVar = dc.field(JMod.PRIVATE, java.lang.String.class, "revisionNumber");
			revisionNumberVar.init(JExpr.lit(""+revisionNumber));
			
			JFieldVar productNameVar = dc.field(JMod.PRIVATE + JMod.FINAL, java.lang.String.class, "productName");
			productNameVar.init(JExpr.lit(""+productName));
			
			// eg Fri, 25-January-2013 12:26:56 GMT
			JFieldVar timeStampVar = dc.field(JMod.PRIVATE , java.lang.String.class, "timeStamp");
			timeStampVar.init(JExpr.lit(timeStamp));
			
			JFieldVar versionVar = dc.field(JMod.PRIVATE, java.lang.String.class, "version");
			versionVar.init(JExpr.lit(version));
			
			JFieldVar nameVar = dc.field(JMod.PRIVATE, java.lang.String.class, "name");
			nameVar.init(JExpr.lit(name));
			
			JFieldVar yearVar = dc.field(JMod.PRIVATE + JMod.FINAL, java.lang.String.class, "year");
			yearVar.init(JExpr.lit(year));		
			
	    	JMethod m1 = dc.method(JMod.PUBLIC, String.class, "getRevisionNumber");
	    	m1.body()._return(revisionNumberVar);
	    	
	    	JMethod m6 = dc.method(JMod.PUBLIC, cm.VOID, "setRevisionNumber");
	    	m6.param(String.class, "revisionNumber");
	    	JBlock jBlock6 = m6.body();          
            jBlock6.directStatement("this.revisionNumber = revisionNumber;");   
	    	
	    	JMethod m2 = dc.method(JMod.PUBLIC, String.class, "getTimeStamp");
	    	m2.body()._return(timeStampVar);
	    	
	    	JMethod m7 = dc.method(JMod.PUBLIC, cm.VOID, "setTimeStamp");
	    	m7.param(String.class, "timeStamp");
	    	JBlock jBlock7 = m7.body();          
            jBlock7.directStatement("this.timeStamp = timeStamp;");  
	    	
	    	JMethod m3 = dc.method(JMod.PUBLIC, String.class, "getVersion");
	    	m3.body()._return(versionVar);
	    	
	    	JMethod m8 = dc.method(JMod.PUBLIC, cm.VOID, "setVersion");
	    	m8.param(String.class, "version");
	    	JBlock jBlock8 = m8.body();          
            jBlock8.directStatement("this.version = version;");     	
	    	
	    	JMethod m4 = dc.method(JMod.PUBLIC, String.class, "getName");
	    	m4.body()._return(nameVar);
	    	
	    	JMethod m9 = dc.method(JMod.PUBLIC, cm.VOID, "setName");
	    	m9.param(String.class, "name");
	    	JBlock jBlock9 = m9.body();          
            jBlock9.directStatement("this.name = name;");     	
	    	
	    	JMethod m5 = dc.method(JMod.PUBLIC, cm.VOID, "clear");
	    	JBlock jBlock = m5.body();
	    	jBlock.directStatement("revisionNumber=\"?\";timeStamp=\"?\";version=\"?\";name=\"?\";");
	    	
	    	JMethod m10 = dc.method(JMod.PUBLIC, String.class, "getProductName");
	    	m10.body()._return(productNameVar);
	    	
	    	JMethod m11 = dc.method(JMod.PUBLIC, cm.VOID, "setProductName");
	    	m11.param(String.class, "productName");
	    	JBlock jBlock10 = m11.body();          
            jBlock10.directStatement("this.productName = productName;");   
            
	    	JMethod m12 = dc.method(JMod.PUBLIC, String.class, "getYear");
	    	m12.body()._return(yearVar);

	    	File file = new File(path);
	    	file.mkdirs();
	    	cm.build(file);
		} catch (JClassAlreadyExistsException | IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	
	}	
}
