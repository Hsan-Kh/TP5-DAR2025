package corbaClient;


import corbaBanque.*;
import javax.naming.Context;
import javax.naming.InitialContext;

public class BanqueClient {

    public static void main(String[] args) {
        try {
            // Récupérer les propriétés de l’annuaire JNDI
            Context ctx = new InitialContext();
            System.out.println("Connexion à l'annuaire JNDI...");

            //  Chercher la référence
            Object ref = ctx.lookup("BK");

            //  Convertir  la référence en objet CORBA (Création du Stub)
            IBanqueRemote stub=  IBanqueRemoteHelper.narrow((org.omg.CORBA.Object)ref);
            
            System.out.println("Connexion au service Banque réussie!\n");


            // Test 1: Création de comptes
            System.out.println("=== Test 1: Création de comptes ===");
            Compte compte1 = new Compte(1001, 1000.0f);
            Compte compte2 = new Compte(1002, 2500.0f);
            Compte compte3 = new Compte(1003, 500.0f);

            stub.creerCompte(compte1);
            stub.creerCompte(compte2);
            stub.creerCompte(compte3);
            System.out.println("3 comptes créés avec succès!\n");

            // Test 2: Consultation d'un compte
            System.out.println("=== Test 2: Consultation d'un compte ===");
            Compte c = stub.getCompte(1001);
            System.out.println("Compte " + c.code + " - Solde: " + c.solde + "€\n");

            // Test 3: Versement
            System.out.println("=== Test 3: Versement sur un compte ===");
            System.out.println("Versement de 500€ sur le compte 1001");
            stub.verser(500.0f, 1001);
            c = stub.getCompte(1001);
            System.out.println("Nouveau solde du compte 1001: " + c.solde + "€\n");

            // Test 4: Retrait
            System.out.println("=== Test 4: Retrait d'un compte ===");
            System.out.println("Retrait de 300€ du compte 1002");
            stub.retirer(300.0f, 1002);
            c = stub.getCompte(1002);
            System.out.println("Nouveau solde du compte 1002: " + c.solde + "€\n");

            // Test 5: Retrait avec solde insuffisant
            System.out.println("=== Test 5: Test de retrait avec solde insuffisant ===");
            System.out.println("Tentative de retrait de 10000€ du compte 1003");
            stub.retirer(1000.0f, 1003);
            c = stub.getCompte(1003);
            System.out.println("  Compte " + c.code + " - Solde: " + c.solde + "€");
            System.out.println();

            // Test 6: Conversion Euro vers Dinar Tunisien
            System.out.println("=== Test 6: Conversion de devises ===");
            float montantEuro = 100.0f;
            double montantDT = stub.conversion(montantEuro);
            System.out.println(montantEuro + "€ = " + montantDT + " DT\n");

            // Test 7: Consultation de tous les comptes
            System.out.println("=== Test 7: Liste de tous les comptes ===");
            Compte[] tousLesComptes = stub.getComptes();
            System.out.println("Nombre total de comptes: " + tousLesComptes.length);
            for (int i = 0; i < tousLesComptes.length; i++) {
                System.out.println("  Compte " + tousLesComptes[i].code +
                        " - Solde: " + tousLesComptes[i].solde + "€");
            }

            System.out.println("\n=== Tests terminés avec succès! ===");

        } catch (Exception e) {
            System.err.println("ERREUR Client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}