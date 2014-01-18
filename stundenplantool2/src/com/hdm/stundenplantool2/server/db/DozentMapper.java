package com.hdm.stundenplantool2.server.db;


import java.sql.*;
import java.util.Vector;

import com.hdm.stundenplantool2.shared.bo.*;

/**
 * Mapper-Klasse, die <code>Dozent</code>-Objekte auf eine relationale
 * Datenbank abbildet. Hierzu wird eine Reihe von Methoden zur Verfügung
 * gestellt, mit deren Hilfe z.B. Objekte gesucht, erzeugt, modifiziert und
 * gelöscht werden können. Das Mapping ist bidirektional. D.h., Objekte können
 * in DB-Strukturen und DB-Strukturen in Objekte umgewandelt werden.
 * 
 * @see BelegungMapper, LehrveranstaltungMapper, RaumMapper, SemesterverbandMapper, StudiengangMapper, ZeitslotMapper
 * @author Thies (implement: Zimmermann, Klatt, Roth)
 */
public class DozentMapper {
	
	private static DozentMapper dozentMapper = null;
	
	protected DozentMapper(){
		
	}
	
	public static DozentMapper dozentMapper() {
	    if (dozentMapper == null) {
	      dozentMapper = new DozentMapper();
	    }

	    return dozentMapper;
	   }
	
	/**
	 * Methode um eine beliebige Anzahl an Dozenten anhand Ihrerer ID's aus der
	 * DB auszulesen
	 * 
	 * @param	id Primärschlüsselattribut(e) (->DB)
	 * 			boolean zur Steuerung welche referenzierten Entitäten/Objekte geladen 
	 * 			bzw. erzeugt werden (dient Performance-Zwecken)
	 * @return	Vector mit Dozenten, die den Primärschlüsselattributen entsprechen
	 */
	public Vector<Dozent> findByKey(Vector<Integer> keys, Boolean loop) throws RuntimeException {
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
		Vector<Dozent> dozenten = new Vector<Dozent>();
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Dozent WHERE ID IN (" + ids.toString() + ")";
			rs = stmt.executeQuery(sql);
			
			// Erstellung des "Dozenten-Vectors"
			while(rs.next()){
	            Dozent dozent = new Dozent();
	            dozent.setId(rs.getInt("ID"));
	            dozent.setNachname(rs.getString("Nachname"));
	            dozent.setVorname(rs.getString("Vorname"));
	            dozent.setPersonalnummer(rs.getInt("Personalnummer"));
	            dozenten.add(dozent);  
	          }
			
			
			// Einfügen der zugehörigen Belegungen in die Dozenten des "Dozenten-Vectors"
			if (loop == true) {
				for (int i = 0; i < dozenten.size(); i++) {
					sql = "SELECT BelegungID FROM Dozentenbelegung_ZWT WHERE DozentID = "+ dozenten.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
						
					BelegungMapper bMapper = BelegungMapper.belegungMapper();
					Vector<Integer> vi = new Vector<Integer>();
						
						while(rs.next()){
							
							vi.add(rs.getInt("BelegungID")); 
							
							}
						if (vi.size() > 0) {
							dozenten.elementAt(i).setBelegungen(bMapper.findByKey(vi, false));
						}
					}
			}
					
			// Einfügen der zugehörigen Lehrveranstaltungen in die Dozenten des "Dozenten-Vectors"
			if (loop == true) {
				for (int i = 0; i < dozenten.size(); i++) {
					sql = "SELECT LehrveranstaltungID FROM Dozentenzugehörigkeit_ZWT WHERE DozentID = "+ dozenten.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
						
					LehrveranstaltungMapper lMapper = LehrveranstaltungMapper.lehrveranstaltungMapper();
					Vector<Integer> vi = new Vector<Integer>();
						
						while(rs.next()){
							
							vi.add(rs.getInt("LehrveranstaltungID")); 
							
							}
						if (vi.size() > 0) {
							dozenten.elementAt(i).setLehrveranstaltungen(lMapper.findByKey(vi, false));
						}					 
						
					}
					
			}
			}
			catch (SQLException e1) {
				throw new RuntimeException("Datenbankbankproblem - dm fbk");				
			}
		
		return dozenten;
	}
	
	/**
	 * Methode um alle Dozenten anhand eines Lehrveranstaltung-Objekts aus der DB auszulesen
	 * 
	 * @param	Lehrveranstaltung-Objekt aufgrund dessen die Dozenten ausgelesen werden sollen
	 * 			boolean zur Steuerung welche referenzierten Entitäten/Objekte geladen 
	 * 			bzw. erzeugt werden (dient Performance-Zwecken) 			
	 * @return	Vector mit Dozenten
	 */
	public Vector<Dozent> findByLV(Lehrveranstaltung lv, Boolean loop) throws RuntimeException {
		
		//Einholen einer DB-Verbindung		
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Dozent> dozenten = new Vector<Dozent>();
		try{
			// Ausführen des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM stundenplantool.Dozent WHERE ID IN (SELECT DozentID FROM stundenplantool.Dozentenzugehörigkeit_ZWT WHERE LehrveranstaltungID = " + lv.getId() + ")";
			rs = stmt.executeQuery(sql);
			
			// Erstellung des "Dozenten-Vectors"
			while(rs.next()){
	            Dozent dozent = new Dozent();
	            dozent.setId(rs.getInt("ID"));
	            dozent.setNachname(rs.getString("Nachname"));
	            dozent.setVorname(rs.getString("Vorname"));
	            dozent.setPersonalnummer(rs.getInt("Personalnummer"));
	            dozenten.add(dozent);  
	          }
			
			
			// Einfügen der zugehörigen Belegungen in die Dozenten des "Dozenten-Vectors"
			if (loop == true) {
				for (int i = 0; i < dozenten.size(); i++) {
					sql = "SELECT BelegungID FROM Dozentenbelegung_ZWT WHERE DozentID = "+ dozenten.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
						
					BelegungMapper bMapper = BelegungMapper.belegungMapper();
					Vector<Integer> vi = new Vector<Integer>();
						
					while(rs.next()){
							
						vi.add(rs.getInt("BelegungID")); 
							
						}
					if (vi.size() > 0) {
						dozenten.elementAt(i).setBelegungen(bMapper.findByKey(vi, false));
					}
				}
			}
					
			// Einfügen der zugehörigen Lehrveranstaltungen in die Dozenten des "Dozenten-Vectors"
			if (loop == true) {
				for (int i = 0; i < dozenten.size(); i++) {
					sql = "SELECT LehrveranstaltungID FROM Dozentenzugehörigkeit_ZWT WHERE DozentID = "+ dozenten.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
						
					LehrveranstaltungMapper lMapper = LehrveranstaltungMapper.lehrveranstaltungMapper();
					Vector<Integer> vi = new Vector<Integer>();
						
					while(rs.next()){
							
						vi.add(rs.getInt("LehrveranstaltungID")); 
							
					}
					if (vi.size() > 0) {
						dozenten.elementAt(i).setLehrveranstaltungen(lMapper.findByKey(vi, false));
					}						
				}					
			}

		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - dm fbl");				
		}
		
		return dozenten;
	}
	
	/**
	 * Methode um einen Dozent in der DB zu aktualisieren
	 * 
	 * @param	Dozent-Objekt welches aktualisiert werden soll 			
	 * @return	Dozent-Objekt
	 */
	public Dozent update(Dozent dozent) throws RuntimeException {
		Connection con = DBConnection.connection();
		
		// Aktualisierung der Dozent-Entität in der DB		
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "UPDATE Dozent SET Nachname='"+dozent.getNachname()+"', Vorname='"+dozent.getVorname()+"', Personalnummer='"+dozent.getPersonalnummer()+"' WHERE ID="+dozent.getId()+";";
			stmt.executeUpdate(sql);
			
			/* --> Aktuell wird es nicht möglich sein, über den Dozenten Belegungen zu ändern
			 * Löschen der "Dozenten-Belegung" (die m zu n Beziehung zwischen Dozent und Belegungen)
			 *  >>> Funktion am 08.12.2013 deaktiviert - Erforderniss fraglich
			 
			sql = "DELETE FROM Dozentenbelegung_ZWT WHERE DozentID = '"+dozent.getId()+"';";
			stmt.executeUpdate(sql);
			
			// Aktualisierung der "Dozenten-Belegung" (die m zu n Beziehung zwischen Dozent und Belegungen)
			
			if (dozent.getBelegungen() != null) {
				for (int i = 0; i < dozent.getBelegungen().size(); i++){
					sql = "INSERT INTO Dozentenbelegung_ZWT (`DozentID`, `BelegungID`) VALUES ('"+dozent.getId()+"', '"+dozent.getBelegungen().elementAt(i).getId()+"');";
					stmt.executeUpdate(sql);
					}
			}
			*/
			
			
			// Löschen der "Dozenten-Zugehörigkeit" (die m zu n Beziehung zwischen Dozent und Lehrveranstaltung)		
			sql = "DELETE FROM Dozentenzugehörigkeit_ZWT WHERE DozentID = '"+dozent.getId()+"';";
			stmt.executeUpdate(sql);
					
			// Aktualisierung der "Dozenten-Zugehörigkeit" (die m zu n Beziehung zwischen Dozent und Lehrveranstaltung)		
			if (dozent.getLehrveranstaltungen() != null) {
				for (int i = 0; i < dozent.getLehrveranstaltungen().size(); i++){
					sql = "INSERT INTO Dozentenzugehörigkeit_ZWT (`LehrveranstaltungID`, `DozentID`) VALUES ('"+dozent.getLehrveranstaltungen().elementAt(i).getId()+"', '"+dozent.getId()+"');";
					stmt.executeUpdate(sql);
					}
			}

		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - DozentMapper.update");
		}
		
		return dozent;
	}
	
	/**
	 * Methode um eine neuen Dozent in die DB zu schreiben
	 * 
	 * @param	Dozent-Objekt welcher neu hinzukommt			
	 * @return	Dozent-Objekt
	 */
	public Dozent insertIntoDB(Dozent dozent) throws RuntimeException {
		Connection con = DBConnection.connection();
		ResultSet rs;
						
		try{
			// Ausführung des SQL-Querys	
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO Dozent (`Nachname`, `Vorname`, `Personalnummer`) VALUES ('"+dozent.getNachname()+"', '"+dozent.getVorname()+"', '"+dozent.getPersonalnummer()+"');";
			stmt.executeUpdate(sql);
			
			/*
			 *  Auslesen der nach einfügen eines neuen Dozenten in DB entstandenen "größten" ID
			 *  @author: Herr Prof. Thies 
			 *  @implement: Lucas Zanella 
			 */
			sql = "SELECT MAX(ID) AS maxid FROM Dozent;";
			rs = stmt.executeQuery(sql);
			
			/*
			 *  Setzen der ID dem hier aktuellen Semesterverband-Objekt
			 *  @author: Herr Prof. Thies
			 *  @implement: Lucas Zanella 
			 */
			while(rs.next()){
				dozent.setId(rs.getInt("maxid"));
			}
			
			/*
			 * Das Hinzufügen von Belegungen ist mit der Anlegung eines Dozenten nicht im gleichen Schritt möglich,
			 * daher wurde die Funktion deaktiviert - Stand 17.01.2014
			 * 
			if(dozent.getBelegungen() != null) {
				for ( int i = 0; i < dozent.getBelegungen().size(); i++) {
					sql = "INSERT INTO Dozentenbelegung_ZWT (`DozentID`, `BelegungID`) VALUES ('"+dozent.getId()+"', '"+dozent.getBelegungen().elementAt(i).getId()+"');";
					stmt.executeUpdate(sql);
				}
			}
			*/
			
			if(dozent.getLehrveranstaltungen() != null) {
				for ( int i = 0; i < dozent.getLehrveranstaltungen().size(); i++) {
					sql = "INSERT INTO Dozentenzugehörigkeit_ZWT (`LehrveranstaltungID`, `DozentID`) VALUES ('"+dozent.getLehrveranstaltungen().elementAt(i).getId()+"', '"+dozent.getId()+"');";
					stmt.executeUpdate(sql);
				}
			}
		
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem");
		}
		
		return dozent;
	}
	
	/**
	 * Methode um alle Dozenten aus der DB auszulesen
	 * 
	 * @param	boolean zur Steuerung welche referenzierten Entitäten/Objekte geladen 
	 * 			bzw. erzeugt werden (dient Performance-Zwecken)
	 * @return	Vector mit Dozenten
	 */	
	public Vector<Dozent> findAll(Boolean loop) throws RuntimeException {
				
		Connection con = DBConnection.connection();
		
		Vector<Dozent> dozenten = new Vector<Dozent>();
		
		try{		
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM Dozent;";
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				Dozent dozent = new Dozent();
	            dozent.setId(rs.getInt("ID"));
	            dozent.setNachname(rs.getString("Nachname"));
	            dozent.setVorname(rs.getString("Vorname"));
	            dozent.setPersonalnummer(rs.getInt("Personalnummer"));
	            dozenten.add(dozent); 
			}
			
					
			/* 
			 * Das Einfügen der zugehörigen Belegungen in die Dozenten des "Dozenten-Vectors"	
			 * wurde aus Performancegründen deaktiviert - Stand: 18.01.2014		
			
			if (loop == true) {
				for (int i = 0; i < dozenten.size(); i++) {
					sql = "SELECT BelegungID FROM Dozentenbelegung_ZWT WHERE DozentID = "+ dozenten.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
						
					BelegungMapper bMapper = BelegungMapper.belegungMapper();
					Vector<Integer> vi = new Vector<Integer>();
						
						while(rs.next()){
							
							vi.add(rs.getInt("BelegungID")); 
							
							}
						if (vi.size() > 0) {
							dozenten.elementAt(i).setBelegungen(bMapper.findByKey(vi, false));
						}					 
					}
			}
			*/	
				
			// Einfügen der zugehörigen Lehrveranstaltungen in die Dozenten des "Dozenten-Vectors"			
			if (loop == true) {
				for (int i = 0; i < dozenten.size(); i++) {
					sql = "SELECT LehrveranstaltungID FROM Dozentenzugehörigkeit_ZWT WHERE DozentID = "+ dozenten.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
						
					LehrveranstaltungMapper lMapper = LehrveranstaltungMapper.lehrveranstaltungMapper();
					Vector<Integer> vi = new Vector<Integer>();
						
						while(rs.next()){
							
							vi.add(rs.getInt("LehrveranstaltungID")); 
							
							}
						if (vi.size() > 0) {
							dozenten.elementAt(i).setLehrveranstaltungen(lMapper.findByKey(vi, false));
						}					 
						
					}
			}

		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - dm fa");		
		}
		
				
		return dozenten;
			
	}
	
	/**
	 * Methode um einen Dozent aus der DB zu löschen
	 * 
	 * @param	Dozent-Objekt welches gelöscht werden soll
	 */
	public void delete(Dozent dozent) throws RuntimeException {
		
		// Ein Dozent kann nur gelöscht werden, wenn er durch keine Belegungen mehr referenziert ist
		if (dozent.getBelegungen() == null) {
			Connection con = DBConnection.connection();
			try {
				Statement stmt = con.createStatement();
				
				// Löschen der Referenzen von Dozenten zu Lehrveranstaltungen aus "Dozentenzugehörigkeit_ZWT"
				String sql = "DELETE FROM Dozentenzugehörigkeit_ZWT WHERE DozentID = '"+ dozent.getId()+ "';";
				stmt.executeUpdate(sql);
				
				// Löschen Löschen der Dozent-Entität
				sql = "DELETE FROM Dozent WHERE ID = '"+dozent.getId()+"';";
				stmt.executeUpdate(sql);
				
				
				
			}
			catch (SQLException e1) {
				throw new RuntimeException("Datenbankbankproblem");
			}
		}
	}
	

}
