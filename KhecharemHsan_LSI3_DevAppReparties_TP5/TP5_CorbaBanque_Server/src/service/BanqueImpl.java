package service;

import corbaBanque.*;
import java.util.ArrayList;
import java.util.List;

public class BanqueImpl extends IBanqueRemotePOA {

    private List<Compte> comptes = new ArrayList<>();

    private static final double TAUX_CONVERSION = 3.3;

    @Override
    public void creerCompte(Compte cpte) {

        for (Compte c : comptes) {
            if (c.code == cpte.code) {
                System.out.println("Compte avec code " + cpte.code + " existe déjà!");
                return;
            }
        }
        comptes.add(cpte);
        System.out.println("Compte créé: Code=" + cpte.code + ", Solde=" + cpte.solde);
    }

    @Override
    public void verser(float mt, int code) {
        for (Compte c : comptes) {
            if (c.code == code) {
                c.solde += mt;
                System.out.println("Versement de " + mt + " sur compte " + code);
                System.out.println("Nouveau solde: " + c.solde);
                return;
            }
        }
        System.out.println("Compte " + code + " introuvable!");
    }

    @Override
    public void retirer(float mt, int code) {
        for (Compte c : comptes) {
            if (c.code == code) {
                if (c.solde >= mt) {
                    c.solde -= mt;
                    System.out.println("Retrait de " + mt + " du compte " + code);
                    System.out.println("Nouveau solde: " + c.solde);
                } else {
                    System.out.println("Solde insuffisant pour le compte " + code);
                }
                return;
            }
        }
        System.out.println("Compte " + code + " introuvable!");
    }

    @Override
    public Compte getCompte(int code) {
        for (Compte c : comptes) {
            if (c.code == code) {
                System.out.println("Consultation compte " + code + ": Solde=" + c.solde);
                return c;
            }
        }
        System.out.println("Compte " + code + " introuvable!");
        return new Compte(-1, 0);
    }

    @Override
    public Compte[] getComptes() {
        System.out.println("Consultation de tous les comptes (" + comptes.size() + " comptes)");
        Compte[] tabComptes = new Compte[comptes.size()];
        for (int i = 0; i < comptes.size(); i++) {
            tabComptes[i] = comptes.get(i);
        }
        return tabComptes;
    }

    @Override
    public double conversion(float mt) {
        double resultat = mt * TAUX_CONVERSION;
        System.out.println("Conversion: " + mt + "€ = " + resultat + " DT");
        return resultat;
    }
}