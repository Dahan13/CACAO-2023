package abstraction.eq7Distributeur1;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import abstraction.eq4Transformateur1.Achat.CC_producteur;
import abstraction.eq8Distributeur2.ContratCadre;
import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.produits.Lot;

public class DistributeurContratCadreAcheteur extends Distributeur1Stock implements IAcheteurContratCadre{
	protected List<ExemplaireContratCadre> mesContratEnTantQuAcheteur;
	protected List<ExemplaireContratCadre> historique_de_mes_contrats;
	protected SuperviseurVentesContratCadre superviseurVentesCC;
	private List<Object> negociations = new ArrayList<>();
	private double minNego=5;
	protected LinkedList<Integer> durees_CC ; 

	
	public void initialiser() {
		super.initialiser();
		this.superviseurVentesCC = (SuperviseurVentesContratCadre)(Filiere.LA_FILIERE.getActeur("Sup.CCadre"));
		initiate_durees();
	}
	
	public void initiate_durees(){
		this.durees_CC= new LinkedList<>();
//		durees_CC.add(24); //12 mois = 1an
//		durees_CC.add(18); //9 mois
		durees_CC.add(12); //6 mois
//		durees_CC.add(6); //3 mois
	}
	
	public DistributeurContratCadreAcheteur() {
		super();
		this.mesContratEnTantQuAcheteur=new LinkedList<ExemplaireContratCadre>();
	}

	/**
	 * @author Ghaly
	 */
	public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {
		if (Math.random()<0) {
			Echeancier e = contrat.getEcheancier();
			int stepdebut = e.getStepDebut();
			for (int step = stepdebut; step < e.getStepFin()+1; step++) {
				e.set(step, e.getQuantite(step)*0.95);
			}
			return e;
		}
		else {
			return contrat.getEcheancier();
		}
	}

	public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat) {
		ChocolatDeMarque marque = (ChocolatDeMarque) contrat.getProduit();
		if (nombre_achats.get(marque)==0) {
			
			return contrat.getPrix();		
		} 
//		else if ((cout_marque.get(marque)*1.5	< contrat.getPrix()) || (contrat.getPrix()<0.5*getCout_gamme(marque))) {
//			journal_achat.ajouter("le prix est hors prévision donc on a du refuser");
//			return 0.;
//		}
		else { //Negocier en fonction des couts moyens
				if (Math.random()<0.3) {
					return contrat.getPrix(); // on ne cherche pas a negocier dans 30% des cas
				} else {//dans 70% des cas on fait une contreproposition differente

			return contrat.getPrix()*0.95;// 5% de moins.
				}
		}
	}

	
    /**
     * 	enleve les contrats obsolete (nous pourrions vouloir les conserver pour "archive"...)
     * @author Ghaly sentissi & Romain
     */

	public void enleve_contrats_obsolete() {
		List<ExemplaireContratCadre> contratsObsoletes=new LinkedList<ExemplaireContratCadre>();
		for (ExemplaireContratCadre contrat : this.mesContratEnTantQuAcheteur) {
			if (contrat.getQuantiteRestantALivrer()==0.0 && contrat.getMontantRestantARegler()==0.0) {
				contratsObsoletes.add(contrat);
			}
		}
		this.mesContratEnTantQuAcheteur.removeAll(contratsObsoletes);
	}

	/**   
	 * proposition d'un contrat a un des vendeurs choisi aleatoirement
	 * @param produit le produit qu'on veut vendre
	 * @return le contrat s'il existe, sinon null
     * @author Ghaly sentissi
     */
	public ExemplaireContratCadre getContrat(IProduit produit,Echeancier e) {
		this.journal_achat.ajouter(Color.white,Color.black,"--------------------------------------------------------------------------------");
		this.journal_achat.ajouter(Color.gray, Color.BLACK,"Recherche de vendeur CC pour le produit : " + produit + "...");
		List<IVendeurContratCadre> vendeurs = superviseurVentesCC.getVendeurs(produit);
		ExemplaireContratCadre cc = null;
		
		if (vendeurs.isEmpty()) {
			cc_sans_vendeur++;
			journal_achat.ajouter(Color.orange,Color.black,"personne ne vend le produit "+produit);
		}
		
		//On parcourt tous les vendeurs aleatoirement
		while (!vendeurs.isEmpty() && cc == null) {
			IVendeurContratCadre vendeur = null;
			if (vendeurs.size()==1) {
				vendeur=vendeurs.get(0);
			} 
			else if (vendeurs.size()>1) {
				vendeur = vendeurs.get((int)( Math.random()*vendeurs.size()));
			}
			vendeurs.remove(vendeur);
			if (e.getQuantiteTotale()< quantite_min_cc) {
				return cc;
			}
			if (vendeur!=null) {
				this.journal_achat.ajouter("Tentative de négociation de contrat cadre avec "+vendeur.getNom()+" pour "+produit);
				cc = superviseurVentesCC.demandeAcheteur((IAcheteurContratCadre)this, (IVendeurContratCadre) vendeur, produit, e, cryptogramme,false);
				
				if (cc != null) { //si le contrat est signé 
			        mesContratEnTantQuAcheteur.add(cc);
					notificationNouveauContratCadre(cc);
					mesContratEnTantQuAcheteur.add(cc);
					cc_vendus.put((IActeur)vendeur, (cc_vendus.containsKey(vendeur))? cc_vendus.get(vendeur)+1 : 1);
					
			    } 
				else { //si le contrat est un echec
					cc_non_aboutis.put((IActeur)vendeur,(cc_non_aboutis.containsKey(vendeur))? cc_non_aboutis.get(vendeur)+1 : 1);
			        this.journal_achat.ajouter(Color.RED, Color.BLACK,"Echec de la négociation de contrat cadre avec "+vendeur.getNom()+" pour "+produit+"...");
				}
			}
			}
		if (cc ==null) {
			cc_sans_vendeur++;
			journal_achat.ajouter("On a cherché à établir un contrat cadre pour le produit "+produit+" de durée "+e.getNbEcheances()+ " mais aucune négociation n'a aboutie");
		}
	
		return cc;

	}

	/**
	 * @author Theo, Ghaly
	 * @param step : étape
	 * @return la quantité totale de livraisons d'un produit devant se faire livrer jusqu'à l'étape step
	 */
	public double getLivraison_periode(IProduit produit, int step ) {
		double somme = 0;
		for (ExemplaireContratCadre contrat : mesContratEnTantQuAcheteur) {
			if (contrat.getProduit() == produit) {
				somme += contrat.getEcheancier().getQuantiteJusquA(step);
			}
		}
		return somme;
	}
	
	/**
	 * @author ghaly
	 * @return la qte totale livree à ce tour
	 */
	public double getLivraisonEtape(ChocolatDeMarque marque, int step) {
		double somme = 0;
		for (ExemplaireContratCadre contrat : mesContratEnTantQuAcheteur) {
			if (contrat.getProduit() == marque) {
				somme += contrat.getEcheancier().getQuantite(step);
			}
		}
		return somme;
	}

	/**
	 * @author Ghaly & Theo
	 * @param d : nombre d'étapes 
	 * est appelée pour savoir si on a besoin d'un contrat cadre sur la durée d
	 * On lance un CC seulement si notre stock n'est pas suffisant sur la durée qui suit
	 */
	public boolean besoin_de_CC (int d,ChocolatDeMarque marque) {  
			double previsionannee = 0;
			int step= Filiere.LA_FILIERE.getEtape();
			for (int numetape = step+1; numetape < step+d ; numetape++ ) {
				previsionannee += previsionsperso.get(numetape%24).get(marque);
				}
			return (previsionannee > get_valeur(Var_Stock_choco, marque)+getLivraison_periode(marque, step + d)+ quantite_min_cc);
	};
	/**
	 * est appelée pour savoir si de combien on a besoin sur la durée d
	 * @param d : nombre d'étapes 
	 * @author Ghaly
	 */
	public double quantite_besoin_cc (int d,ChocolatDeMarque marque) {  
			double prevision = 0;
			int etape = Filiere.LA_FILIERE.getEtape();
			for (int numetape = etape+1; numetape < etape+d ; numetape++ ) {
				prevision += previsionsperso.get(numetape%24).get(marque);
				}
			return prevision - get_valeur(Var_Stock_choco, marque)-getLivraison_periode(marque, etape + d);
	};

	/**
	 * @author Theo
	 * @param stepDebut : debut de livraison
	 * @param d : nbr_etape
	 * @return echeancier sur d etapes, base sur les previsions de ventes
	 */
	public Echeancier echeancier_strat(int stepDebut, int d, ChocolatDeMarque marque) {
		Echeancier e = new Echeancier(stepDebut);
		int delai_livraison = 1;
		for (int etape = stepDebut+1; etape<stepDebut+d+1; etape++) {
			double q = 0.;
			if ((delai_livraison > 0) && (etape < stepDebut+1+delai_livraison)) { //On prevoit une plus grosse premiere livraison pour anticiper et respecter le decalage
				for (int i=stepDebut+1; i<stepDebut+1+delai_livraison; i++) {
					q += previsionsperso.get(i%24).get(marque) -getLivraisonEtape(marque, i) -get_valeur(Var_Stock_choco,marque)/d;
				}
			}
			else {
				q = previsionsperso.get((etape+delai_livraison)%24).get(marque) -getLivraisonEtape(marque, etape+delai_livraison) -get_valeur(Var_Stock_choco,marque)/d;
			}
			if (q>0) {
				e.ajouter(q);
			}
			else {
				e.ajouter(0.);
			}
		}
		if ((quantite_min_cc*0.9 < e.getQuantiteTotale()) && (e.getQuantiteTotale() < quantite_min_cc)) {
			for (int i=0; i<d; i++) {
				double qte = e.getQuantite(i);
				e.set(i, qte*1.1);
			}
		}
		return e;
	}
	
	/**
	 * @author Ghaly & Theo
	 */
	public void next() {
		Depense();

		enleve_contrats_obsolete();

		cc_sans_vendeur=0;
		for (ChocolatDeMarque marque : Filiere.LA_FILIERE.getChocolatsProduits()) {
			for (Integer d : durees_CC) {
				
			if(besoin_de_CC ( d,marque)) {	//On va regarder si on a besoin d'un nouveau contrat cadre pour chaque marque
							
//				Echeancier echeancier = new Echeancier(Filiere.LA_FILIERE.getEtape()+1, d, quantite_besoin_cc(d, marque)/d);
				Echeancier echeancier = echeancier_strat(Filiere.LA_FILIERE.getEtape()+1,d,marque);
				
				ExemplaireContratCadre cc = getContrat(marque,echeancier);
				if (cc!=null) {
					nombre_achats.replace(marque, nombre_achats.get(marque)+1);
					actualise_cout (marque, cc.getPrix()); 
					actualise_cout_marque(marque, cc.getPrix());
					break;

				}
				}
			}} 
		
		for (Map.Entry<IActeur, Integer> entry : cc_vendus.entrySet()) {
			IActeur key = entry.getKey();
            Integer value = entry.getValue();
            cc_vendus.put(key, 0); //on réinitialise la valeur pour le prochain tour
    		this.Bilan_achat.ajouter(COLOR_GREEN, Color.black,"le nombre de contrats_signés par "+key+" est de "+value);
        }
		for (Map.Entry<IActeur, Integer> entry : cc_non_aboutis.entrySet()) {
			IActeur key = entry.getKey();
            Integer value = entry.getValue();
            cc_non_aboutis.put(key, 0); //on réinitialise la valeur pour le prochain tour
            if(value!=0) {
    		this.Bilan_achat.ajouter(Color.RED, Color.black,"le nombre de contrats dont les négociations ont été rompues avec "+key+" est de "+value);
        }}
		this.Bilan_achat.ajouter(Color.PINK, Color.black,"le nombre de produits dont on a pas trouvé de vendeurs sur le marché est de "+cc_sans_vendeur);
		cc_sans_vendeur=0;

		super.next();

		}
		
	
	@Override
	/**
	 * @author Theo
	 */
	// A COMPLETER SI ASSEZ DE STOCK (appele si cc initie par vendeur)
	public boolean achete(IProduit produit) {
		if ((produit instanceof ChocolatDeMarque) && (besoin_de_CC (24,(ChocolatDeMarque)produit))) {
			return true;
		}
		return false;
	}
    /**
     * retourne l'étape de négociation en cours
     * @param contrat     
     * @author Ghaly sentissi
     */
	public Integer step_nego (ExemplaireContratCadre contrat) {
		return contrat.getListePrix().size()/2;
	}
	
//	public double contrePropositionPrixAcheteur_negociations(ExemplaireContratCadre contrat) {
//		int step_nego = step_nego ( contrat);
//		if (step_nego<minNego) {
//			return contrat.getPrix()*0.95
//		}
			
			
    /**
  
     * @author Ghaly sentissi
     */
	public void best_prix(IProduit produit, ExemplaireContratCadre contrat) {
		
		List<Object> list = new ArrayList<>();
		double prix_au_min_nego = contrat.getListePrix().get(2*(int)(minNego)-1);
		if (list.isEmpty()) {
			list.add(contrat.getVendeur());
			list.add(prix_au_min_nego);
			list.add(minNego);			
		}
		else {
			
//			if (Double.compare((double)(list.get(1)), prix_au_min_nego)) {
//				list.removeAll(list);
//				list.add(contrat.getVendeur());
//				list.add(prix_au_min_nego);
//				list.add(step_nego(contrat));		
//			}
	
		}
	}
//	public String meilleur_prix(Echeancier e,IProduit produit) {
//		HashMap<IActeur, Double> res= new HashMap<>();
//		int minNego=5;
//		
//		for (IActeur acteur : Filiere.LA_FILIERE.getActeurs()) {
//			if (acteur!=this && acteur instanceof IVendeurContratCadre && ((IVendeurContratCadre)acteur).vend(produit)) {
//				ExemplaireContratCadre cc = superviseurVentesCC.demandeAcheteur((IAcheteurContratCadre)this, (IVendeurContratCadre) acteur, produit,e, cryptogramme,false);
//				double prix_au_min_nego = cc.getListePrix().get(2*(int)(minNego)-1);
//				res.put(acteur, prix_au_min_nego);
//			} else {
//			}
//				return "0";
//				}
//		
//		return "pas de vendeur trouvé" ;
//	}
	
//	public static Integer obtenirValeurMinimale(HashMap<String, Integer> hashMap) {
//        Integer valeurMinimale = null;
//        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
//            Integer valeur = entry.getValue();
//            if (valeurMinimale == null || valeur < valeurMinimale) {
//                valeurMinimale = valeur;
//            }
//        }
//        return valeurMinimale;	}
	
	
	@Override
	/**
	 * @author Theo
	 * Actions necessaires pour actualiser/annoncer chaque reception de cc
	 */
	public void receptionner(Lot lot, ExemplaireContratCadre contrat) {
		IProduit produit= lot.getProduit();
		double quantite = lot.getQuantiteTotale();
		if (produit instanceof ChocolatDeMarque) {
			ChocolatDeMarque marque = (ChocolatDeMarque)produit;
			if (Var_Stock_choco.keySet().contains(produit)) {
				mettre_a_jour(Var_Stock_choco, marque, get_valeur(Var_Stock_choco, marque)+quantite);
			}
			else {
				Var_Stock_choco.put(marque, new Variable("le stock de la marque "+marque.getNom(), "le stock de la marque "+marque.getNom(), this, quantite));
			}
			this.totalStocks.ajouter(this, quantite, this.cryptogramme);
			this.journal_stock.ajouter("Reception de "+quantite+" T de "+produit+". Stock->  "+ get_valeur(Var_Stock_choco, (ChocolatDeMarque)(produit)));
		}
	}

	public String toString() {
		return this.getNom();
	}

	public int fixerPourcentageRSE(IAcheteurContratCadre acheteur, IVendeurContratCadre vendeur, IProduit produit,
			Echeancier echeancier, long cryptogramme, boolean tg) {
		return 5;
	}

	@Override
	public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
		//on separe 2 cas selon si c'est un tout nouvel achat, sinon on montre quel est le % par rapport aux cout moyen
		String prix = "";
		if (nombre_achats.get((ChocolatDeMarque)(contrat.getProduit()))!=0) {
			prix+=" ce qui equivaut à "+ Math.floor( contrat.getPrix()*100 /get_valeur(Var_Cout_Choco, (ChocolatDeMarque)( contrat.getProduit()))) + "% du prix de cout moyen ";
		}
		String message="contrat signé avec "+ contrat.getVendeur().getNom()+" pour "+contrat.getProduit().toString()+" à un prix de "+contrat.getPrix()+ prix+ " de durée "+contrat.getEcheancier();
		journal.ajouter(Color.GREEN, Color.BLACK,message);
		journal_achat.ajouter(Color.GREEN, Color.BLACK,message);
		journal_achat.ajouter(Color.white,Color.black,"--------------------------------------------------------------------------------");

		}
	/**
	 * @author Ahmed
	 * Actions pour avoir le montant des depenses du tour actuel
	 */
	public void Depense() {
		Double cont = 0.0;
		for (ExemplaireContratCadre contrat : this.mesContratEnTantQuAcheteur) {
			double val = contrat.getPaiementAEffectuerAuStep();
			if(val>0) {
				
			cont += val ;
			
			}
		depenses.setValeur(this, totalStocks.getValeur()*cout_stockage_distributeur.getValeur() + cout_main_doeuvre_distributeur.getValeur()*qte_totale_en_vente + cont);

	}
	
	}}
	







