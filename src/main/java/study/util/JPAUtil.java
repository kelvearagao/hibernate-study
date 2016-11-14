package study.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("exemploPU");
	
	public static EntityManager createEntityManager() {
		return emf.createEntityManager();
	}
	
	public static EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}
	
}