package com.hdm.stundenplantool2.server.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.hdm.stundenplantool2.shared.bo.*;

/**
 * Mapper-Klasse, die <code>Belegung</code>-Objekte auf eine relationale
 * Datenbank abbildet. Hierzu wird eine Reihe von Methoden zur Verfügung
 * gestellt, mit deren Hilfe z.B. Objekte gesucht, erzeugt, modifiziert und
 * gelöscht werden können. Das Mapping ist bidirektional. D.h., Objekte können
 * in DB-Strukturen und DB-Strukturen in Objekte umgewandelt werden.
 * 
 * @see DozentMapper, LehrveranstaltungMapper, RaumMapper, SemesterverbandMapper, StudiengangMapper, ZeitslotMapper
 * @author Thies (implement: Zimmermann, Klatt, Roth)
 */
public class BelegungMapper {
	
	private static BelegungMapper belegungMapper = null;
	
	protected BelegungMapper(){
		
	}
	
	public static BelegungMapper belegungMapper() {
	    if (belegungMapper == null) {
	      belegungMapper = new BelegungMapper();
	    }

	    return belegungMapper;
	   }
	
	/**
	 * Methode um eine beliebige Anzahl an Belegungen anhand Ihrerer ID's aus der
	 * DB auszulesen
	 * 
	 * @param	id Primärschlüsselattribut(e) (->DB)
	 * 			boolean zur Steuerung welche referenzierten Entitäten/Objekte geladen 
	 * 			bzw. erzeugt werden (dient Performance-Zwecken)
	 * @return	Vector mit Belegungen, die den Primärschlüsselattributen entsprechen
	 */
	public Vector<Belegung> findByKey(Vector<Integer> keys, Boolean loop) throws RuntimeException {
		StringBuffer ids = new StringBuffer();
		
		String sql;
		
		//Erstellung des dynamischen Teils des SQL-Querys		
		if (keys.size() > 1) {
			for (int i = 0; i < keys.size()-1; i++) {
				ids.append(keys.elementAt(i));	
				ids.append(",");
			}
		}
			
		ids.append(keys.elementAt(keys.size()-1));			
			
		//Einholen einer DB-Verbindung und		
		Connection con = DBConnection.connection();
		Statement stmt;
		ResultSet rs;
		Vector<Belegung> belegungen = new Vector<Belegung>();
		try{
			// Ausführung des SQL-Querys
			stmt = con.createStatement();
			sql = "SELECT * FROM Belegung WHERE ID IN (" + ids.toString() + ")";
			rs = stmt.executeQuery(sql);
			
			// Befüllen des "Belegung-Vectors"
			while(rs.next()){
	            Belegung belegung = new Belegung();
	            belegung.setId(rs.getInt("ID"));
	            
	            Vector<Integer> vI1 = new Vector<Integer>();
	            vI1.add(rs.getInt("RaumID"));
	            belegung.setRaum(RaumMapper.raumMapper().findByKey(vI1).elementAt(0));
	            
	            Vector<Integer> vI2 = new Vector<Integer>();
	            vI2.add(rs.getInt("ZeitslotID"));
	            belegung.setZeitslot(ZeitslotMapper.zeitslotMapper().findByKey(vI2).elementAt(0));
	            
	            Vector<Integer> vI3 = new Vector<Integer>();
	            vI3.add(rs.getInt("LehrveranstaltungID"));
	            belegung.setLehrveranstaltung(LehrveranstaltungMapper.lehrveranstaltungMapper().findByKey(vI3, false).elementAt(0));
	            belegungen.add(belegung);  
	          }
		}
		catch(SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - BelegungMapper.findByKey-1");
		}
			
		try{
			
		// Einfügen der zugehörigen Dozenten in die einzelenen Belegungen des "Belegung-Vectors"		
			if (loop == true) {
				for (int i = 0; i < belegungen.size(); i++) {
					sql = "SELECT DozentID FROM Dozentenbelegung_ZWT WHERE BelegungID = "+ belegungen.elementAt(i).getId();
					stmt = con.createStatement();
					rs = stmt.executeQuery(sql);
					
					DozentMapper dMapper = DozentMapper.dozentMapper();
					Vector<Integer> vi = new Vector<Integer>();
					
					while(rs.next()){
						
						vi.add(rs.getInt("DozentID")); 
						
					}
					
					if (vi.size() > 0) {
						belegungen.elementAt(i).setDozenten(dMapper.findByKey(vi, false));
					}					
				}
			}
		}
		catch(SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - BelegungMapper.findByKey-2");
		}		
		
		try{
		// Einfügen der zugehörigen Semesterverbaende in die einzelenen Belegungen des "Belegung-Vectors"
		
		if (loop == true) {
			for (int i = 0; i < belegungen.size(); i++) {
				sql = "SELECT SemesterverbandID FROM Semesterverbandszugehörigkeit_ZWT WHERE BelegungID = "+ belegungen.elementAt(i).getId();
				stmt = con.createStatement();
				rs = stmt.executeQuery(sql);
					
				SemesterverbandMapper svMapper = SemesterverbandMapper.semesterverbandMapper();
				Vector<Integer> vi = new Vector<Integer>();
					
					while(rs.next()){
						
						vi.add(rs.getInt("SemesterverbandID")); 
						
						}
					if (vi.size() > 0) {
						belegungen.elementAt(i).setSemesterverbaende(svMapper.findByKey(vi, false));
					}					 
					
				}
		}
					
			//con.close();
			}
			catch (SQLException e1) {
				throw new RuntimeException("Datenbankbankproblem - BelegungMapper.findByKey-3");				
			}
		
		return belegungen;
	}
	
	/**
	 * Methode um alle Belegungen aus der DB auszulesen
	 * 
	 * @param	boolean zur Steuerung welche referenzierten Entitäten/Objekte geladen 
	 * 			bzw. erzeugt werden (dient Performance-Zwecken)
	 * @return	Vector mit Belegungen
	 */	
	public Vector<Belegung> findAll(Boolean loop) throws RuntimeException {
		
		//Einholen einer DB-Verbindung und		
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Belegung> belegungen = new Vector<Belegung>();
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT Belegung.ID, Belegung.RaumID, Raum.Kapazitaet, Raum.Bezeichnung, Belegung.ZeitslotID, "
					+ "Zeitslot.Anfangszeit, Zeitslot.Endzeit, Zeitslot.Wochentag, Belegung.LehrveranstaltungID, "
					+ "Lehrveranstaltung.Umfang, Lehrveranstaltung.Bezeichnung, Lehrveranstaltung.Studiensemester "
					+ "FROM stundenplantool.Belegung JOIN stundenplantool.Raum ON Belegung.RaumID = Raum.ID "
					+ "JOIN stundenplantool.Zeitslot ON Belegung.ZeitslotID = Zeitslot.ID JOIN stundenplantool.Lehrveranstaltung "
					+ "ON Belegung.LehrveranstaltungID = Lehrveranstaltung.ID";
			rs = stmt.executeQuery(sql);
			
			// Befüllen des "Belegung-Vectors"
			while(rs.next()){
	            Belegung belegung = new Belegung();
	            belegung.setId(rs.getInt("ID"));
	            
	            Raum raum = new Raum();
	            raum.setId(rs.getInt("RaumID"));
	            raum.setKapazitaet(rs.getInt("Kapazitaet"));
	            raum.setBezeichnung(rs.getString("Bezeichnung"));
	            belegung.setRaum(raum);
	            
	            Zeitslot zeitslot = new Zeitslot();
	            zeitslot.setId(rs.getInt("ZeitslotID"));
	            zeitslot.setAnfangszeit(rs.getInt("Anfangszeit"));
	            zeitslot.setEndzeit(rs.getInt("Endzeit"));
	            zeitslot.setWochentag(rs.getString("Wochentag"));
	            belegung.setZeitslot(zeitslot);
	            
	            Lehrveranstaltung lv = new Lehrveranstaltung();
	            lv.setId(rs.getInt("LehrveranstaltungID"));
	            lv.setUmfang(rs.getInt("Umfang"));
	            lv.setBezeichnung(rs.getString("Bezeichnung"));
	            lv.setStudiensemester(rs.getInt("Studiensemester"));
	            belegung.setLehrveranstaltung(lv);
	            
	            belegungen.add(belegung);  
	          }
			
			
			// Einfügen der zugehörigen Dozenten in die einzelenen Belegungen des "Belegung-Vectors"		
			if (loop == true) {
				for (int i = 0; i < belegungen.size(); i++) {
					sql = "SELECT DozentID FROM Dozentenbelegung_ZWT WHERE BelegungID = "+ belegungen.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
					
					DozentMapper dMapper = DozentMapper.dozentMapper();
					Vector<Integer> vi = new Vector<Integer>();
					
					while(rs.next()){
						
						vi.add(rs.getInt("DozentID")); 
						
					}
					if (vi.size() > 0) {
						belegungen.elementAt(i).setDozenten(dMapper.findByKey(vi, false));
					}					 
				}
			}
			
			// Einfügen der zugehörigen Semesterverbände in die einzelenen Belegungen des "Belegung-Vectors"		
			if (loop == true) {
				for (int i = 0; i < belegungen.size(); i++) {
					sql = "SELECT SemesterverbandID FROM Semesterverbandszugehörigkeit_ZWT WHERE BelegungID = "+ belegungen.elementAt(i).getId();
					rs = stmt.executeQuery(sql);
				
					SemesterverbandMapper svMapper = SemesterverbandMapper.semesterverbandMapper();
					Vector<Integer> vi = new Vector<Integer>();
					
					while(rs.next()){						
						vi.add(rs.getInt("SemesterverbandID"));						
					}
				
					if (vi.size() > 0) {
						belegungen.elementAt(i).setSemesterverbaende(svMapper.findByKey(vi, false));
					}					 
					
				}
			}					
		
		}
		catch (SQLException e1) {
				throw new RuntimeException("Datenbankbankproblem");				
		}
		
	return belegungen;
	}
	
	/**
	 * Methode um alle Belegungen anhand eines Raum-Objekts aus der DB auszulesen
	 * 
	 * @param	Raum-Objekt aufgrund dessen die Belegungen ausgelesen werden sollen 			
	 * @return	Vector mit Belegungen
	 */	
	public Vector<Belegung> findByRaum(Raum raum) throws RuntimeException {
		
		//Einholen einer DB-Verbindung und		
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Integer> vi = new Vector<Integer>();
		
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT ID FROM Belegung WHERE RaumID = "+ raum.getId();
			rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				
				vi.add(rs.getInt("ID")); 
				
				}
			}
		
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem");			
		}
		if (vi.size() > 0) {
			return this.findByKey(vi, true);
		}
		else return null;			
		}
	
	/**
	 * Methode um alle Belegungen anhand eines Zeitslot-Objekt aus der DB auszulesen
	 * 
	 * @param	Zeitslot-Objekt aufgrund dessen die Belegungen ausgelesen werden sollen 			
	 * @return	Vector mit Belegungen
	 */	
	public Vector<Belegung> findByZeitslot(Zeitslot zs) throws RuntimeException {
		//Einholen einer DB-Verbindung und
			
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Integer> vi = new Vector<Integer>();
			
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT ID FROM Belegung WHERE ZeitslotID = "+ zs.getId();
			rs = stmt.executeQuery(sql);
				
			while(rs.next()){
					
				vi.add(rs.getInt("ID")); 
					
				}
			}
			
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem");			
		}
		if (vi.size() > 0) {
			return this.findByKey(vi, true);
		}
		else return null;			
		}
	
	/**
	 * Methode um alle Belegungen anhand eines Dozent-Objekts aus der DB auszulesen
	 * 
	 * @param	Dozent-Objekt aufgrund dessen die Belegungen ausgelesen werden sollen 			
	 * @return	Vector mit Belegungen
	 */
	public Vector<Belegung> findByDozent(Dozent dozent) throws RuntimeException {
		
		//Einholen einer DB-Verbindung 		
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Integer> vi = new Vector<Integer>();
		
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT BelegungID FROM Dozentenbelegung_ZWT WHERE DozentID = "+ dozent.getId();
			rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				
				vi.add(rs.getInt("BelegungID")); 
				
				}
			}
		
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem");			
		}
		if (vi.size() > 0) {
			return this.findByKey(vi, true);
		}
		else return null;			
		}
	
	/**
	 * Methode um alle Belegungen anhand eines Semesterverband-Objekts aus der DB auszulesen
	 * 
	 * @param	Semesterverband-Objekt aufgrund dessen die Belegungen ausgelesen werden sollen 			
	 * @return	Vector mit Belegungen
	 */
	public Vector<Belegung> findBySemesterverband(Semesterverband semesterverband) throws RuntimeException {
		
		//Einholen einer DB-Verbindung		
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Integer> vi = new Vector<Integer>();
		
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT BelegungID FROM Semesterverbandszugehörigkeit_ZWT WHERE SemesterverbandID = "+ semesterverband.getId();
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				
				vi.add(rs.getInt("BelegungID"));
				}
			}
		
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem");			
		}
		if (vi.size() > 0) {
			return this.findByKey(vi, true);
		}
		else return null;			
	}
	
	/**
	 * Methode um alle Belegungen anhand eines Lehrveranstaltung-Objekts aus der DB auszulesen
	 * 
	 * @param	Lehrveranstaltung-Objekt aufgrund dessen die Belegungen ausgelesen werden sollen 			
	 * @return	Vector mit Belegungen
	 */
	public Vector<Belegung> findByLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) throws RuntimeException {
		
		//Einholen einer DB-Verbindung			
		Connection con = DBConnection.connection();
		ResultSet rs;
		Vector<Integer> vi = new Vector<Integer>();
			
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "SELECT ID FROM Belegung WHERE LehrveranstaltungID = "+ lehrveranstaltung.getId();
			rs = stmt.executeQuery(sql);
				
			while(rs.next()){
					
				vi.add(rs.getInt("ID")); 
					
				}
			}
			
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem - BelegungMapper.findByLehrveranstaltung");			
		}
		if (vi.size() > 0) {
			return this.findByKey(vi, true);
		}
		else return null;			
	}
	
	/**
	 * Methode um eine Belegung in der DB zu aktualisieren
	 * 
	 * @param	Belegung-Objekt welches aktualisiert werden soll 			
	 * @return	Belegung-Objekt
	 */
	public Belegung update(Belegung belegung) throws RuntimeException {
		Connection con = DBConnection.connection();
		
		// Aktualisierung der Belegung-Entität in der DB		
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "UPDATE Belegung SET RaumID='"+belegung.getRaum().getId()+"', ZeitslotID='"+belegung.getZeitslot().getId()+"', LehrveranstaltungID='"+belegung.getLehrveranstaltung().getId()+"' WHERE ID="+belegung.getId()+";";
			stmt.executeUpdate(sql);
			
			// Löschen der "Dozenten-Belegung" (die m zu n Beziehung zwischen Dozent und Belegungen)		
			sql = "DELETE FROM Dozentenbelegung_ZWT WHERE BelegungID = '"+belegung.getId()+"';";
			stmt.executeUpdate(sql);
			
			// Aktualisierung der "Dozenten-Belegung" (die m zu n Beziehung zwischen Dozent und Belegungen)		
			if (belegung.getDozenten() != null) {
				for (int i = 0; i < belegung.getDozenten().size(); i++){
					sql = "INSERT INTO Dozentenbelegung_ZWT (`DozentID`, `BelegungID`) VALUES ('"+belegung.getDozenten().elementAt(i).getId()+"', '"+belegung.getId()+"');";
					stmt.executeUpdate(sql);
					}
			}
			
			// Löschen der "Semesterverbandszugehörigkeit" (die m zu n Beziehung zwischen Semesterverbände und Belegungen)		
			sql = "DELETE FROM Semesterverbandszugehörigkeit_ZWT WHERE BelegungID = '"+belegung.getId()+"';";
			stmt.executeUpdate(sql);
					
			// Aktualisierung der "Semesterverbandszugehörigkeit" (die m zu n Beziehung zwischen Semesterverbände und Belegungen)		
			if (belegung.getSemesterverbaende() != null) {
				for (int i = 0; i < belegung.getSemesterverbaende().size(); i++){
					sql = "INSERT INTO Semesterverbandszugehörigkeit_ZWT (`SemesterverbandID`, `BelegungID`) VALUES ('"+belegung.getSemesterverbaende().elementAt(i).getId()+"', '"+belegung.getId()+"');";
					stmt.executeUpdate(sql);
					}
			}
			
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem");		
		}
		
		return belegung;
	}
	
	/**
	 * Methode um eine Belegung aus der DB zu löschen
	 * 
	 * @param	Belegung-Objekt welches gelöscht werden soll
	 */
	public void delete(Belegung belegung) throws RuntimeException {

			Connection con = DBConnection.connection();
			try {
				Statement stmt = con.createStatement();
				
				// Löschen der "Semesterverbandszugehörigkeit" (die m zu n Beziehung zwischen Semesterverbände und Belegungen)				
				String sql = "DELETE FROM Semesterverbandszugehörigkeit_ZWT WHERE BelegungID = '"+belegung.getId()+"';";
				stmt.executeUpdate(sql);
				
				// Löschen der "Dozenten-Belegung" (die m zu n Beziehung zwischen Dozent und Belegungen)				
				sql = "DELETE FROM Dozentenbelegung_ZWT WHERE BelegungID = '"+belegung.getId()+"';";
				stmt.executeUpdate(sql);
				
				// Löschen der Belegung-Entität
				sql = "DELETE FROM Belegung WHERE ID = '"+belegung.getId()+"';";
				stmt.executeUpdate(sql);			
				
			}
			catch (SQLException e1) {
				throw new RuntimeException("Datenbankbankproblem");
			}
		}
	
	/**
	 * Methode um eine neue Belegung in die DB zu schreiben
	 * 
	 * @param	Belegung-Objekt welches neu hinzukommt			
	 * @return	Belegung-Objekt
	 */
	public Belegung insertIntoDB(Belegung belegung) throws RuntimeException {
		Connection con = DBConnection.connection();
		ResultSet rs;
						
		try{
			// Ausführung des SQL-Querys
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO Belegung (`RaumID`, `ZeitslotID`, `LehrveranstaltungID`) VALUES ('"+belegung.getRaum().getId()+"', '"+belegung.getZeitslot().getId()+"', '"+belegung.getLehrveranstaltung().getId()+"');";
			stmt.executeUpdate(sql);
			
			/*
			 *  Auslesen der nach einfügen eines neuen Dozenten in DB entstandenen "größten" ID
			 *  @author: Herr Prof. Thies 
			 *  @implement: Lucas Zanella 
			 */
			sql = "SELECT MAX(ID) AS maxid FROM Belegung;";
			rs = stmt.executeQuery(sql);
			
			/*
			 *  Setzen der ID dem hier aktuellen Belegung-Objekt
			 *  @author: Herr Prof. Thies
			 *  @implement: Lucas Zanella 
			 */
			while(rs.next()){
				belegung.setId(rs.getInt("maxid"));
			}
			
			// Verknüpfen von Dozenten mit der neuen Belegung über die Zwischentabelle "Dozentenbelegung_ZWT"
			if(belegung.getDozenten() != null) {
				for ( int i = 0; i < belegung.getDozenten().size(); i++) {
					sql = "INSERT INTO Dozentenbelegung_ZWT (`DozentID`, `BelegungID`) VALUES ('"+belegung.getDozenten().elementAt(i).getId()+"', '"+belegung.getId()+"');";
					stmt.executeUpdate(sql);
				}
			}
			
			// Verknüpfen von Semesterverbaenden mit der neuen Belegung über die Zwischentabelle "Semesterverbandszugehörigkeit_ZWT"
			if(belegung.getSemesterverbaende() != null) {
				for ( int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
					sql = "INSERT INTO Semesterverbandszugehörigkeit_ZWT (`SemesterverbandID`, `BelegungID`) VALUES ('"+belegung.getSemesterverbaende().elementAt(i).getId()+"', '"+belegung.getId()+"');";
					stmt.executeUpdate(sql);
				}
			}		
		}
		catch (SQLException e1) {
			throw new RuntimeException("Datenbankbankproblem");
		}
		
		return belegung;
	}

}
