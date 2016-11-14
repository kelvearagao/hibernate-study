package study.criteria;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import study.model.Carro;
import study.model.Cliente;
import study.model.ModeloCarro;
import study.util.JPAUtil;

public class ExemplosCriteria {
	
	private static EntityManagerFactory factory;
	
	private EntityManager manager;
	
	@BeforeClass
	public static void init() {
		factory = JPAUtil.getEntityManagerFactory();
	}
	
	@Before
	public void setUp() {
		this.manager = factory.createEntityManager();
	}
	
	@After
	public void tearDown() {
		this.manager.close();
	}
	
	/**
	 * Projeções.
	 * 
	 */
	@Test
	public void projecoes() {
		// select c.profissao from Cliente c
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<String> criteriaQuery = builder.createQuery(String.class);
		
		Root<Cliente> cliente = criteriaQuery.from(Cliente.class);
		criteriaQuery.select(cliente.<String>get("profissao"));
		
		TypedQuery<String> query = manager.createQuery(criteriaQuery);
		List<String> profissoes = query.getResultList();
		
		for (String profissao : profissoes) {
			System.out.println(profissao);
		}
	}
	
	/**
	 * Funções de agregação.
	 * 
	 */
	@Test
	public void funcoesDeAgregacao() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = builder.createQuery(Integer.class);
		
		Root<Cliente> cliente = criteriaQuery.from(Cliente.class);
		criteriaQuery.select(builder.sum(cliente.<Integer>get("idade")));
		
		TypedQuery<Integer> query = manager.createQuery(criteriaQuery);
		Integer total = query.getSingleResult();
		
		System.out.println("Soma de todas as idades: " + total);
	}
	
	/**
	 * Resultados complexos, tuplas e construtores.
	 * 
	 */
	
	@Test
	public void resultadoComplexo() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Object[]> criteriaQuery = builder.createQuery(Object[].class);
		
		Root<Carro> carro = criteriaQuery.from(Carro.class);
		criteriaQuery.multiselect(carro.get("placa"), carro.get("valorDiaria"));
		
		TypedQuery<Object[]> query = manager.createQuery(criteriaQuery);
		List<Object[]> resultado = query.getResultList();
		
		for (Object[] valores : resultado) {
			System.out.println(valores[0] + " - " + valores[1]);
		}
	}
	
	@Test
	public void resultadoTupla() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = builder.createTupleQuery();
		
		Root<Carro> carro = criteriaQuery.from(Carro.class);
		criteriaQuery.multiselect(carro.get("placa").alias("placa"), carro.get("valorDiaria").alias("valorCarro"));
		
		TypedQuery<Tuple> query = manager.createQuery(criteriaQuery);
		List<Tuple> resultado = query.getResultList();
		
		for (Tuple tupla : resultado) {
			System.out.println(tupla.get("placa") + " - " + tupla.get("valorCarro"));
		}
	}
	
	@Test
	public void resultadoContrutores() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<PrecoCarro> criteriaQuery = builder.createQuery(PrecoCarro.class);
		
		Root<Carro> carro = criteriaQuery.from(Carro.class);
		criteriaQuery.select(builder.construct(PrecoCarro.class, carro.get("placa"), carro.get("valorDiaria")));
		
		TypedQuery<PrecoCarro> query = manager.createQuery(criteriaQuery);
		List<PrecoCarro> resultado = query.getResultList();
		
		for (PrecoCarro precoCarro : resultado) {
			System.out.println(precoCarro.getPlaca() + " - " + precoCarro.getValor());
		}
	}
	
	/**
	 * Funções
	 * 
	 */
	@Test
	public void exmploFuncao() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Carro> criteriaQuery = builder.createQuery(Carro.class);
		
		Root<Carro> carro = criteriaQuery.from(Carro.class);
		Predicate predicate = builder.equal(builder.upper(carro.get("cor")), "prata".toUpperCase());
		
		criteriaQuery.select(carro);
		criteriaQuery.where(predicate);
		
		TypedQuery<Carro> query = manager.createQuery(criteriaQuery);
		List<Carro> carros = query.getResultList();
		
		for (Carro c : carros) {
			System.out.println(c.getPlaca() + " - " + c.getCor());
		}
	}
	
	/**
	 * Ordenação de resultado.
	 * 
	 */
	@Test
	public void exemploOrdenação() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Carro> criteriaQuery = builder.createQuery(Carro.class);
		
		Root<Carro> carro = criteriaQuery.from(Carro.class);
		Order order = builder.desc(carro.get("valorDiaria"));
		
		criteriaQuery.select(carro);
		criteriaQuery.orderBy(order);
		
		TypedQuery<Carro> query = manager.createQuery(criteriaQuery);
		List<Carro> carros = query.getResultList();
		
		for (Carro c : carros) {
			System.out.println(c.getPlaca() + " - " + c.getValorDiaria());
		}
	}
	
	/**
	 * Join e Fetch.
	 * 
	 */
	
	@Test
	public void exemploJoin() {
		// select c from Carro c inner join c.modelo where c.modelo.descricao = "Fit" --Fazer um exemplo!
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Carro> criteriaQuery = builder.createQuery(Carro.class);
		
		Root<Carro> carro = criteriaQuery.from(Carro.class);
		
		// Não traz o objeto modelo
		Join<Carro, ModeloCarro> modelo = (Join) carro.join("modelo");
		
		criteriaQuery.select(carro);
		
		// Faz verificação no objeto modelo
		criteriaQuery.where(builder.equal(modelo.get("descricao"), "Fit"));
		
		TypedQuery<Carro> query = manager.createQuery(criteriaQuery);
		List<Carro> carros = query.getResultList();
		
		for (Carro c : carros) {
			System.out.println(c.getPlaca());
		}
	}
	
	@Test
	public void exemploFetch() {
		// select c from Carro c inner join fetch c.modelo --Fazer um exemplo!
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Carro> criteriaQuery = builder.createQuery(Carro.class);
		
		Root<Carro> carro = criteriaQuery.from(Carro.class);
		// traz o objeto modelo
		Join<Carro, ModeloCarro> modelo = (Join) carro.fetch("modelo");
		
		criteriaQuery.select(carro);
		
		TypedQuery<Carro> query = manager.createQuery(criteriaQuery);
		List<Carro> carros = query.getResultList();
		
		for (Carro c : carros) {
			System.out.println(c.getPlaca() + " - " + c.getModelo().getDescricao());
		}
	}
	
	/**
	 * Subquery.
	 *  
	 */
	
	@Test
	public void exemploMedia() {
		// consulta apenas a média sem subquery
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Double> criteriaQuery = builder.createQuery(Double.class);
		
		Root<Carro> carro = criteriaQuery.from(Carro.class);
		criteriaQuery.select(builder.avg(carro.<Double>get("valorDiaria")));
		
		TypedQuery<Double> query = manager.createQuery(criteriaQuery);
		Double total = query.getSingleResult();
		
		System.out.println("Média da diária: " + total);
	}
	
	@Test
	public void exemploSubquery() {
		// consulta a média por subquery
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Carro> criteriaQuery = builder.createQuery(Carro.class);
		Subquery<Double> subquery = criteriaQuery.subquery(Double.class);
		
		Root<Carro> carro = criteriaQuery.from(Carro.class);
		Root<Carro> carroSub = subquery.from(Carro.class);
		
		subquery.select(builder.avg(carroSub.<Double>get("valorDiaria")));
		
		criteriaQuery.select(carro);
		criteriaQuery.where(builder.greaterThanOrEqualTo(carro.<Double>get("valorDiaria"), subquery));
		
		TypedQuery<Carro> query = manager.createQuery(criteriaQuery);
		List<Carro> carros = query.getResultList();
		
		for (Carro c : carros) {
			System.out.println(c.getPlaca() + " - " + c.getValorDiaria());
		}
	}
}
