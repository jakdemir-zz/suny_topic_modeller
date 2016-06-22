package edu.albany.cs.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import edu.albany.cs.spellchecker.LanguageToolSP;
import edu.albany.cs.spellchecker.SpellCheckerUtil;
import edu.albany.cs.stemmer.KStemmer;

public class FileGeneratorFromMysql {

	public static HashMap<String, String> writeInFolders(String hostname,
			String schema, String username, String password, String views,
			String notes) {

		HashMap<String, String> corpus = new HashMap<String, String>();

		Connection con = null;
		int count = 0;
		String rawContentFolder = "raw_content";
		String rawContentFolderSP = "raw_content_sp";
		// SpellCheckerUtil sc = new SpellCheckerUtil("english");
		SpellCheckerUtil sc = new SpellCheckerUtil();
		try {
			// jdbc:mysql://localhost:3306/builder_ikit_org_J_ICS_2012_2013
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + hostname
					+ ":3306/" + schema, username, password);
			try {
				// SELECT vt.idview as viewid,vt.title as
				// viewtitle,nt.noteid,nt.notetitle,nt.notecontent FROM
				// note_table as nt
				// inner join view_note as vn on nt.noteid = vn.noteid
				// inner join view_table as vt on vt.idview=vn.viewid limit
				// 1000;

				String sql = "SELECT vt.idview as viewid,vt.title as viewtitle,nt.noteid,nt.notetitle,nt.notecontent FROM "+ConfigManager.getInstance().getNoteTable()+" as nt inner join view_note as vn on nt.noteid = vn.noteid inner join view_table as vt on vt.idview=vn.viewid";
				// "SELECT nt.notecontent FROM itm.note_table as nt left join builder_ikit_org_taccl_ualbany.view_note as vn on nt.noteid=vn.noteid left join builder_ikit_org_taccl_ualbany.view_table as vt on vt.idview=vn.viewid where vt.idview is not null";
				if (!views.equals("") || !notes.equals("")) {
					boolean isChanged = false;
					sql = sql + " WHERE ";
					if (!views.equals("")) {
						sql = sql + " vn.viewid in (" + views + ") ";
						isChanged = true;
					}
					if (!notes.equals("")) {
						if (isChanged) {
							sql = sql + " and ";
						}
						sql = sql + "nt.noteid in (" + notes + ") ";
					}
				}
				System.out.println(sql);

				PreparedStatement prest = con.prepareStatement(sql);
				ResultSet rs = prest.executeQuery();
				while (rs.next()) {
					// String content = rs.getString(1);
					String foldername = rs.getString(1);
					String viewTitle = rs.getString(2);
					String filename = rs.getString(3);
					String noteTitle = rs.getString(4);
					String noteContent = rs.getString(5);

					String content = noteTitle + " " + noteContent;

					// createDirectory(foldername,rawContentFolder);
					// createFile(foldername + "/" + filename,
					// content,rawContentFolder);

					// Lets do the same with some spellchecking:
					// createDirectory(foldername,rawContentFolder);
					// createFile(foldername + "/" + filename,
					// content,rawContentFolder);

					//removing since spellchecking is moved to spellchecked table
					//String contentSP = sc.correctFull(content);
					// contentSP = PorterStemmer.stemWords(contentSP);
					
					String contentSP = KStemmer.stemWords(content);
					// String contentSP = KStemmer.stemWords(content);

					createDirectory(foldername, rawContentFolderSP);
					createFile(foldername + "/" + filename, contentSP,
							rawContentFolderSP);

					count++;
					// System.out.println("Content : " + content);
					// System.out.println("ContentSP : " + contentSP);

					corpus.put("" + count, contentSP);
					// corpus.put(""+count, noteContent);

				}
				System.out.println("Number of Documents: " + count);
				prest.close();
				con.close();

			} catch (SQLException s) {
				System.out.println("SQL statement is not executed!"
						+ s.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return corpus;
	}

	private static void createDirectory(String directory, String parent) {

		String directoryname = "/tmp/" + parent + "/" + directory;

		File file = new File(directoryname);

		// Create file if it does not exizt
		boolean success = file.mkdirs();
		if (success) {
			// System.out.println("Directory is Created");
		} else {
			// System.out.println("Directory is NOT CREATED");
		}

	}

	//XXX Use File manager
	private static void createFile(String name, String content,
			String folderName) {
		String filename = "/tmp/" + folderName + "/" + name;
		try {
			File file = new File(filename);

			// Create file if it does not exist
			boolean success = file.createNewFile();
			if (success) {
				// File did not exist and was created
			} else {
				// File already exists
			}

			FileWriter fstream = new FileWriter(filename);
			PrintWriter pw = new PrintWriter(fstream);
			pw.println(content);
			pw.close();

		} catch (IOException e) {
			System.out.println("Couldn't create file : " + filename);
		}

	}

	private static void writeAllNotesTogether() {

		Connection con = null;
		int count = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager
					.getConnection(
							"jdbc:mysql://localhost:3306/builder_ikit_org_J_ICS_2012_2013",
							// "jdbc:mysql://localhost:3306/builder_ikit_org_taccl_ualbany",
							"root", "");
			try {

				String sql = "SELECT nt.notecontent FROM note_table_sp as nt left join itm.view_note as vn on nt.noteid=vn.noteid left join builder_ikit_org_taccl_ualbany.view_table as vt on vt.idview=vn.viewid where vt.idview is not null ";

				PreparedStatement prest = con.prepareStatement(sql);
				ResultSet rs = prest.executeQuery();
				while (rs.next()) {
					String content = rs.getString(1);
					createFile("" + count, content, "raw_content");
					count++;
					System.out.println("Content : " + content);

				}
				System.out.println("Number of records: " + count);
				prest.close();
				con.close();
			} catch (SQLException s) {
				System.out.println("SQL statement is not executed!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createSPNoteTable(String hostname, String schema,
			String username, String password) throws ClassNotFoundException,
			SQLException {

		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://" + hostname
				+ ":3306/" + schema, username, password);
		String createTable = "CREATE TABLE `note_table_sp` (`noteid` int(11) NOT NULL, `notetitle` varchar(200) DEFAULT NULL, `notecontent` mediumtext, `createtime` datetime DEFAULT NULL,PRIMARY KEY (`noteid`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		PreparedStatement prest = con.prepareStatement(createTable);
		prest.executeUpdate();
		prest.close();
		con.close();
	}

	public static void dropSPNoteTable(String hostname, String schema,
			String username, String password) throws ClassNotFoundException,
			SQLException {

		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://" + hostname
				+ ":3306/" + schema, username, password);
		String createTable = "DROP TABLE `note_table_sp` ;";
		PreparedStatement prest = con.prepareStatement(createTable);
		prest.executeUpdate();
		prest.close();
		con.close();
	}
	
	// public static void main(String[] args) {
	// // writeAllNotesTogether();
	//
	// // writeInFolders("localhost", "builder_ikit_org_J_ICS_2012_2013",
	// // "root", "");
	// writeInFolders("localhost", "builder_ikit_org_J_ICS_2012_2013", "root",
	// "", "", "");
	//
	// }

	public static void spellCheckingNoteTable(String hostname, String schema,
			String username, String password) throws Exception{
		
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://" + hostname + ":3306/" + schema, username, password);
		String selectQuery = "SELECT noteid,notetitle,notecontent,createtime FROM "+schema+".note_table where noteid";
		//Ya spellchecker patlarsa kaldigi yerden devam.
		//String selectQuery = "SELECT noteid,notetitle,notecontent,offset,createtime FROM "+schema+".note_table where noteid > 12943;";
		
		PreparedStatement prest = con.prepareStatement(selectQuery);

		ResultSet rs = prest.executeQuery();
		SpellCheckerUtil sc = new SpellCheckerUtil();

		while (rs.next()) {

			String noteid = rs.getString(1);
			String notetitle = rs.getString(2);
			String notecontent = rs.getString(3);
			String createtime = rs.getString(4);

			
			String notetitleSP = LanguageToolSP.spellCheck(notetitle);
			String notecontentSP = LanguageToolSP.spellCheck(notecontent);
			
			//Google sicti
			//String notetitleSP = sc.correctFull(notetitle);
			//String notecontentSP = sc.correctFull(notecontent);

			//sikerler
			//String notetitleSP = notetitle;
			//String notecontentSP = notecontent;
			
			notecontentSP = notecontentSP.replaceAll("'", "\\\\'");
			notetitleSP = notetitleSP.replaceAll("'", "\\\\'");
			String insertQuery = "insert into "+schema+".note_table_sp (noteid,notetitle,notecontent,createtime ) VALUES  ("+noteid+",'"+notetitleSP+"','"+notecontentSP+"','"+createtime+"')" ;
			System.out.println("sql:"+insertQuery);
			PreparedStatement insertps = con.prepareStatement(insertQuery);
			insertps.executeUpdate();
			
		}
			
	}

	public static void main(String[] args) {
		String hostname = ConfigManager.getInstance().getMysqlHostname();
		String schema = ConfigManager.getInstance().getSchema();
		String username = ConfigManager.getInstance().getUsername();
		String password= ConfigManager.getInstance().getPassword();
		
		try {
			FileGeneratorFromMysql.dropSPNoteTable(hostname, schema, username, password);
			FileGeneratorFromMysql.createSPNoteTable(hostname, schema, username, password);
			FileGeneratorFromMysql.spellCheckingNoteTable(hostname, schema, username, password);
			
		} catch (Exception e) {
			System.out.println("Sicti " + e);
		}
	}

}
