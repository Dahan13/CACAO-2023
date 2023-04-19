package abstraction.eq4Transformateur1.Achat;

import java.util.List;

import abstraction.eq4Transformateur1.Transformateur1;
import abstraction.eqXRomu.bourseCacao.BourseCacao;

import abstraction.eqXRomu.bourseCacao.IAcheteurBourse;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.Gamme;
import abstraction.eqXRomu.produits.Lot;

public class AchatBourse extends CC_producteur implements IAcheteurBourse{

	public double demande(Feve f, double cours) {
		double solde = Filiere.LA_FILIERE.getBanque().getSolde(this, this.cryptogramme);
		double quantite=0;
		if (f.getGamme().equals(Gamme.BQ)) {
			double quantCC = 0;
			for (ExemplaireContratCadre c:this.ContratEnCours) {
				if (c.getProduit().equals(f)) {
					quantCC+=c.getQuantiteALivrerAuStep();
				}
			}
			for (ExemplaireContratCadre d:this.ContratEnCours) {
				if (d.getProduit().equals(Chocolat.C_BQ)) {
					quantite+=d.getQuantiteALivrerAuStep()/(this.pourcentageTransfo.get(Feve.F_BQ)).get(Chocolat.C_BQ); //quantite = venteBQ/ratioTransfo - Quant_CC_BQ
				}
			}
			quantite-=quantCC;
			if (this.stockChoco.get(Chocolat.C_BQ)==0) {
				quantite *= 1.2;
			}
			if (quantite*cours>solde) {
				quantite=solde*0.6/cours;
			}
		}
		double demande = quantite ;
		this.journal.ajouter(COLOR_LLGRAY, COLOR_LPURPLE,"   BOURSEA: demande en bourse de "+demande+" de "+f);
		return demande;
	}

	public void notificationAchat(Lot l, double coursEnEuroParT) {
		Feve f = (Feve)(l.getProduit());
		this.stockFeves.put(f,  this.stockFeves.get(f) + l.getQuantiteTotale());
		this.totalStocksFeves.ajouter(this,  l.getQuantiteTotale(), this.cryptogramme);
		this.journal.ajouter(COLOR_LLGRAY, COLOR_LPURPLE,"   BOURSEA: obtenu "+ l.getQuantiteTotale()+" T de "+f+" en bourse. Stock -> "+this.stockFeves.get(f));
	}

	public void notificationBlackList(int dureeEnStep) {
		this.journal.ajouter(" aie aie aie ... blackliste de la bourse pendant "+dureeEnStep+" tour");
	}
	
	public void next() {
		super.next();
	}
}
