import java.sql.*;
import java.io.*;


public class test
{
	public static void main(String[] args)throws Exception
	{
		System.out.println("hello!");
		Class.forName("org.sqlite.JDBC");
		
		Connection c=DriverManager.getConnection("jdbc:sqlite:DBforCourse.db3");
		System.out.println("DB open successfully!");
			
		Statement stat=c.createStatement();
		stat.executeUpdate("CREATE TABLE Apply(sID int,cName varchar(255),major varchar(255),decision varchar(255))");
		stat.executeUpdate("CREATE TABLE College(cName varchar(255),state varchar(255),enrollment int)");
		stat.executeUpdate("CREATE TABLE Student(sID int,sName varchar(255),GPA decimal(10,5),sizeHS int)");
		
		File file=new File("dbapply.txt");
		BufferedReader reader=new BufferedReader(new FileReader(file));
		String temp=null;
		while((temp=reader.readLine())!=null)
		{
			String[] s=temp.split(",");
			temp="INSERT INTO Apply VALUES ("+s[0]+",'"+s[1]+"','"+s[2]+"','"+s[3]+"')";
			stat.executeUpdate(temp);
		}
		file=new File("dbcollege.txt");
		reader=new BufferedReader(new FileReader(file));
		temp=null;
		while((temp=reader.readLine())!=null)
		{
			String[] s=temp.split(",");
			temp="INSERT INTO College VALUES ('"+s[0]+"','"+s[1]+"',"+s[2]+")";
			stat.executeUpdate(temp);
		}
		file=new File("dbstudent.txt");
		reader=new BufferedReader(new FileReader(file));
		temp=null;
		while((temp=reader.readLine())!=null)
		{
			String[] s=temp.split(",");
			temp="INSERT INTO Student VALUES ("+s[0]+",'"+s[1]+"',"+s[2]+","+s[3]+")";
			stat.executeUpdate(temp);
		}
		
		ResultSet r;
		r=stat.executeQuery("SELECT * FROM College");
		System.out.println("#####Table College#####");
		while(r.next())
			System.out.println(r.getString("cName")+" "+r.getString("state")+" "+r.getString("enrollment"));
		r=stat.executeQuery("SELECT * FROM Student");
		System.out.println("#####Table Student#####");
		while(r.next())
			System.out.println(r.getString("sID")+" "+r.getString("sName")+" "+r.getString("GPA")+" "+r.getString("sizeHS"));
		r=stat.executeQuery("SELECT * FROM Apply");
		System.out.println("#####Table Apply#####");
		while(r.next())
			System.out.println(r.getString("sID")+" "+r.getString("cName")+" "+r.getString("major")+" "+r.getString("decision"));
		
		r=stat.executeQuery("SELECT * FROM Student WHERE GPA>3.6");
		System.out.println("#####GPA>3.6#####");
		while(r.next())
			System.out.println(r.getString("sID")+" "+r.getString("sName")+" "+r.getString("GPA"));
			
		r=stat.executeQuery("SELECT DISTINCT sName,cName FROM Student,Apply WHERE Student.sID=Apply.sID");
		System.out.println("#####Pair of sName and cName#####");
		while(r.next())
			System.out.println(r.getString("sName")+" "+r.getString("cName"));
			
		r=stat.executeQuery("SELECT sName,GPA,decision FROM College,Student,Apply WHERE Student.sID=Apply.sID and College.cName=Apply.cName and Student.sizeHS<1000 and Apply.cName='Stanford' and Apply.major='CS'");
		System.out.println("#####1000,Stanford,CS#####");
		while(r.next())
			System.out.println(r.getString("sName")+" "+r.getString("GPA")+" "+r.getString("decision"));
		
		r=stat.executeQuery("SELECT * FROM Apply JOIN Student Join College ON Student.sID=Apply.sID and College.cName=Apply.cName ORDER BY GPA DESC,enrollment");
		System.out.println("#####All Apply Info#####");
		while(r.next())
			System.out.println(r.getString("major")+" "+r.getString("decision")+" "+r.getString("sName")+" "+r.getString("GPA")+" "+r.getString("sizeHS")+" "+r.getString("state")+" "+r.getString("enrollment"));
		
		r=stat.executeQuery("SELECT sID,sName,GPA,sizeHS,GPA*(sizeHS/1000.0) as newGPA FROM Student");
		System.out.println("#####New GPA#####");
		while(r.next())
			System.out.println(r.getString("sID")+" "+r.getString("sName")+" "+r.getString("GPA")+" "+r.getString("sizeHS")+" "+r.getString("newGPA"));
			
		r=stat.executeQuery("SELECT cName FROM College C1 WHERE not exists (SELECT cName FROM College C2 WHERE C1.enrollment<C2.enrollment)");
		System.out.println("#####Expensive College#####");
		while(r.next())
			System.out.println(r.getString("cName"));
		
		r=stat.executeQuery("SELECT * FROM Apply JOIN Student Join College ON Student.sID=Apply.sID and College.cName=Apply.cName");
		System.out.println("#####All Joined Info#####");
		while(r.next())
			System.out.println(r.getString("major")+" "+r.getString("decision")+" "+r.getString("sName")+" "+r.getString("GPA")+" "+r.getString("sizeHS")+" "+r.getString("state")+" "+r.getString("enrollment"));
		
		r=stat.executeQuery("SELECT cName FROM Apply GROUP BY cName HAVING count(*)<5");
		System.out.println("#####Apply<5 College#####");
		while(r.next())
			System.out.println(r.getString("cName"));
	}
}

//javac test.java
//java -classpath ".;sqlite-jdbc-3.7.2.jar" test