package eu.scape_project.planning.efficiency;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;

import org.supercsv.cellprocessor.Optional;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.tree.Leaf;

public class CriteriaImpactAnalysis extends StatisticsGenerator {
	private Plan plan;

	private double actualScores[];
	private double calculatedScores[];

	private List<Leaf> allCriteria;
	private List<Leaf> criteriaSet;

	class HeuristicResult {

	}

	public CriteriaImpactAnalysis(Writer writer, EntityManager em)
			throws IOException {
		super(writer, em);
	}

	public void initialize(final Plan plan) throws IOException {
		this.plan = plan;

		int numAlternatives = plan.getAlternativesDefinition()
				.getAlternatives().size();

		actualScores = new double[numAlternatives];
		calculatedScores = new double[numAlternatives];

		allCriteria.addAll(plan.getTree().getRoot().getAllLeaves());

		// 1. Rank all criteria according to IMPACT descending
		Collections.sort(allCriteria, new Comparator<Leaf>() {
			@Override
			public int compare(Leaf leaf1, Leaf leaf2) {
				// TODO Auto-generated method stub
				return Double.compare(leaf2.getPotentialImpact(),
						leaf1.getPotentialImpact());
			}
		});

		// 2. Start with empty set of criteria
		criteriaSet = new ArrayList<Leaf>();

		setupColumns();

		// write the header
		listWriter.writeHeader(headers);
	}

	@Override
	public void writeStatistics(Plan plan) throws IOException {
		initialize(plan);

		calculateHeuristic();
	}

	private void calculateHeuristic() throws IOException {
		HeuristicResult result = new HeuristicResult();

		// 3. Repeat till set is full
		for (Leaf leaf : allCriteria) {
			// a. add top criterion to set
			criteriaSet.add(leaf);
			
			// b. populate values
			// c. Calculate preliminary ranking
			// d. compare to actual ranking and note whether identical. note scores.
		}

		listWriter.write(result, headers, processors);
		listWriter.flush();
	}

	@Override
	protected void setupColumns() {
		addColumn("id", new LUndef());
		addColumn("setSize", new LUndef());
		addColumn("numOfMeasurements", new LUndef());
		addColumn("match", new Optional());
		for (int i = 1; i < plan.getAlternativesDefinition().getAlternatives()
				.size(); i++) {
			addColumn("A" + i, new Optional());
		}
	}

}
