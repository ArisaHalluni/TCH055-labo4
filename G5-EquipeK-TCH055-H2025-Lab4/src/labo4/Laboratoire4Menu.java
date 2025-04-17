package labo4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.Scanner;


/**
 * Classe principale du laboratoire 4
 * Contient un ensemble de méthodes statique pour 
 * la manipulation de la BD Produit 
 *  
 * @author Pamella Kissok
 * @author Inoussa Legrene
 * @author Amal Ben Abdellah
 * 
 * @equipe : K
 *
 * @author Arisa Halluni
 * @author Farkunda Wahedi
 * @author Ayoub Oubalkass
 * @author Adam Boulisfane
 * 
 * @version 2
 *
 */
public class Laboratoire4Menu {
	
	public static Statement statmnt = null;
	
	/* Référence vers l'objer de connection à la BD*/ 
	public static Connection connexion = null;
	
	/* Chargement du pilote Oracle */ 
	static {
	   try {
		   Class.forName("oracle.jdbc.driver.OracleDriver");
	   } catch (ClassNotFoundException e) {
		
		   e.printStackTrace();
	   }
	}
	
	/**
	 * Question : Ouverture de la connection
	 * 
	 * @param login
	 * @param password
	 * @param uri
	 * @return
	 * @throws SQLException 
	 */
    public static Connection connexionBDD(String login, String password, String uri) throws SQLException, ClassNotFoundException {
		Connection connJdbc = null;

		try {
			connJdbc = DriverManager.getConnection(uri, login, password);
			connJdbc.setAutoCommit(false);
		} catch (SQLException e){
			System.out.println("Connexion echouee : nom utilisateur ou mot de passe invalide");
		}
		return connJdbc;
    }
    
    /**
     *  Option 1 - lister les produits 
     * @throws SQLException 
     */
    public static void listerProduits() {

		String requete = "SELECT ref_produit, nom_produit, marque,prix_unitaire, quantite_stock, "+
				"quantite_seuil,statut_produit,code_fournisseur_prioritaire "+
				"FROM produit "+
				"ORDER BY ref_produit";

		try{
			Statement stm= connexion.createStatement();
			ResultSet resultat= stm.executeQuery(requete);

			System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
			System.out.printf("%-12s %-20s %-15s %-15s %-12s %-10s %-15s %-20s\n",
					"Référence", "NOM", "MARQUE", "Prix Unitaire", "Quantite", "Seuil", "Statut", "Code fournisseur");
			System.out.println("-----------------------------------------------------------------------------------------------------------------------------");

			while (resultat.next()) {
				System.out.printf("%-12s %-20s %-15s %-15.2f %-12d %-10d %-15s %-20d\n",
						resultat.getString("ref_produit"),
						resultat.getString("nom_produit"),
						resultat.getString("marque"),
						resultat.getDouble("prix_unitaire"),
						resultat.getInt("quantite_stock"),
						resultat.getInt("quantite_seuil"),
						resultat.getString("statut_produit"),
						resultat.getInt("code_fournisseur_prioritaire"));
			}
			System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
		}catch(SQLException e){
			System.out.println("erreur lors de l'affiche des produits");
			e.printStackTrace();
		}
    	
    }
    
    /**
     *  Option 2 - Ajouter un produit
     *   
     */
    public static void ajouterProduit() { 
    	Scanner sc = new Scanner(System.in);

		try{
			System.out.println("Veuillez saisir le numero de reference : ");
			String ref= sc.nextLine();

			System.out.print("Veuillez saisir le nom du produit : ");
			String nom = sc.nextLine();

			System.out.print("Veuillez saisir la marque : ");
			String marque = sc.nextLine();

			System.out.print("Veuillez saisir le prix unitaire : ");
			float prix = Float.parseFloat(sc.nextLine());

			System.out.print("Veuillez saisir la quantité en stock : ");
			int qteStock = Integer.parseInt(sc.nextLine());

			System.out.print("Veuillez saisir la quantité seuil : ");
			int qteSeuil = Integer.parseInt(sc.nextLine());

			System.out.print("Veuillez saisir le nom de la catégorie : ");
			String nomCategorie = sc.nextLine();

			System.out.print("Veuillez saisir le code fournisseur prioritaire : ");
			int codeFournisseur = Integer.parseInt(sc.nextLine());


			String req = "INSERT INTO Produit (ref_produit, nom_produit, marque, prix_unitaire, " +
					"quantite_stock, quantite_seuil, statut_produit, nom_categorie, code_fournisseur_prioritaire) " +
					"VALUES (?, ?, ?, ?, ?, ?, 'ENVENTE', ?, ?)";

			PreparedStatement requete = connexion.prepareStatement(req);

			requete.setString(1, ref);
			requete.setString(2, nom);
			requete.setString(3, marque);
			requete.setFloat(4, prix);
			requete.setInt(5, qteStock);
			requete.setInt(6, qteSeuil);
			requete.setString(7, nomCategorie);
			requete.setInt(8, codeFournisseur);

			int nbProduit = requete.executeUpdate();
			connexion.commit();

			if (nbProduit > 0) {
				System.out.println("Produit ajouté avec succès");
			} else {
				System.out.println("Échec de l’ajout du produit");
			}
		}catch (SQLException e) {
			System.out.println("Erreur SQL : " + e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println("Erreur : nombre invalide saisi.");
		}

		System.out.println("Appuyer sur ENTER pour continuer...");
		sc.nextLine();
    }
 
    /**
     * Option 3 : Affiche la Commande et ses items 
     *  
     * @param numCommande : numéro de la commande à afficher 
     * 
     */
    public static void afficherCommande(int numCommande) {
		//Verifier si la commande existe deja
		try {
			String commandeInfos =
					"SELECT c.nom, c.prenom, c.telephone, com.no_commande, com.date_commande, com.statut " +
							"FROM Client c INNER JOIN Commande com ON c.no_client = com.no_client " +
							"WHERE com.no_commande = ?";

			PreparedStatement requete = connexion.prepareStatement(commandeInfos);
			requete.setInt(1, numCommande);
			ResultSet rs = requete.executeQuery();

		if(!rs.next()){
			System.out.println("Aucune commande trouvee avec ce numero");
			return;
		}

		//En tete
			System.out.println("Client     : " + rs.getString("prenom") + " " + rs.getString("nom"));
			System.out.println("Téléphone  : " + rs.getString("telephone"));
			System.out.println("No Commande: " + rs.getInt("no_commande"));
			System.out.println("Date       : " + rs.getDate("date_commande"));
			System.out.println("Statut     : " + rs.getString("statut"));
			System.out.println("------------------------------------------------------------------------------------------------");
			System.out.printf("%-12s %-20s %-15s %-10s %-13s %-10s %-12s\n",
					"Ref Produit", "Nom", "Marque", "Prix", "Q.Commandée", "Q.Stock", "T.Partiel");
			System.out.println("------------------------------------------------------------------------------------------------");

			//element de la liste
			String itemsCommande =
					"SELECT p.ref_produit, p.nom_produit, p.marque, p.prix_unitaire, " +
							"       cp.quantite_cmd, p.quantite_stock, " +
							"       (p.prix_unitaire * cp.quantite_cmd) AS total_partiel " +
							"FROM Commande_Produit cp " +
							"INNER JOIN Produit p ON cp.no_produit = p.ref_produit " +
							"WHERE cp.no_commande = ?"+
							"ORDER BY total_partiel DESC";

			requete = connexion.prepareStatement(itemsCommande);
			requete.setInt(1, numCommande);
			rs = requete.executeQuery();

			double sommetotal = 0.0;
			while(rs.next()) {
				double sousTotal = rs.getDouble("total_partiel");
				sommetotal += sousTotal;

				System.out.printf("%-12s %-20s %-15s %-10.2f %-13d %-10d %-12.2f\n",
						rs.getString("ref_produit"),
						rs.getString("nom_produit"),
						rs.getString("marque"),
						rs.getDouble("prix_unitaire"),
						rs.getInt("quantite_cmd"),
						rs.getInt("quantite_stock"),
						sousTotal);
			}
			System.out.println("------------------------------------------------------------------------------------------------");
			System.out.printf("Total commande : %.2f $ \n", sommetotal);
			System.out.println("Appuyer sur ENTER pour continuer...");
			new Scanner(System.in).nextLine();
		} catch (SQLException e) {
			System.out.println("Erreur lors de l'affichage de la commande.");
			e.printStackTrace();
		}
	}
    /**
     * Option 4 : Calcule le total des paiements effectués pour une facture
     *   
     * @param numFacture : numéro de la facture
     * @param affichage  : si false, la méthode ne fait aucun affichage ni arrêt
     * 
     */
    public static float calculerPaiements(int numFacture , boolean affichage) {
		float totalmontant= 0;

		try {
			//Verifier si facture existe
			String chkFacture = "SELECT COUNT(*) AS total FROM Facture WHERE id_facture =?";
			PreparedStatement checkSt = connexion.prepareStatement(chkFacture);
			checkSt.setInt(1, numFacture);
			ResultSet rs = checkSt.executeQuery();

			if(rs.next() && rs.getInt("total")==0){
				if(affichage){
					System.out.println("facture non trouvee ou vide");
					System.out.println("Appuyez sur ENTER pour continuer ");
					new Scanner(System.in).nextLine();
				}
				return -1;
			}

			//total des paiements pour la facture choisi
			String requete = "SELECT SUM(montant) AS total_paiements FROM Paiement WHERE id_facture = ?";
			PreparedStatement ps = connexion.prepareStatement(requete);
			ps.setInt(1, numFacture);
			rs = ps.executeQuery();

			if (rs.next()) {
				totalmontant = rs.getFloat("total_paiements");
			}
			if (affichage) {
				System.out.printf("Total des paiements : %.2f $ \n", totalmontant);
				System.out.println("Appuyez sur ENTER pour continuer");
				new Scanner(System.in).nextLine();
			}
		}
		catch (SQLException e) {
		System.out.println("Erreur lors du calcul des paiements : " + e.getMessage());
	}
		return totalmontant;

    }

    /** 
     * Option 5 -  Enregistrer un paiement 
     * Ajoute un paiement pour une facture 
     *  
     * @param numFacture : numéro de la facture pour laquelle est fait le paiement
     * 
     */
    public static void enregistrerPaiement(int numFacture) { 

	Scanner sc= new Scanner(System.in);

	// Methode pas termine!!!
//	try{
//		//Verifier si facture existe
//		String sql = "SELECT montant, taxe FROM Facture WHERE id_facture = ?";
//		PreparedStatement requete = connexion.prepareStatement(sql);
//		requete.setInt(1, numFacture);
//		ResultSet resultat = requete.executeQuery();
//
//		if(!resultat.next()){
//			System.out.println("Facture non trouvee");
//			System.out.println("Appuyez sur ENTER pour continuer");
//			return;
//		}
//
//		float montantFacture = resultat.getFloat("montant");
//		float taxe =resultat.getFloat("taxe");
//
//	}

    }

    /**
     * 
     *  
     */
    
    /**
     * Option 6 : enregistre une liste d'évalutions dans la BD. Les données d'une évaluation sont des objets 
     * 			   SatisfactionData. 
     * 
     * @param listEvaluation : tableau d'objet StatisfactionData, contient les données des évaluations 
     * 						   du client à insérer dans la BD
     */
    public static void enregistreEvaluation(SatisfactionData[] listEvaluation) {
    	// Ligne suivante à supprimer après implémentation
    	System.out.println("Option 6 : enregistreEvaluation() n'est pas implémentée");
    }

    /**
     * Question 9 - fermeture de la connexion   
     * @return
     */
    public static boolean fermetureConnexion() {

		try{
			if(connexion !=null && !connexion.isClosed()){
				connexion.close();
				return true;
			}
		} catch (SQLException e) {
			System.out.println("Erreur lors de la fermeture de la connexion");
			e.printStackTrace();
		}
    	return false;
    }

    // ==============================================================================
    // NE PAS MODIFIER LE CODE QUI VA SUIVRE 
    // ==============================================================================    
    /**
     * Crée et retourne un tableau qui contient 5 évaluations de produits 
     * Chaque évaluation est stockée dans un objet de la classe SatisfactionData
     * 
     * @return un tableau d'objets SatisfactionData
     */
	public static SatisfactionData[] listSatisfactionData() {
			
		SatisfactionData[] list = new SatisfactionData[5]; 
		
		list[0] = new SatisfactionData(105 , "PC2000" , 4 , "PC très performant" ) ;
		list[1] = new SatisfactionData(105 , "LT2011" , 3 , "Produit satisfaisant, un peu bruyant" ) ;
		list[2] = new SatisfactionData(103 , "PC2000" , 5 , "Excellent ordinateur" ) ;
		list[3] = new SatisfactionData(101 , "DD2003" , 2 , "Performance moyenne du disque" ) ;
		list[4] = new SatisfactionData(104 , "SF3001" , 4 , "Je suis très satisfait de ma nouvelle version de l'OS" ) ;
		
		return list ;
	}
    /* ------------------------------------------------------------------------- */      
    /**
     * Affiche un menu pour le choix des opérations 
     * 
     */
    public static void afficheMenu(){
        System.out.println("0. Quitter le programme");
        System.out.println("1. Lister les produits");
        System.out.println("2. Ajouter un produit");
        System.out.println("3. Afficher une commande");
        System.out.println("4. Afficher le montant payé d'une facture");
        System.out.println("5. Enregistrer un paiement");
        System.out.println("6. Enregistrer les évaluations des clients");   
        System.out.println();
        System.out.println("Votre choix...");
    }
    
    
	/**
	 * La méthode main pour le lancement du programme 
	 * Il faut mettre les informations d'accès à la BDD  
	 * 
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String args[]) throws ClassNotFoundException, SQLException{
		
		// Mettre les informations de votre compte sur SGBD Oracle 
		String username = ".." ;
		String motDePasse = ".." ;
		
		String uri = "jdbc:oracle:thin:@localhost:1521:XE" ;
		
		// Appel de le méthode pour établir la connexion avec le SGBD 
		connexion = connexionBDD(username , motDePasse , uri ) ;
		
		if (connexion != null) {
			
			System.out.println("Connection reussie...");
			
			// Affichage du menu pour le choix des opérations 
			afficheMenu(); 
             
			Scanner sc = new Scanner(System.in);
            String choix = sc.nextLine();
            
            while(!choix.equals("0")){
           	
                if(choix.equals("1")){
 
                    listerProduits() ; 
                    
                 }else if(choix.equals("2")){
 
                	 ajouterProduit() ; 
                                     
                 }else if(choix.equals("3")){
 
                    System.out.print("Veuillez saisir le numéro de la commande: ");
                    sc = new Scanner(System.in);
                    int numCommande = Integer.parseInt(sc.nextLine().trim()) ;              
                   
                    afficherCommande(numCommande) ; 
                    
                 }else if(choix.equals("4")){
                	
                	sc = new Scanner(System.in);
                	System.out.print("Veuillez saisir le numéro de la facture : ");
                	int numFacture = Integer.parseInt(sc.nextLine().trim()) ;                                  
                	calculerPaiements(numFacture , true) ; 
                                                                           
                 }else if(choix.equals("5")){
                	
                    
                	System.out.print("Veuillez saisir le numéro de la facture : ");
                	int numFacture = Integer.parseInt(sc.nextLine().trim()) ;      
                	sc = new Scanner(System.in);
                	enregistrerPaiement(numFacture) ; 

                 }else if(choix.equals("6")){                    
                	 enregistreEvaluation(listSatisfactionData());
                	 
                 }

                afficheMenu();
                sc = new Scanner(System.in);
                choix = sc.nextLine();
            	
            } // while 

            // FIn de la boucle While - Fermeture de la connexion 
            if(fermetureConnexion()){
                System.out.println("Deconnection reussie...");
                
            }else{
                System.out.println("Échec ou Erreur lors de le déconnexion...");
            }
            
		 } else {  // if (connexion != null) {
			 
			 System.out.println("Echec de la Connection. Au revoir ! ");
			 
		 } // if (connexion != null) {	        
	} // main() 
}


// =============================================================================================
/**
 * Contient les données d'une évaluation d'un produit 
 * 
* @author Pamella Kissok
 * @author Inoussa Legrene
 * @author Amal Ben Abdellah
 * 
 * @version 2
 */
class SatisfactionData
{
	 int no_client ;
	String ref_produit ;
	int note ; 
	String commentaire ; 
	
	/**
	 * Constructeur
	 * 
	 * @param no_client
	 * @param ref_produit
	 * @param note
	 * @param commentaire
	 */
	public SatisfactionData(int no_client, String ref_produit, int note, String commentaire) {
		super();
		this.no_client = no_client;
		this.ref_produit = ref_produit;
		this.note = note;
		this.commentaire = commentaire;
	}	
}