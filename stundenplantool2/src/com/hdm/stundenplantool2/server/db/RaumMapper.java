package com.hdm.stundenplantool2.server.db;

import java.sql.*;
import java.util.Vector;

import com.hdm.stundenplantool2.shared.bo.*;

/**
 * Mapper-Klasse, die <code>Raum</code>-Objekte auf eine relationale
 * Datenbank abbildet. Hierzu wird eine Reihe von Methoden zur Verfügung
 * gestellt, mit deren Hilfe z.B. Objekte gesucht, erzeugt, modifiziert und
 * gelöscht werden können. Das Mapping ist bidirektional. D.h., Objekte können
 * in DB-Strukturen und DB-Strukturen in Objekte umgewandelt werden.
 * 
 * @see DozentMapper, LehrveranstaltungMapper, BelegungMapper, SemesterverbandMapper, StudiengangMapper, ZeitslotMapper
 * @author Thies (implement: Zimmermann, Klatt, Roth)
 */
public class RaumMapper {

	private static RaumMapper raumMapper = null;
	
	protected RaumMapper(){
		
	}
	
	public static RaumMapper raumMapper() {
	    if (raumMapper == null) {
	      raumMapper = new RaumMapper();
	    }

	    return raumMapper;
	   }
	
	/**
	 * Methode um einen Raum in der DB zu aktualisieren
	 * 
	 * @param	Raum-Objekt welches aktualisiert werden soll 			
	 * @return	Raum-Objekt
	 */
	public Raum update(Raum raum) throws RuntimeException {
		Connection con = DBConnection.connection();
		
		// Aktualisierung der Raum-Entität in der DB		
		try{
			Statement stmt = con.createStatement();
			
			// Ausführung des SQL-Statements
			String sql = "UPDATE Raum SET Kapazitaet='"+raum.getKapazitaet()+"', Bezeichnung='"+raum.getBezeichnung()+"' WHERE ID="+raum.getId()+";";
			stmt.executeUpdate(sql);
		}			
		
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem");	
		}
		
		return raum;
	}

	/**
	 * Methode um einen Raum in der DB zu aktualisieren
	 * 
	 * @param	Raum-Objekt welches aktualisiert werden soll 			
	 * @return	Raum-Objekt
	 */
	public void delete(Raum raum) throws RuntimeException {
			
		Connection con = DBConnection.connection();
		try {
			Statement stmt = con.createStatement();
			
			// Ausführung des SQL-Statements
			String sql = "DELETE FROM Raum WHERE ID = '"+raum.getId()+"';";
			stmt.executeUpdate(sql);
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem");
		}
	}
	
	/**
	 * Methode um einen neuen Raum in die DB zu schreiben
	 * 
	 * @param	Raum-Objekt welcher neu hinzukommt			
	 * @return	Raum-Objekt
	 */
	public Raum insertIntoDB (Raum raum) throws RuntimeException {
		
		Connection con = DBConnection.connection();
		ResultSet rs;
		
		try{
			// Ausführen des SQL-Statements
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO Raum (`ID`, `Kapazitaet`, `Bezeichnung`) VALUES ('" + raum.getId() + "', '" + raum.getKapazitaet() + "', '"+raum.getBezeichnung()+"');";
			stmt.executeUpdate(sql);
			
			/*
			 *  Auslesen der nach einfügen eines neuen Dozenten in DB entstandenen "größten" ID
			 *  @author: Herr Prof. Thies 
			 *  @implement: Lucas Zanella 
			 */
			sql = "SELECT MAX(ID) AS maxid FROM Raum;";
			rs = stmt.executeQuery(sql);
			
			/*
			 *  Setzen der ID dem hier aktuellen Semesterverband-Objekt
			 *  @author: Herr Prof. Thies
			 *  @implement: Lucas Zanella 
			 */
			while(rs.next()){
				raum.setId(rs.getInt("maxid"));
			}		
		}		
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem");
		}
		
		return raum;
		
	}
	
	/**
	 * Methode um eine beliebige Anzahl an Räumen anhand Ihrerer ID's aus der
	 * DB auszulesen
	 * 
	 * @param	id Primärschlüsselattribut(e) (->DB)
	 * @return	Vector mit Räumen, die den Primärschlüsselattributen entsprechen
	 */
	public Vector<Raum> findByKey(Vector<Integer> keys) throws RuntimeException {
		StringBuffer ids = new StringBuffer();
		
		//Erstellung des dynamischen Teils des SQL-Querys		
		if (keys.size() > 1) {
			for (int i = 0; i < keys.size()-1; i++) {
				ids.append(keys.elementAt(i));	
				ids.append(",");
			}
		}
			
		ids.append(keys.elementAt(keys.size()-1));	
		
		//Einholen einer DB-Verbindung		
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Raum> raeume = new Vector<Raum>();
		
		try{
			// Ausführen des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Raum WHERE ID IN (" + ids.toString() + ")";
			rs = stmt.executeQuery(sql);
			
			// Befüllen des "Raum-Vectors"
			while(rs.next()){
	            Raum raum = new Raum();
	            raum.setId(rs.getInt("ID"));
	            raum.setKapazitaet(rs.getInt("Kapazitaet"));
	            raum.setBezeichnung(rs.getString("Bezeichnung"));
	            raeume.add(raum);  
	          }
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem");				
		}
		return raeume;
	}
	
	/**
	 * Methode um alle Räume aus der DB auszulesen
	 * 
	 * @return	Vector mit Räume
	 */	
	public Vector<Raum> findAll() throws RuntimeException {
		
		//Einholen einer DB-Verbindung				
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Raum> raeume = new Vector<Raum>();
		
		try{
			// Ausführen des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Raum";
			rs = stmt.executeQuery(sql);
			
			// Erstellung des "Raum-Vectors"
			while(rs.next()){
	            Raum raum = new Raum();
	            raum.setId(rs.getInt("ID"));
	            raum.setKapazitaet(rs.getInt("Kapazitaet"));
	            raum.setBezeichnung(rs.getString("Bezeichnung"));
	            raeume.add(raum);  
	          }
			
			}
			catch (SQLException e1) {
				throw new RuntimeException("Datenbankbankproblem");				
			}
		return raeume;
	}
	
}
