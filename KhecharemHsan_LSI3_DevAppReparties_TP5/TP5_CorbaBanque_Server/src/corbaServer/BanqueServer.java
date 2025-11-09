package corbaServer;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import service.BanqueImpl;
import javax.naming.Context;
import javax.naming.InitialContext;

public class BanqueServer {

    public static void main(String[] args) {
        try {
            // 1. Initialisation de l'ORB
            System.out.println("Initialisation de l'ORB...");
            ORB orb = ORB.init(args, null);

            // 2. Récupération du RootPOA et activation du POAManager
            System.out.println("Récupération du POA...");
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            // 3. Création du servant (Objet distant)
            System.out.println("Création du servant...");
            BanqueImpl banqueImpl = new BanqueImpl();

            // 4. Enregistrement de la référence dans l'annuaire
            Context ctx = new InitialContext();
            ctx.rebind("BK",rootPOA.servant_to_reference(banqueImpl));

            System.out.println("Serveur Banque prêt et en attente de requêtes...");
            // 5. Mise en attente de l'ORB pour traiter les requêtes
            orb.run();

        } catch (Exception e) {
            System.err.println("ERREUR Serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}