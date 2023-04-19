/**
 * 
 */
package abstraction.eq4Transformateur1.Achat;

import java.awt.Color;




import abstraction.eq4Transformateur1.Transformateur1Transformateur;
import abstraction.eq4Transformateur1.Produits.ChocolatDeMarque;
import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.Gamme;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.produits.Lot;

/**
 * @author francois/fouad/amine
 *
 */
public class CC_producteur extends Transformateur1Transformateur implements IAcheteurContratCadre{
	public boolean achete(IProduit produit) {
		if (produit instanceof Feve) {
		if (((Feve) produit).getGamme().equals(Gamme.BQ) || (((Feve) produit).getGamme().equals(Gamme.HQ)))  {
			this.journal.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCA : j'affirme acheter le produit "+produit);
			return true;
		}
		return false;
	}
		return false;
}
	
	public Echeancier propositionDeLAcheteur(ExemplaireContratCadre contrat) {
		Object produit = contrat.getProduit();
		double qfeve=0;
		int ventetotH = 0;
		int ventetotB = 0;
		for (abstraction.eqXRomu.produits.ChocolatDeMarque c : Filiere.LA_FILIERE.getChocolatsProduits()) {
			if (c.getGamme().equals(Gamme.HQ)){
				ventetotH += Filiere.LA_FILIERE.getVentes(c, Filiere.LA_FILIERE.getEtape() );
			}
			if (c.getGamme().equals(Gamme.BQ)){
				ventetotB += Filiere.LA_FILIERE.getVentes(c, Filiere.LA_FILIERE.getEtape() );
			} 
		}
		if (produit instanceof Feve) {
			switch ((Feve)produit) {
			case F_MQ  : return null;
			case F_MQ_BE :return null;
			
			case F_BQ : 
				
				if (this.stockFeves.keySet().contains(produit)) {
					qfeve= this.stockFeves.get(produit);
					if ((qfeve >= ventetotB/30)){
						return null;
					}
					else {this.journal.ajouter(COLOR_LLGRAY, COLOR_LBLUE, "  CCV : propAchat --> nouvel echeancier="+new Echeancier(contrat.getEcheancier().getStepDebut(), 15, qfeve/15.0));
					return new Echeancier(Filiere.LA_FILIERE.getEtape() + 1, 15, ventetotB/30);
					}
				}
			
			case F_HQ_BE :
				
				
			if (this.stockFeves.keySet().contains(produit)) {
				
				qfeve= this.stockFeves.get(produit);
				if ((qfeve >= ventetotH/30)){
					return null;
				}
				else {this.journal.ajouter(COLOR_LLGRAY, COLOR_LBLUE, "  CCV : propAchat --> nouvel echeancier="+new Echeancier(contrat.getEcheancier().getStepDebut(), 15, qfeve/15.0));
				return new Echeancier(Filiere.LA_FILIERE.getEtape() + 1, 15, ventetotH/30);
				}
			}
	 
	}}
		
		
		return null;
		}
	
	
	
	
	public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {
		
		Echeancier echeancier = contrat.getEcheancier();
		int duree = echeancier.getNbEcheances();
		double quantitetot = echeancier.getQuantiteTotale();
		
		int ventetotH = 0;
		int ventetotB = 0;
		for (abstraction.eqXRomu.produits.ChocolatDeMarque c : Filiere.LA_FILIERE.getChocolatsProduits()) {
			if (c.getGamme().equals(Gamme.HQ)){
				ventetotH += Filiere.LA_FILIERE.getVentes(c, Filiere.LA_FILIERE.getEtape() );
			}
			if (c.getGamme().equals(Gamme.BQ)){
				ventetotB += Filiere.LA_FILIERE.getVentes(c, Filiere.LA_FILIERE.getEtape() );
			} 
		}
		if (( duree >= 15) && ( quantitetot <= ventetotH) && ( quantitetot >= 10000)) {
			
			this.journal.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCA : j'accepte l'echeancier "+contrat.getEcheancier());
			return contrat.getEcheancier();
		}
		if (( duree >= 15) && ( quantitetot <= ventetotB) && ( quantitetot >= 10000)) {
			
			this.journal.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCA : j'accepte l'echeancier "+contrat.getEcheancier());
			return contrat.getEcheancier();
		}
		Feve f = (Feve) contrat.getProduit();
		if (f.getGamme().equals(Gamme.MQ)) {
			return null;
		}
		
		if (f.getGamme().equals(Gamme.HQ)) {
			Echeancier echeancier2 = new Echeancier(Filiere.LA_FILIERE.getEtape() + 1, 15, ventetotH/30);
			return echeancier2;
		}
		
		if (f.getGamme().equals(Gamme.BQ)) {
			Echeancier echeancier2 = new Echeancier(Filiere.LA_FILIERE.getEtape() + 1, 15, ventetotB/30);
			return echeancier2;
		}
		return null;
		
	}
	
	
	public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat) {
		double prix=0.0;
		double solde = Filiere.LA_FILIERE.getBanque().getSolde(this, this.cryptogramme);
		Object produit = contrat.getProduit();
		if (produit instanceof Feve) {
			switch ((Feve)produit) {
			case F_HQ_BE : prix= 3.525;break;
			case F_BQ : prix= 1.425;break;
			}
		}
		int nbPas=0;
		while (nbPas<30 && prix*contrat.getQuantiteTotale()>(solde/10.0)) {
			prix = 0.75*prix;
			nbPas++;
		};
		if (nbPas==30) {
			return 0.0;
		}
		prix = Math.min(prix, contrat.getPrix());
		this.journal.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCA : on me propose le prix "+contrat.getPrix()+" -> ma proposition ="+prix);
		return prix;
	}

	public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
		this.journal.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCA : nouveau cc conclu "+contrat);
	}

	public void receptionner(Lot lot, ExemplaireContratCadre contrat) {
		IProduit produit= lot.getProduit();
		double quantite = lot.getQuantiteTotale();
		if (produit instanceof Feve) {
			if (this.stockFeves.keySet().contains(produit)) {
				this.stockFeves.put((Feve)produit, this.stockFeves.get(produit)+quantite);
			} else {
				this.stockFeves.put((Feve)produit, quantite);
			}
			this.totalStocksFeves.ajouter(this, quantite, this.cryptogramme);
			this.journal.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCA : reception "+quantite+" T de feves "+produit+". Stock->  "+this.stockFeves.get(produit));
		} else {
			this.journal.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCA : reception d'un produit de type surprenant... "+produit);
		}
	}

	public int fixerPourcentageRSE(IAcheteurContratCadre acheteur, IVendeurContratCadre vendeur, IProduit produit,
			Echeancier echeancier, long cryptogramme, boolean tg) {
		return 10; // --> j'afficherai un taux de RSE de 10% sur mes chocolats de marque produits
	}

	public void next() {
		super.next();
	}
}
