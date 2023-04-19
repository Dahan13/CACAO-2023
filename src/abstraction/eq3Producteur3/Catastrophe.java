package abstraction.eq3Producteur3;


import java.util.HashMap;
import java.util.Set;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Journal;


import abstraction.eqXRomu.produits.Feve;


public class Catastrophe extends Producteur3{
	
	/**
	 * @author BOCQUET Gabriel
	 * @param s
	 */
	public void Fire(String s) {
			Champs fields = this.getFields();
			HashMap<Integer,Integer> FieldsH = fields.getChamps().get("H");
			HashMap<Integer,Integer> FieldsM = fields.getChamps().get("M");
			double hectarMburnt = 0;
			double hectarHburnt = 0;
			Set<Integer> KeyM = FieldsM.keySet();
			Set<Integer> KeyH = FieldsH.keySet();
			Journal j = this.getJCatastrophe();
			if(s.equals("Big")) {
				for(Integer key : KeyM) {
					hectarMburnt += FieldsM.get(key)*0.5;
					FieldsM.put(key,(int) (FieldsM.get(key)*0.5));
				}
				j.ajouter(Color.gray, Color.black, hectarMburnt + " d'hectares de Moyenne Gamme d'arbres ont brulé");
				for(Integer key : KeyH) {
					hectarHburnt = FieldsH.get(key)*0.5;
					FieldsH.put(key,(int) (FieldsH.get(key)*0.5));
				}
				j.ajouter(Color.yellow, Color.black, hectarHburnt + " d'hectares de Haute Gamme d'arbres ont brulé");
			}
			else if(s.equals("Med")) {
				for(Integer key : KeyM) {
					hectarMburnt += FieldsM.get(key)*0.2;
					FieldsM.put(key,(int) (FieldsM.get(key)*0.8));
				}
				j.ajouter(Color.gray, Color.black, hectarMburnt + " d'hectares de Moyenne Gamme d'arbres ont brulé");
				for(Integer key : KeyH) {
					hectarHburnt = FieldsH.get(key)*0.2;
					FieldsH.put(key,(int) (FieldsH.get(key)*0.8));
				}
				j.ajouter(Color.yellow, Color.black, hectarHburnt + " d'hectares de Haute Gamme d'arbres ont brulé");
			}
			else if(s.equals("Lit")) {
				for(Integer key : KeyM) {
					hectarMburnt += FieldsM.get(key)*0.1;
					FieldsM.put(key,(int) (FieldsM.get(key)*0.9));
				}
				j.ajouter(Color.gray, Color.black, hectarMburnt + " d'hectares de Moyenne Gamme d'arbres ont brulé");
				for(Integer key : KeyH) {
					hectarHburnt = FieldsH.get(key)*0.1;
					FieldsH.put(key,(int) (FieldsH.get(key)*0.9));
				}
				j.ajouter(Color.yellow, Color.black, hectarHburnt + " d'hectares de Haute Gamme d'arbres ont brulé");
			}
		
	}


	/**
	 * @author NAVEROS Marine
	 */	
	public void Cyclone(String s) {
		Champs fields = this.getFields();
		HashMap<Integer,Integer> FieldH = fields.getChamps().get("H");
		HashMap<Integer, Integer> FieldM = fields.getChamps().get("M");
		double hectarDetruitH = 0;
		double hectarDetruitM=0;
		Set<Integer> KeysH = FieldH.keySet();
		Set<Integer> KeysM = FieldM.keySet();
		Journal j = this.getJCatastrophe();
		if (s.equals("H")) {
			for(Integer key: KeysH) {
				hectarDetruitH += FieldH.get(key)*(0+ Math.random()*0.3);
				FieldH.put(key, (int)(FieldH.get(key)*(0+ Math.random()*0.3)));	
			}
			j.ajouter(Color.yellow, Color.black, hectarDetruitH + "d'hectares de Haute Gamme qui ont été détruits");
			for (Integer key: KeysM) {
				hectarDetruitM += FieldM.get(key)*(0+ Math.random()*0.3);
				FieldM.put(key, (int)(FieldM.get(key)*(0+ Math.random()*0.3)));
			}
			j.ajouter(Color.gray, Color.black, hectarDetruitM+"d'hectares de Moyenne Gamme qui ont été détruits");
			
			
		}
		
		
		
		
		
	}
	

	//Pour modéliser la grève générale, on va considérer les champs qui ne sont pas récoltés seront une perte de fève

	//Pour modéliser la greve generale, on va considerer les champs qui ne sont pas recoltes seront une perte de fève

	protected void GreveGeneral() {
		//On a autant d'employé que d'hectare Utilise
		Integer nbrgreviste = (int) Math.round(super.getHectaresUt()*0.8);
		//on calcule le nombre ce qu'on aurait du produire avec ces employees
		Champs fields = super.getFields();
		HashMap<String, LinkedList<Integer>> Keys = fields.HarvestKeys(Filiere.LA_FILIERE.getEtape());
		LinkedList<Integer> quantitePerdues = fields.HarvestQuantityG(Filiere.LA_FILIERE.getEtape(),Keys, nbrgreviste);
		super.getStock().retirerVielleFeve(Feve.F_HQ_BE,quantitePerdues.get(0));
		super.getStock().retirerVielleFeve(Feve.F_HQ_BE,quantitePerdues.get(1));
		Journal j = super.getJCatastrophe();
		j.ajouter(Color.red, Color.black, "Il y a "+ nbrgreviste + " qui font grèves ");
		j.ajouter(Color.gray, Color.black, quantitePerdues.get(1) + " d'hectares de Feves Moyennes Gammes n'ont pas été récolté par les grévistes ");
		j.ajouter(Color.yellow, Color.black, quantitePerdues.get(0) + " d'hectares de Feves Hautes Gammes n'ont pas été récolté par les grévistes ");
		
		
	}

	
	
	
	
	
	
}
