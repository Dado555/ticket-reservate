package dao;

import model.Cancelation;


public interface CancelationDao extends GenericDao<Integer, Cancelation>{
	
	boolean add(Cancelation cancelation);

}
