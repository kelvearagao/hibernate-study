package study.main;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import study.model.Cliente;
import study.util.JPAUtil;

public class ConsultaComCriteria {
	
	public static void main(String[] args) {
		EntityManager em = JPAUtil.createEntityManager();
		
		listaClientes(em);
		
		listaClientesPorNome(em);
		
		listaClientesPorParametro(em);
		
		em.close();
	}
	
	private static void listaClientes(EntityManager em) {
		// JPQL: from Cliente
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Cliente> cq = builder.createQuery(Cliente.class);
		cq.from(Cliente.class);
		
		TypedQuery<Cliente> query = em.createQuery(cq);
		List<Cliente> clientes = query.getResultList();
		
		for (Cliente cliente : clientes ) {
			System.out.println("Código: " + cliente.getCodigo());
			System.out.println("Nome: " + cliente.getNome());
		}
	}
	
	private static void listaClientesPorNome(EntityManager em) {
		// JPQ: select c from Cliente where c.nome like = 'Fernando%'
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Cliente> cq = builder.createQuery(Cliente.class);
		Root<Cliente> c = cq.from(Cliente.class);
		cq.select(c);
		cq.where(builder.like(c.<String>get("nome") ,"Fernando%"));
		
		TypedQuery<Cliente> query = em.createQuery(cq);
		List<Cliente> clientes = query.getResultList();
		
		for (Cliente cliente : clientes ) {
			System.out.println("Código: " + cliente.getCodigo());
			System.out.println("Nome: " + cliente.getNome());
		}
	}
	
	private static void listaClientesPorParametro(EntityManager em) {
		// JPQ: select c from Cliente c where c.idade between :idadeInicial and :idadeFinal and sexo = :sexo
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Cliente> cq = builder.createQuery(Cliente.class);
		Root<Cliente> c = cq.from(Cliente.class);
		cq.select(c);
		
		List<Predicate> predicates = new ArrayList<>();
		
		ParameterExpression<Integer> idadeInicial = builder.parameter(Integer.class, "idadeInicial");
		ParameterExpression<Integer> idadeFinal = builder.parameter(Integer.class, "idadeFinal");
		predicates.add(builder.between(c.<Integer>get("idade"), idadeInicial, idadeFinal));	    
		
		ParameterExpression<String> sexo = builder.parameter(String.class, "sexo");
		predicates.add(builder.equal(c.<String>get("sexo"), sexo));
		
		cq.where(predicates.toArray(new Predicate[0]));
		
		TypedQuery<Cliente> query = em.createQuery(cq);
		
		query.setParameter("idadeInicial", 12);
		query.setParameter("idadeFinal", 16);
		query.setParameter("sexo", "M");
		
		List<Cliente> clientes = query.getResultList();
		
		for (Cliente cliente : clientes ) {
			System.out.println("Código: " + cliente.getCodigo());
			System.out.println("Nome: " + cliente.getNome());
		}
	}

}